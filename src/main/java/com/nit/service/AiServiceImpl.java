package com.nit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nit.domain.AIResponse;
import com.nit.domain.EmailTemplate;
import com.nit.domain.ResumeAnalysisDTO;
import com.nit.domain.SkillCategoryResponse;
import com.nit.entity.Role;
import com.nit.util.HashUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class AiServiceImpl implements AiService {

    private final ChatModel primaryChatModel; // Added primary model
    private final ChatModel ollamaChatModel;
    private final ChatModel openAiChatModel;
    private final ChatModel googleGenAiChatModel;

    private final ResultSaveService resultSaveService;
    private final PromptLoaderService promptLoaderService;
    private final SimpMessagingTemplate messagingTemplate;
    private final com.nit.repository.UserRepository userRepository;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CACHE_PREFIX_SKILLS = "categorizeSkills:";
    private static final Duration TTL = Duration.ofHours(1);

    private final SkillMatchingService skillMatchingService;

    public AiServiceImpl(
            ChatModel primaryChatModel, // Inject primary
            @Qualifier("ollamaChatModel") ChatModel ollamaChatModel,
            @Qualifier("openAiChatModel") ChatModel openAiChatModel,
            @Qualifier("googleGenAiChatModel") ChatModel googleGenAiChatModel,
            ResultSaveService resultSaveService,
            PromptLoaderService promptLoaderService,
            SimpMessagingTemplate messagingTemplate,
            RedisTemplate<String, Object> redisTemplate,
            ObjectMapper objectMapper,
            com.nit.repository.UserRepository userRepository,
            SkillMatchingService skillMatchingService) {
        this.primaryChatModel = primaryChatModel;
        this.ollamaChatModel = ollamaChatModel;
        this.openAiChatModel = openAiChatModel;
        this.googleGenAiChatModel = googleGenAiChatModel;
        this.resultSaveService = resultSaveService;
        this.promptLoaderService = promptLoaderService;
        this.messagingTemplate = messagingTemplate;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.skillMatchingService = skillMatchingService;
    }

    @Override
    @Retryable(value = { Exception.class }, maxAttempts = 2, backoff = @Backoff(delay = 5000))
    public void analyzeResume(String resumeText, String jdText, UUID resultId, UUID jobId, Long userId, String model) {
        long startTime = System.currentTimeMillis();
        ChatModel chatModel = selectChatModel(model, userId);

        ChatClient chatClient = ChatClient.builder(chatModel).build();

        // 1. Analysis Step
        notifyProgress(jobId, userId, "Analysis: ⏳ | Docs: ⚪", 10, null);
        long analysisStart = System.currentTimeMillis();
        var analysis = performAnalysis(chatModel, resumeText, jdText, resultId.toString());
        long analysisDuration = System.currentTimeMillis() - analysisStart;

        // Increment usage after successful Step 1 (Heavy AI work done)
        userRepository.findById(userId).ifPresent(user -> {
            if (chatModel != ollamaChatModel) {
                user.setPremiumUsageCount(user.getPremiumUsageCount() + 1);
                log.info("Incremented PREMIUM usage for user {}. Count: {}", userId, user.getPremiumUsageCount());
            } else {
                user.setAnalysisCount(user.getAnalysisCount() + 1);
                log.info("Incremented STANDARD usage for user {}. Count: {}", userId, user.getAnalysisCount());
            }
            userRepository.save(user);
        });

        savePartialResult(resultId, analysis);
        notifyProgress(jobId, userId, "Analysis: ✅ | Docs: ⏳", 40, resultId);

        // Run Step 2 and 3 in parallel with individual updates
        long parallelStart = System.currentTimeMillis();

        var coverLetterFuture = CompletableFuture.runAsync(() -> {
            String cl = generateCoverLetter(chatClient, resumeText, jdText, analysis);
            updateCoverLetter(resultId, cl);
            notifyDocProgress(jobId, userId, resultId, "Docs: Cover Letter ✅ | Email: ⏳", 70);
        });

        var emailFuture = CompletableFuture.runAsync(() -> {
            EmailTemplate em = generateEmail(chatClient, resumeText, jdText, analysis);
            updateEmail(resultId, em);
            notifyDocProgress(jobId, userId, resultId, "Docs: Cover Letter ⏳ | Email: ✅", 85);
        });

        CompletableFuture.allOf(coverLetterFuture, emailFuture).join();
        long parallelDuration = System.currentTimeMillis() - parallelStart;

        long totalTime = System.currentTimeMillis() - startTime;
        log.info("Analysis Job {} complete Task by Task. Total Time: {}ms, Analysis: {}ms, DocGen: {}ms.",
                jobId, totalTime, analysisDuration, parallelDuration);

        notifyDocProgress(jobId, userId, resultId, "All Tasks: ✅", 100);
    }

    private void updateCoverLetter(UUID resultId, String cl) {
        synchronized (this) {
            AIResponse response = resultSaveService.getResult(resultId);
            response.setCoverLetter(cl);
            persistResult(resultId, response);
        }
    }

    private void updateEmail(UUID resultId, EmailTemplate em) {
        synchronized (this) {
            AIResponse response = resultSaveService.getResult(resultId);
            response.setEmail(em);
            persistResult(resultId, response);
        }
    }

    private void notifyDocProgress(UUID jobId, Long userId, UUID resultId, String msg, int progress) {
        AIResponse latest = resultSaveService.getResult(resultId);
        String finalMsg = "Analysis: ✅ | " + msg;
        // Special logic: if both are done, show both ✅
        if (latest.getCoverLetter() != null && latest.getEmail() != null) {
            finalMsg = "Analysis: ✅ | Documents: ✅";
        } else if (latest.getCoverLetter() != null) {
            finalMsg = "Analysis: ✅ | CL: ✅ | Email: ⏳";
        } else if (latest.getEmail() != null) {
            finalMsg = "Analysis: ✅ | CL: ⏳ | Email: ✅";
        }
        notifyProgress(jobId, userId, finalMsg, progress, resultId);
    }

    private ChatModel selectChatModel(String model, Long userId) {
        if (model == null)
            return primaryChatModel;

        String modelLower = model.toLowerCase();

        if ("openai".equals(modelLower) || "openrouter".equals(modelLower)) {
            return openAiChatModel;
        }
        if ("gemini".equals(modelLower)) {
            return googleGenAiChatModel;
        }
        return ollamaChatModel;
    }

    private void checkKeyConfigured(String envVar, String property) {
        // Simple check: if the bean exists but the key is placeholder, it means it's
        // not actually usable
        // We can check the environment/properties directly if needed, but for now we
        // rely on the placeholder check
    }

    private ResumeAnalysisDTO performAnalysis(ChatModel selectedChatModel, String resume, String jd, String resultId) {
        return skillMatchingService.performSkillMatching(resultId, resume, jd, selectedChatModel);
    }

    private String generateCoverLetter(ChatClient client, String resume, String jd, ResumeAnalysisDTO analysis) {
        String prompt = promptLoaderService.loadPrompt("cover-letter-prompt.st")
                .replace("{resumeText}", resume)
                .replace("{jdText}", jd)
                .replace("{score}", String.valueOf(analysis.getScore()))
                .replace("{matchedSkills}", String.join(", ", analysis.getMatchedSkills()));

        return client.prompt().user(prompt).call().content();
    }

    private EmailTemplate generateEmail(ChatClient client, String resume, String jd, ResumeAnalysisDTO analysis) {
        var converter = new BeanOutputConverter<>(EmailTemplate.class);
        String prompt = promptLoaderService.loadPrompt("email-prompt.st")
                .replace("{resumeText}", resume)
                .replace("{jdText}", jd)
                .replace("{score}", String.valueOf(analysis.getScore()))
                .replace("{matchedSkills}", String.join(", ", analysis.getMatchedSkills()));

        String response = client.prompt().user(prompt).call().content();
        return converter.convert(response);
    }

    private void savePartialResult(UUID resultId, ResumeAnalysisDTO analysis) {
        AIResponse response = new AIResponse();
        response.setScore(analysis.getScore());
        response.setScoreExplanation(analysis.getScoreExplanation());
        response.setMatchedSkills(analysis.getMatchedSkills());
        response.setMissingSkills(analysis.getMissingSkills());
        response.setImprovementSuggestions(analysis.getImprovementSuggestions());
        response.setOptimizedResume(analysis.getOptimizedResume());

        // Hybrid ATS scoring and Experience components
        response.setSkillExperience(analysis.getSkillExperience());
        response.setExperienceScore(analysis.getExperienceScore());
        response.setKeywordScore(analysis.getKeywordScore());
        response.setSemanticScore(analysis.getSemanticScore());

        // Structured Layout for UI rendering
        response.setStructuredResume(analysis.getStructuredResume());

        persistResult(resultId, response);
    }

    private synchronized void persistResult(UUID resultId, AIResponse response) {
        try {
            String json = objectMapper.writeValueAsString(response);
            resultSaveService.saveResult(resultId, json);
        } catch (JsonProcessingException e) {
            log.error("Failed to persist result", e);
        }
    }

    private void notifyProgress(UUID jobId, Long userId, String message, int progress, UUID resultId) {
        String destination = "/topic/notifications-" + userId;
        Map<String, Object> payload = new java.util.HashMap<>(Map.of(
                "jobId", jobId,
                "message", message,
                "progress", progress,
                "type", "PROGRESS"));
        if (resultId != null) {
            payload.put("resultId", resultId);
        }
        messagingTemplate.convertAndSend(destination, payload);
    }

    @Override
    @Retryable(value = { Exception.class }, maxAttempts = 2, backoff = @Backoff(delay = 5000))
    public String categorizeSkills(java.util.List<String> skills) {
        String key = CACHE_PREFIX_SKILLS + HashUtil.sha256(skills.toString());
        String cached = (String) redisTemplate.opsForValue().get(key);
        if (cached != null)
            return cached;

        var chatClient = ChatClient.builder(ollamaChatModel).build();
        var converter = new BeanOutputConverter<>(SkillCategoryResponse.class);
        String userPrompt = promptLoaderService.loadPrompt("skill-categorization-prompt.st")
                .replace("{skills}", String.join(", ", skills))
                .replace("{formatInstruction}", converter.getFormat());

        String aiResponse = chatClient.prompt().user(userPrompt).call().content();
        if (Objects.nonNull(aiResponse))
            redisTemplate.opsForValue().set(key, aiResponse, TTL);

        return aiResponse;
    }
}
