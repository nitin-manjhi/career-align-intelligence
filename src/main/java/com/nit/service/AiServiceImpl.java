package com.nit.service;

import com.nit.domain.AIResponse;
import com.nit.domain.AIExtractionDTO;
import com.nit.domain.SkillCategoryResponse;
import com.nit.util.HashUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final ChatClient chatClient;
    private final ResultSaveService resultSaveService;
    private final PromptLoaderService promptLoaderService;

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CACHE_PREFIX_SKILLS = "categorizeSkills:";
    private static final Duration TTL = Duration.ofHours(1);

    @Override
    @Retryable(value = { Exception.class }, maxAttempts = 2, backoff = @Backoff(delay = 5000))
    public void analyzeResume(String resumeText, String jdText, UUID analysisId) {
        var converter = new BeanOutputConverter<>(AIExtractionDTO.class);
        String formatInstruction = converter.getFormat();

        String userPrompt = promptLoaderService.loadPrompt("code-generation-prompt.st")
                .replace("{resumeText}", resumeText)
                .replace("{jdText}", jdText)
                .replace("{formatInstruction}", formatInstruction);
        var chat = chatClient
                .prompt()
                .user(userPrompt)
                .call()
                .content();
        resultSaveService.saveResult(analysisId, chat);
    }

    @Override
    @Retryable(value = { Exception.class }, maxAttempts = 2, backoff = @Backoff(delay = 5000))
    public String categorizeSkills(java.util.List<String> skills) {

        String key = CACHE_PREFIX_SKILLS + HashUtil.sha256(skills.toString());

        // Check cache
        String cached = (String) redisTemplate.opsForValue().get(key);

        if (cached != null) {
            return cached; // cache hit → instant response
        }

        var converter = new BeanOutputConverter<>(SkillCategoryResponse.class);
        String formatInstruction = converter.getFormat();

        String userPrompt = promptLoaderService.loadPrompt("skill-categorization-prompt.st")
                .replace("{skills}", String.join(", ", skills))
                .replace("{formatInstruction}", formatInstruction);

        String aiResponse = chatClient
                .prompt()
                .user(userPrompt)
                .call()
                .content();
        // Store in Redis with TTL
        if (Objects.nonNull(aiResponse))
            redisTemplate.opsForValue().set(key, aiResponse, TTL);

        return aiResponse;
    }
}
