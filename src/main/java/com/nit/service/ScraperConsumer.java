package com.nit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nit.dto.JobStatus;
import com.nit.dto.LinkedInJobResponse;
import com.nit.dto.LinkedInJobSearchRequest;
import com.nit.entity.AnalysisJob;
import com.nit.repository.AnalysisJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScraperConsumer {

    private final ScraperService scraperService;
    private final AnalysisJobRepository jobRepository;
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "${kafka.topic.job-search-topic}", groupId = "scraper-group")
    public void processJobSearch(String jobIdStr) {
        UUID jobId = UUID.fromString(jobIdStr);
        AnalysisJob job = jobRepository.findById(jobId).orElseThrow();

        try {
            LinkedInJobSearchRequest request = objectMapper.readValue(job.getJobData(), LinkedInJobSearchRequest.class);
            updateJobStatus(job, JobStatus.PROCESSING, 20);

            // Call Scraper API Sync via Service
            LinkedInJobResponse response = scraperService.searchJobsSync(request).block();
            
            // Cache in Redis
            scraperService.cacheResults(jobId, response);
            
            updateJobStatus(job, JobStatus.DONE, 100);
            notifySuccess(job.getUserId(), jobId);
            
        } catch (Exception e) {
            log.error("Scraping job failed: " + jobId, e);
            job.setStatus(JobStatus.FAILED);
            job.setErrorMessage(e.getMessage());
            notifyFailure(job.getUserId(), jobId, e.getMessage());
        } finally {
            job.setCompletedAt(LocalDateTime.now());
            jobRepository.save(job);
        }
    }

    private void updateJobStatus(AnalysisJob job, JobStatus status, int progress) {
        job.setStatus(status);
        job.setProgress(progress);
        jobRepository.save(job);
    }

    private void notifySuccess(Long userId, UUID jobId) {
        String destination = "/topic/notifications-" + userId;
        messagingTemplate.convertAndSend(destination, java.util.Map.of(
                "jobId", jobId.toString(),
                "status", "DONE",
                "type", "JOB_SEARCH_SUCCESS"
        ));
    }

    private void notifyFailure(Long userId, UUID jobId, String error) {
        String destination = "/topic/notifications-" + userId;
        messagingTemplate.convertAndSend(destination, java.util.Map.of(
                "jobId", jobId.toString(),
                "status", "FAILED",
                "message", error,
                "type", "JOB_SEARCH_ERROR"
        ));
    }
}
