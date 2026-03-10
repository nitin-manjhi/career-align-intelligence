package com.nit.service;

import com.nit.dto.JobStatus;
import com.nit.entity.AnalysisJob;
import com.nit.repository.AnalysisJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalysisJobService {

    private final AnalysisJobRepository jobRepository;

    public AnalysisJob createJob(Long userId, UUID analysisId, String model, com.nit.dto.JobType type) {

        AnalysisJob job = AnalysisJob.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .status(JobStatus.PENDING)
                .progress(0)
                .jobType(type)
                .resultId(analysisId)
                .model(model)
                .createdAt(LocalDateTime.now())
                .build();

        return jobRepository.save(job);
    }

}
