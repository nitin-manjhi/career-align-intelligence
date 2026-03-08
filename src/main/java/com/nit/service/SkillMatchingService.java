package com.nit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nit.domain.ResumeAnalysisDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SkillMatchingService {

    private final VectorStore vectorStore;
    private final ObjectMapper objectMapper;
    private final PromptLoaderService promptLoaderService;
    private final ExperienceMatchingService experienceMatchingService;

    public SkillMatchingService(
            VectorStore vectorStore,
            ObjectMapper objectMapper,
            PromptLoaderService promptLoaderService,
            ExperienceMatchingService experienceMatchingService) {
        this.vectorStore = vectorStore;
        this.objectMapper = objectMapper;
        this.promptLoaderService = promptLoaderService;
        this.experienceMatchingService = experienceMatchingService;
    }

    private VectorStore getVectorStore(ChatModel chatModel) {
        return vectorStore;
    }

    /**
     * Pipeline implementation for Skill Matching using RAG and Semantic Search.
     */
    public ResumeAnalysisDTO performSkillMatching(String resumeId, String resumeText, String jdText,
            ChatModel selectedChatModel) {
        log.info("Starting Skill Matching Pipeline for resumeId: {} using store: {}", resumeId,
                getVectorStore(selectedChatModel).getClass().getSimpleName());

        // 1 & 2. Chunking (skip parser since we have text)
        List<Document> splitDocuments = chunkText(resumeId, resumeText);

        // 3. Vectorization (Embedded once per upload logic)
        embedAndStore(resumeId, splitDocuments, selectedChatModel);

        // 4. Skill Extractor: Get technical skills from JD
        List<String> jdSkills = extractSkillsFromJd(jdText, selectedChatModel);

        // 5 & 6. Semantic Matching & Scoring
        return matchSkillsAndCalculateScore(resumeId, jdSkills, jdText, resumeText, selectedChatModel);
    }

    private List<Document> chunkText(String resumeId, String text) {
        log.debug("Chunking text for resumeId: {}...", resumeId);
        Document doc = new Document(text, Map.of("resume_id", resumeId));
        TokenTextSplitter splitter = new TokenTextSplitter(300, 50, 5, 100, true);
        return splitter.apply(List.of(doc));
    }

    private void embedAndStore(String resumeId, List<Document> documents, ChatModel selectedChatModel) {
        VectorStore vectorStore = getVectorStore(selectedChatModel);
        log.info("Generating embeddings and storing {} chunks for resumeId: {} in {}...",
                documents.size(), resumeId, vectorStore.toString());
        long startTime = System.currentTimeMillis();
        vectorStore.add(documents);
        log.info("Successfully stored embeddings in {}ms", System.currentTimeMillis() - startTime);
    }

    private List<String> extractSkillsFromJd(String jdText, ChatModel selectedChatModel) {
        log.debug("Extracting skills from JD using {}...", selectedChatModel.getClass().getSimpleName());
        ChatClient chatClient = ChatClient.create(selectedChatModel);

        String prompt = promptLoaderService.loadPrompt("jd-skills-extractor.st")
                .replace("{jdText}", jdText);

        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content()
                .replaceAll("```json", "").replaceAll("```", "").trim();

        try {
            Map<String, List<String>> result = objectMapper.readValue(response, Map.class);
            return result.getOrDefault("skills", List.of());
        } catch (Exception e) {
            log.error("Failed to parse JD skills", e);
            return List.of();
        }
    }

    private ResumeAnalysisDTO matchSkillsAndCalculateScore(String resumeId, List<String> jdSkills, String jdText,
            String resumeText, ChatModel selectedChatModel) {
        log.info("Matching {} extracted skills against vector store for resumeId: {}...", jdSkills.size(), resumeId);

        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        for (String skill : jdSkills) {
            String normalizedSkill = normalizeSkill(skill);
            log.debug("Searching for skill: '{}' (normalized: '{}')", skill, normalizedSkill);

            // Search with a slightly more lenient threshold for semantic matching
            SearchRequest request = SearchRequest.builder()
                    .query(normalizedSkill)
                    .topK(3)
                    .similarityThreshold(0.50)
                    .filterExpression("resume_id == '" + resumeId + "'")
                    .build();

            try {
                List<Document> results = getVectorStore(selectedChatModel).similaritySearch(request);
                if (!results.isEmpty()) {
                    log.debug("✅ Found match for '{}' with score: {}", skill,
                            results.get(0).getMetadata().get("distance"));
                    matched.add(skill);
                } else {
                    log.debug("❌ No match found for '{}'", skill);
                    missing.add(skill);
                }
            } catch (Exception e) {
                log.error("💥 Similarity search failed for skill: {}. Reason: {}", skill, e.getMessage());
                missing.add(skill);
            }
        }

        // 1. Semantic Matching Score (Existing logic)
        double semanticScore = jdSkills.isEmpty() ? 0 : (matched.size() * 100.0) / jdSkills.size();

        // 2. Keyword Matching Score (Exact string matches)
        double keywordScore = calculateKeywordScore(resumeText, jdSkills);

        // 3. Experience Matching Score (New module)
        ExperienceMatchingService.ExperienceResult expResult = experienceMatchingService.computeExperience(resumeText,
                jdSkills);

        // 4. Hybrid ATS Scoring Formula:
        // ATS_SCORE = 0.40 * keywordScore + 0.40 * semanticScore + 0.20 *
        // experienceScore
        int finalAtsScore = (int) Math.round(
                (0.40 * keywordScore) +
                        (0.40 * semanticScore) +
                        (0.20 * expResult.getExperienceScore()));

        log.info("ATS Scoring Summary [Keyword: {}%, Semantic: {}%, Experience: {}%] -> Final: {}%",
                Math.round(keywordScore), Math.round(semanticScore), expResult.getExperienceScore(), finalAtsScore);

        // Generate final analysis using LLM
        ResumeAnalysisDTO analysis = generateFinalAnalysis(matched, missing, finalAtsScore, jdText, resumeText,
                selectedChatModel);

        // Overwrite/Set calculated score components in DTO
        analysis.setScore(finalAtsScore);
        analysis.setKeywordScore(keywordScore);
        analysis.setSemanticScore(semanticScore);
        analysis.setExperienceScore((int) expResult.getExperienceScore());
        analysis.setSkillExperience(expResult.getSkillExperience());

        return analysis;
    }

    private double calculateKeywordScore(String resumeText, List<String> jdSkills) {
        if (resumeText == null || jdSkills == null || jdSkills.isEmpty())
            return 0;
        String lowerResume = resumeText.toLowerCase();
        long exactMatches = jdSkills.stream()
                .filter(skill -> lowerResume.contains(skill.toLowerCase()))
                .count();
        return (exactMatches * 100.0) / jdSkills.size();
    }

    private String normalizeSkill(String skill) {
        if (skill == null)
            return "";
        String s = skill.toLowerCase().trim();
        if (s.contains("node.js"))
            return "nodejs";
        if (s.contains("react.js"))
            return "reactjs";
        return skill;
    }

    private ResumeAnalysisDTO generateFinalAnalysis(List<String> matched, List<String> missing, int score,
            String jdText, String resumeText, ChatModel selectedChatModel) {
        log.info("Generating comprehensive final analysis report using {}...",
                selectedChatModel.getClass().getSimpleName());
        ChatClient chatClient = ChatClient.create(selectedChatModel);

        String promptTemplate = promptLoaderService.loadPrompt("analysis-prompt.st");

        String formatInstruction = """
                {
                  "score": integer (0-100),
                  "scoreExplanation": ["Reason 1", "Reason 2"],
                  "matchedSkills": ["Skill 1", "Skill 2"],
                  "missingSkills": ["Missing 1", "Missing 2"],
                  "improvementSuggestions": ["Fix 1", "Fix 2"],
                  "optimizedResume": "PROFESSIONAL_LAYOUT (Header\\n\\nEXPERIENCE\\n• Achievement 1)",
                  "structuredResume": {
                    "fullName": "Name", "title": "Title", "contact": "Phone | Email", "summary": "Full Summary", "skills": ["s1", "s2"], "workExperience": [{"title": "t", "company": "c", "date": "d", "points": ["p1"]}], "education": [{"title": "t", "college": "c", "date": "y", "location": "l"}]
                  }
                }
                """;

        String finalPrompt = promptTemplate
                .replace("{jdText}", jdText)
                .replace("{resumeText}", resumeText)
                .replace("{matchedSkills}", String.join(", ", matched))
                .replace("{missingSkills}", String.join(", ", missing))
                .replace("{formatInstruction}", formatInstruction);

        try {
            String jsonResponse = chatClient.prompt()
                    .user(finalPrompt)
                    .call()
                    .content()
                    .replaceAll("(?s)^.*?(\\{.*\\}).*$", "$1") // Extract JSON block securely
                    .trim();

            log.debug("Raw AI Final JSON: {}", jsonResponse);
            return objectMapper.readValue(jsonResponse, ResumeAnalysisDTO.class);
        } catch (Exception e) {
            log.error("💥 Rich analysis generation failed. Falling back to basic record.", e);
            ResumeAnalysisDTO fallback = new ResumeAnalysisDTO();
            fallback.setMatchedSkills(matched);
            fallback.setMissingSkills(missing);
            fallback.setScore(score);
            fallback.setScoreExplanation(List.of("Calculated based on semantic keyword density."));
            fallback.setImprovementSuggestions(List.of("Add missing keywords: " + String.join(", ", missing)));
            fallback.setOptimizedResume(resumeText);
            return fallback;
        }
    }
}
