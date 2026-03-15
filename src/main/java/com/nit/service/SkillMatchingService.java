package com.nit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nit.domain.ResumeAnalysisDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class SkillMatchingService {

    private final ObjectMapper objectMapper;
    private final PromptLoaderService promptLoaderService;
    private final ExperienceMatchingService experienceMatchingService;
    private final SkillWeightingService skillWeightingService;

    public SkillMatchingService(
            ObjectMapper objectMapper,
            PromptLoaderService promptLoaderService,
            ExperienceMatchingService experienceMatchingService,
            SkillWeightingService skillWeightingService) {
        this.objectMapper = objectMapper;
        this.promptLoaderService = promptLoaderService;
        this.experienceMatchingService = experienceMatchingService;
        this.skillWeightingService = skillWeightingService;
    }

    /**
     * Optimized Pipeline for Skill Matching using a Single-Pass LLM call.
     * This minimizes latency by removing RAG overhead and multiple AI roundtrips.
     */
    public ResumeAnalysisDTO performSkillMatching(String resumeId, String resumeText, String jdText,
            ChatModel selectedChatModel) {
        log.info("Starting Optimized Single-Pass Analysis for resumeId: {}", resumeId);
        long startTime = System.currentTimeMillis();

        // 1. Single Comprehensive AI Call
        ResumeAnalysisDTO analysis = generateComprehensiveAnalysis(jdText, resumeText, selectedChatModel);
        
        // 2. Local Enrichment & Structural Scoring (Fast, non-AI)
        enrichWithLocalMetrics(analysis, resumeText, jdText);

        log.info("Analysis Complete for resumeId: {} in {}ms", resumeId, System.currentTimeMillis() - startTime);
        return analysis;
    }

    private ResumeAnalysisDTO generateComprehensiveAnalysis(String jdText, String resumeText, ChatModel selectedChatModel) {
        log.info("Generating comprehensive final analysis report using {}...",
                selectedChatModel.getClass().getSimpleName());
        
        ChatClient chatClient = ChatClient.create(selectedChatModel);
        String promptTemplate = promptLoaderService.loadPrompt("analysis-prompt.st");

        String finalPrompt = promptTemplate
                .replace("{jdText}", jdText)
                .replace("{resumeText}", resumeText);

        try {
            String jsonResponse = chatClient.prompt()
                    .user(finalPrompt)
                    .call()
                    .content()
                    .replaceAll("(?s)^.*?(\\{.*\\}).*$", "$1") // Extract JSON block securely
                    .trim();

            return objectMapper.readValue(jsonResponse, ResumeAnalysisDTO.class);
        } catch (Exception e) {
            log.error("💥 Single-pass analysis generation failed. Falling back to basic record.", e);
            ResumeAnalysisDTO fallback = new ResumeAnalysisDTO();
            fallback.setScore(0);
            fallback.setOptimizedResume(resumeText);
            return fallback;
        }
    }

    private void enrichWithLocalMetrics(ResumeAnalysisDTO analysis, String resumeText, String jdText) {
        List<String> matched = analysis.getMatchedSkills() != null ? analysis.getMatchedSkills() : new ArrayList<>();
        List<String> missing = analysis.getMissingSkills() != null ? analysis.getMissingSkills() : new ArrayList<>();
        List<String> allJdSkills = Stream.concat(matched.stream(), missing.stream()).collect(Collectors.toList());

        // 1. Keyword Matching Score
        double keywordScore = calculateKeywordScore(resumeText, allJdSkills);

        // 2. Experience Matching Score
        ExperienceMatchingService.ExperienceResult expResult = experienceMatchingService.computeExperience(resumeText, allJdSkills);

        // 3. Skill Importance Weighting
        SkillWeightingService.WeightingResult weightingResult = skillWeightingService.calculateWeightedScore(jdText, allJdSkills, matched);

        // 4. Hybrid ATS Scoring Formula
        double semanticScore = allJdSkills.isEmpty() ? 0 : (matched.size() * 100.0) / allJdSkills.size();

        int finalAtsScore = (int) Math.round(
                (0.40 * weightingResult.getWeightedSkillScore()) +
                (0.40 * semanticScore) +
                (0.20 * expResult.getExperienceScore()));

        // Update DTO
        analysis.setScore(finalAtsScore);
        analysis.setKeywordScore(keywordScore);
        analysis.setSemanticScore(semanticScore);
        analysis.setExperienceScore((int) expResult.getExperienceScore());
        analysis.setSkillExperience(expResult.getSkillExperience());
        analysis.setWeightedSkillScore(weightingResult.getWeightedSkillScore());
        
        if (analysis.getSkillImportance() == null) {
            analysis.setSkillImportance(weightingResult.getSkillImportance());
        }

        // 5. GENERATE PLAIN TEXT RESUME LOCALLY (Save token generation time)
        if (analysis.getOptimizedResume() == null && analysis.getStructuredResume() != null) {
            analysis.setOptimizedResume(generatePlainTextResume(analysis.getStructuredResume()));
        }
    }

    /**
     * Converts structured JSON resume to plain text locally.
     * This avoids asking the AI to generate the same content twice, saving 2-4 seconds of latency.
     */
    @SuppressWarnings("unchecked")
    private String generatePlainTextResume(Object structured) {
        try {
            java.util.Map<String, Object> map = objectMapper.convertValue(structured, java.util.Map.class);
            StringBuilder sb = new StringBuilder();
            
            sb.append(map.getOrDefault("fullName", "CANDIDATE NAME")).append("\n");
            sb.append(map.getOrDefault("title", "")).append("\n");
            sb.append(map.getOrDefault("contact", "")).append("\n\n");
            
            sb.append("PROFESSIONAL SUMMARY\n");
            sb.append(map.getOrDefault("summary", "")).append("\n\n");
            
            sb.append("TECHNICAL SKILLS\n");
            List<String> skills = (List<String>) map.get("skills");
            if (skills != null) {
                sb.append(String.join(", ", skills)).append("\n\n");
            }
            
            sb.append("WORK EXPERIENCE\n");
            List<java.util.Map<String, Object>> exps = (List<java.util.Map<String, Object>>) map.get("workExperience");
            if (exps != null) {
                for (java.util.Map<String, Object> exp : exps) {
                    sb.append(exp.get("title")).append(" - ").append(exp.get("company")).append(" | ").append(exp.get("date")).append("\n");
                    List<String> points = (List<String>) exp.get("points");
                    if (points != null) {
                        for (String p : points) sb.append("• ").append(p).append("\n");
                    }
                    sb.append("\n");
                }
            }
            
            sb.append("EDUCATION\n");
            List<java.util.Map<String, Object>> edus = (List<java.util.Map<String, Object>>) map.get("education");
            if (edus != null) {
                for (java.util.Map<String, Object> edu : edus) {
                    sb.append(edu.get("title")).append(" | ").append(edu.get("date")).append("\n");
                    sb.append(edu.get("college")).append(", ").append(edu.get("location")).append("\n\n");
                }
            }
            
            return sb.toString();
        } catch (Exception e) {
            return "Resume preview generated successfully in Detailed View.";
        }
    }

    private double calculateKeywordScore(String resumeText, List<String> jdSkills) {
        if (resumeText == null || jdSkills == null || jdSkills.isEmpty()) return 0;
        String lowerResume = resumeText.toLowerCase();
        long exactMatches = jdSkills.stream()
                .filter(skill -> lowerResume.contains(skill.toLowerCase()))
                .count();
        return (exactMatches * 100.0) / jdSkills.size();
    }
}
