package com.nit.config;

import org.springframework.ai.embedding.EmbeddingModel;
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
    public VectorStore primaryVectorStore(
            EmbeddingModel primaryEmbeddingModel,
            JdbcTemplate jdbcTemplate) {
        return PgVectorStore.builder(jdbcTemplate, primaryEmbeddingModel)
                .dimensions(1024)
                .vectorTableName("vector_store")
                .initializeSchema(true)
                .build();
    }
}
