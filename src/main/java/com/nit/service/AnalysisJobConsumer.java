package com.nit.service;

import com.nit.dto.JobStatus;
import com.nit.entity.AnalysisJob;
import com.nit.entity.AnalysisResultEntity;
import com.nit.repository.AnalysisJobRepository;
import com.nit.repository.AnalysisResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AnalysisJobConsumer {

    private final AnalysisJobRepository jobRepo;
    private final AnalysisResultRepository repository;
    private final AiService aiService;
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "RESUME.AI.ANALYSIS_JOB_TOPIC", groupId = "analysis-group")
    public void processResumeAnalysis(String jobIdStr) {

        UUID jobId = UUID.fromString(jobIdStr);
        AnalysisJob job = jobRepo.findById(jobId).orElseThrow();
        AnalysisResultEntity analysisResultEntity = repository.findById(job.getResultId()).orElseThrow();

        try {
            updateProgress(job, 10, JobStatus.PROCESSING, null);

            // AI processing
           aiService.analyzeResume(analysisResultEntity.getResumeText(),
                    analysisResultEntity.getJdText(), analysisResultEntity.getId());

            updateProgress(job, 100, JobStatus.DONE, LocalDateTime.now());

            // Notify user via WebSocket
            String notificationJson = String.format(
                    "{\"message\": \"Analysis complete for job: %s\", \"resultId\": \"%s\"}",
                    jobId, job.getResultId());
            // Notify user via a direct topic based on their ID
            String destination = "/topic/notifications-" + job.getUserId();
            messagingTemplate.convertAndSend(destination, notificationJson);
        } catch (Exception e) {
            job.setStatus(JobStatus.FAILED);
            job.setErrorMessage(e.getMessage());
        }

        jobRepo.save(job);
    }

    private void updateProgress(AnalysisJob job, int progress, JobStatus status, LocalDateTime localDateTime) {
        job.setProgress(progress);
        job.setStatus(status);
        job.setCompletedAt(localDateTime);
        jobRepo.save(job);
    }
}
