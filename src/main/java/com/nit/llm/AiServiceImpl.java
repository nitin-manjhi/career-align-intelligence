package com.nit.llm;

import com.nit.domain.AIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final ChatClient chatClient;
    private final ResultSaveService resultSaveService;

    /**
     * @param resumeText
     * @param jdText
     * @return
     */
    @Override
    public AIResponse analyzeResume(String resumeText, String jdText) {

        String userPrompt = PromptUtils.CODE_GENERATION_SYSTEM_PROMPT
                .replace("{{resumeText}}", resumeText)
                .replace("{{jdText}}", jdText);
        var chat = chatClient
                .prompt()
                .user(userPrompt)
                .call()
                .content();
        AIResponse response = resultSaveService.saveResult(resumeText, jdText, chat);
        return response;
    }
}
