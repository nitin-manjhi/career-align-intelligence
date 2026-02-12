package com.nit.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OllamaConfig {
    @Bean
    public RestClientCustomizer restClientCustomizer() {
        String configured = System.getenv("OLLAMA_API_KEY");
        return restClientBuilder -> restClientBuilder
                .defaultHeader("Authorization", configured);
    }

    @Bean
    public ChatClient ollamaChatClient(ChatClient.Builder builder) {
        return builder
                .build();
    }
}
