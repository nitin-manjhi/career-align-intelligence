package com.nit.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.boot.web.client.RestClientCustomizer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class OllamaConfig {

    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @org.springframework.beans.factory.annotation.Value("${ollama.api-key:}")
    private String ollamaApiKey;

    @Bean
    public RestClientCustomizer restClientCustomizer() {
        return restClientBuilder -> restClientBuilder
                .defaultHeader("Authorization", ollamaApiKey)
                .messageConverters(converters -> {
                    converters.add(0, new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter(
                            objectMapper));
                });
    }

    @Bean
    public ChatClient ollamaChatClient(ChatClient.Builder builder) {
        return builder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }
}
