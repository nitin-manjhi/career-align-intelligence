package com.nit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducerService {
    @Value("${kafka.topic.analysis-job-topic}")
    private String KAFKA_RESUME_ANALYSIS_JOB_TOPIC;

    @Value("${kafka.topic.skill-analysis-job-topic}")
    private String KAFKA_SKILL_ANALYSIS_JOB_TOPIC;

    private final KafkaTemplate<String, String> kafkaTemplate;


    public void publishJobForResumeAnalysis(UUID jobId) {
        log.info("PRODUCING EVENT to topic: {}", jobId);
        kafkaTemplate.send(KAFKA_RESUME_ANALYSIS_JOB_TOPIC, jobId.toString())
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Kafka send failed", ex);
                    } else {
                        log.info("Kafka send success: {}", result.getRecordMetadata());
                    }
                });
    }
    public void publishJobForSkillAnalysis(UUID jobId) {
        kafkaTemplate.send(KAFKA_SKILL_ANALYSIS_JOB_TOPIC, jobId.toString());
    }
}
