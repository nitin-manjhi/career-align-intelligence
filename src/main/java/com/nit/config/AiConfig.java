package com.nit.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AiConfig {

    @Bean
    @Primary
    public ChatModel primaryChatModel(
            @Value("${spring.app.ai.chat-provider:cloud}") String chatProvider,
            @Qualifier("ollamaChatModel") ObjectProvider<ChatModel> ollamaChatModel,
            @Qualifier("openAiChatModel") ObjectProvider<ChatModel> openAiChatModel,
            @Qualifier("googleGenAiChatModel") ObjectProvider<ChatModel> googleGenAiChatModel) {

        if ("openrouter".equalsIgnoreCase(chatProvider) || "openai".equalsIgnoreCase(chatProvider)) {
            return openAiChatModel.getIfAvailable();
        } else if ("gemini".equalsIgnoreCase(chatProvider)) {
            return googleGenAiChatModel.getIfAvailable();
        }
        // Default to Ollama (cloud or local is handled in OllamaConfig)
        return ollamaChatModel.getIfAvailable();
    }

    @Bean
    @Primary
    public EmbeddingModel primaryEmbeddingModel(
            @Qualifier("ollamaEmbeddingModel") ObjectProvider<EmbeddingModel> ollamaEmbeddingModel) {
        return ollamaEmbeddingModel.getIfAvailable();
    }
}
