package com.nit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ReactorClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class OllamaConfig {

    private final ObjectMapper objectMapper;

    @Value("${spring.app.ai.chat-provider:cloud}")
    private String chatProvider;

    @Value("${spring.ollama.cloud.chat-model:}")
    private String cloudChatModel;

    @Value("${spring.ollama.local.chat-model:}")
    private String localChatModel;

    @Bean
    public OllamaApi ollamaCloudApi(@Value("${spring.ollama.cloud.base-url:https://ollama.com}") String baseUrl,
                                    @Value("${spring.ollama.cloud.api-key:}") String apiKey) {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(120));

        RestClient.Builder customBuilder = RestClient.builder()
                .requestFactory(new ReactorClientHttpRequestFactory(httpClient))
                .defaultHeader("Authorization", apiKey)
                .messageConverters(converters -> {
                    converters.add(0, new MappingJackson2HttpMessageConverter(objectMapper));
                });
        return OllamaApi.builder()
                .baseUrl(baseUrl)
                .restClientBuilder(customBuilder)
                .build();
    }

    @Bean
    public OllamaApi ollamaLocalApi(@Value("${spring.ollama.local.base-url:http://localhost:11434}") String baseUrl) {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(120));

        RestClient.Builder customBuilder = RestClient.builder()
                .requestFactory(new ReactorClientHttpRequestFactory(httpClient))
                .messageConverters(converters -> {
                    converters.add(0, new MappingJackson2HttpMessageConverter(objectMapper));
                });
        return OllamaApi.builder()
                .baseUrl(baseUrl)
                .restClientBuilder(customBuilder)
                .build();
    }

    @Bean
    public OllamaChatModel ollamaChatModel(OllamaApi ollamaCloudApi, OllamaApi ollamaLocalApi) {
        boolean isCloud = "cloud".equalsIgnoreCase(chatProvider);
        OllamaApi targetApi = isCloud ? ollamaCloudApi : ollamaLocalApi;
        String modelName = isCloud ? cloudChatModel : localChatModel;

        return OllamaChatModel.builder()
                .ollamaApi(targetApi)
                .defaultOptions(OllamaChatOptions.builder()
                        .model(modelName)
                        .temperature(0.7)
                        .build())
                .build();
    }

    @Bean
    public ChatClient ollamaChatClient(OllamaChatModel ollamaChatModel) {
        return ChatClient.builder(ollamaChatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }
}
