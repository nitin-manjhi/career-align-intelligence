package com.nit.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTemplateConfig {
    @Value("${kafka.topic.analysis-job-topic}")
    private String KAFKA_ANALYSIS_JOB_TOPIC;

    @Bean
    public NewTopic analysisJobTopic() {
        return new NewTopic(KAFKA_ANALYSIS_JOB_TOPIC, 1, (short) 1);
    }
}
