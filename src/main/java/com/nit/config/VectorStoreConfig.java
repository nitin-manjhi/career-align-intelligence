package com.nit.config;

import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class VectorStoreConfig {

    @Bean
    @Primary
    public VectorStore ollamaVectorStore(
            OllamaEmbeddingModel ollamaEmbeddingModel,
            JdbcTemplate jdbcTemplate) {
        return PgVectorStore.builder(jdbcTemplate, ollamaEmbeddingModel)
                .dimensions(1024)
                .vectorTableName("vector_store_ollama")
                .initializeSchema(true)
                .build();
    }
}
