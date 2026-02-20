package com.nit.service;

import com.nit.domain.PromptUtils;
import com.nit.util.HashUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final ChatClient chatClient;
    private final ResultSaveService resultSaveService;

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CACHE_PREFIX_SKILLS = "categorizeSkills:";
    private static final Duration TTL = Duration.ofHours(1);

    @Override
    public void analyzeResume(String resumeText, String jdText, UUID analysisId) {

        String userPrompt = PromptUtils.CODE_GENERATION_SYSTEM_PROMPT
                .replace("{{resumeText}}", resumeText)
                .replace("{{jdText}}", jdText);
        var chat = chatClient
                .prompt()
                .user(userPrompt)
                .call()
                .content();
        resultSaveService.saveResult(analysisId, chat);
    }

    @Override
    public String categorizeSkills(java.util.List<String> skills) {

        String key = CACHE_PREFIX_SKILLS + HashUtil.sha256(skills.toString());

        // Check cache
        String cached =
                (String) redisTemplate.opsForValue().get(key);

        if (cached != null) {
            return cached; // cache hit → instant response
        }


        String userPrompt = PromptUtils.SKILL_CATEGORIZATION_PROMPT
                .replace("{{skills}}", String.join(", ", skills));

        String aiResponse =  chatClient
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
