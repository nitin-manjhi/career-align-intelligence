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
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AiConfig.class);

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
            @Value("${spring.app.ai.chat-provider:cloud}") String chatProvider,
            @Value("${spring.app.ai.embedding-provider:local}") String embeddingProvider,
            @Qualifier("ollamaEmbeddingModel") ObjectProvider<EmbeddingModel> ollamaEmbeddingModel,
            ObjectProvider<EmbeddingModel> allEmbeddingModels) {

        boolean useGemini = "gemini".equalsIgnoreCase(chatProvider) || "gemini".equalsIgnoreCase(embeddingProvider);

        if (useGemini) {
            EmbeddingModel model = allEmbeddingModels.stream()
                    .filter(m -> m.getClass().getName().toLowerCase().contains("googlegenai") || 
                                m.getClass().getSimpleName().toLowerCase().contains("googlegenai"))
                    .findFirst()
                    .orElse(null);
            
            if (model == null) {
                log.warn("Gemini requested but no GoogleGenAiEmbeddingModel bean found! Falling back to Ollama.");
                return ollamaEmbeddingModel.getIfAvailable();
            }
            return model;
        }
        return ollamaEmbeddingModel.getIfAvailable();
    }
}
