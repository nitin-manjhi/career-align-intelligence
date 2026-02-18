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

    @Bean
    public RestClientCustomizer restClientCustomizer() {
        String configured = System.getenv("OLLAMA_API_KEY");
        return restClientBuilder -> restClientBuilder
                .defaultHeader("Authorization", configured)
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
