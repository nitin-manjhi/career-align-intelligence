package com.nit.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class VectorStoreConfig {
    private static final Logger log = LoggerFactory.getLogger(VectorStoreConfig.class);

    @Bean
    public VectorStore ollamaVectorStore(
            @Qualifier("ollamaEmbeddingModel") EmbeddingModel embeddingModel,
            JdbcTemplate jdbcTemplate) {
        log.info("Creating ollamaVectorStore with model: {}", embeddingModel.getClass().getName());
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .dimensions(1024)
                .vectorTableName("vector_store_ollama")
                .initializeSchema(true)
                .build();
    }

    @Bean
    public VectorStore googleVectorStore(
            ObjectProvider<EmbeddingModel> embeddingModelProvider,
            JdbcTemplate jdbcTemplate) {

        EmbeddingModel googleModel = embeddingModelProvider.stream()
                .filter(m -> m.getClass().getName().toLowerCase().contains("googlegenai") ||
                            m.getClass().getSimpleName().toLowerCase().contains("googlegenai"))
                .findFirst()
                .orElse(null);

        if (googleModel == null) {
            log.warn("Google EmbeddingModel bean not found! Gemini vector store will NOT be available.");
            return null;
        }

        log.info("Creating googleVectorStore with model: {}", googleModel.getClass().getName());
        return PgVectorStore.builder(jdbcTemplate, googleModel)
                .dimensions(768)
                .vectorTableName("vector_store_google")
                .initializeSchema(true)
                .build();
    }

    @Bean
    public VectorStore openAiVectorStore(
            ObjectProvider<EmbeddingModel> embeddingModelProvider,
            JdbcTemplate jdbcTemplate) {

        EmbeddingModel openAiModel = embeddingModelProvider.stream()
                .filter(m -> m.getClass().getName().toLowerCase().contains("openai") ||
                            m.getClass().getSimpleName().toLowerCase().contains("openai"))
                .findFirst()
                .orElse(null);

        if (openAiModel == null) {
            log.warn("OpenAI EmbeddingModel bean not found! OpenAI vector store will NOT be available.");
            return null;
        }

        log.info("Creating openAiVectorStore with model: {}", openAiModel.getClass().getName());
        return PgVectorStore.builder(jdbcTemplate, openAiModel)
                .dimensions(768)
                .vectorTableName("vector_store_openai")
                .initializeSchema(true)
                .build();
    }

    @Bean
    @Primary
    public VectorStore primaryVectorStore(
            @Value("${spring.app.ai.chat-provider:cloud}") String chatProvider,
            @Qualifier("ollamaVectorStore") VectorStore ollamaVectorStore,
            @Qualifier("googleVectorStore") ObjectProvider<VectorStore> googleVectorStore,
            @Qualifier("openAiVectorStore") ObjectProvider<VectorStore> openAiVectorStore) {

        if ("gemini".equalsIgnoreCase(chatProvider)) {
            return googleVectorStore.getIfAvailable(() -> ollamaVectorStore);
        }
        if ("openai".equalsIgnoreCase(chatProvider) || "openrouter".equalsIgnoreCase(chatProvider)) {
            return openAiVectorStore.getIfAvailable(() -> ollamaVectorStore);
        }
        return ollamaVectorStore;
    }
}
