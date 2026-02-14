package com.nit.service;

import com.nit.domain.AIResponse;
import com.nit.domain.PromptResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final ChatClient chatClient;
    private final ResultSaveService resultSaveService;

    @Override
    public AIResponse analyzeResume(String resumeText, String jdText) {

/*        String userPrompt = PromptUtils.CODE_GENERATION_SYSTEM_PROMPT
                .replace("{{resumeText}}", resumeText)
                .replace("{{jdText}}", jdText);
        var chat = chatClient
                .prompt()
                .user(userPrompt)
                .call()
                .content();*/
        var chat = PromptResponse.RESPONSE;
        return resultSaveService.saveResult(resumeText, jdText, chat);
    }
}
