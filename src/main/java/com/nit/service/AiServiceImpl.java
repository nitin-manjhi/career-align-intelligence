package com.nit.service;

import com.nit.domain.PromptUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final ChatClient chatClient;
    private final ResultSaveService resultSaveService;

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
        String userPrompt = PromptUtils.SKILL_CATEGORIZATION_PROMPT
                .replace("{{skills}}", String.join(", ", skills));

        return chatClient
                .prompt()
                .user(userPrompt)
                .call()
                .content();
    }
}
