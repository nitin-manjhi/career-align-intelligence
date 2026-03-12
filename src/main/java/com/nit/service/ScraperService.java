package com.nit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nit.dto.JobStatus;
import com.nit.dto.JobType;
import com.nit.dto.LinkedInJobDTO;
import com.nit.dto.LinkedInJobResponse;
import com.nit.dto.LinkedInJobSearchRequest;
import com.nit.entity.AnalysisJob;
import com.nit.repository.AnalysisJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScraperService {

    private final WebClient webClient;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AnalysisJobRepository jobRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.scraper-api-url:http://localhost:3000}")
    private String scraperApiUrl;

    @Value("${kafka.topic.job-search-topic}")
    private String jobSearchTopic;

    public UUID initiateSearch(Long userId, LinkedInJobSearchRequest request) {
        UUID jobId = UUID.randomUUID();
        
        try {
            String jobData = objectMapper.writeValueAsString(request);
            
            // Create a job record
            AnalysisJob job = AnalysisJob.builder()
                    .id(jobId)
                    .userId(userId)
                    .status(JobStatus.PENDING)
                    .progress(0)
                    .jobType(JobType.JOB_SEARCH)
                    .jobData(jobData)
                    .createdAt(LocalDateTime.now())
                    .build();
            jobRepository.save(job);

            // Send jobId to Kafka
            kafkaTemplate.send(jobSearchTopic, jobId.toString());
            return jobId;
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize job search request", e);
            throw new RuntimeException("Search initialization failed");
        }
    }

    public Mono<LinkedInJobResponse> searchJobsSync(LinkedInJobSearchRequest request) {
        return webClient.post()
                .uri(scraperApiUrl + "/jobs/search")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(LinkedInJobResponse.class);
    }

    public LinkedInJobResponse getCachedResults(UUID jobId) {
        Object results = redisTemplate.opsForValue().get("JOB_SEARCH_RESULT:" + jobId);
        if (results instanceof LinkedInJobResponse) {
            return (LinkedInJobResponse) results;
        }
        return null;
    }

    public Mono<LinkedInJobDTO> fetchJobDetails(String url) {
        return webClient.post()
                .uri(scraperApiUrl + "/jobs/details")
                .bodyValue(java.util.Map.of("url", url))
                .retrieve()
                .bodyToMono(LinkedInJobDTO.class);
    }

    public void cacheResults(UUID jobId, LinkedInJobResponse response) {
        redisTemplate.opsForValue().set("JOB_SEARCH_RESULT:" + jobId, response, 1, TimeUnit.HOURS);
    }
}
