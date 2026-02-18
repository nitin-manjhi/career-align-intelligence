package com.nit.repository;

import com.nit.dto.JobStatus;
import com.nit.entity.AnalysisJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AnalysisJobRepository
        extends JpaRepository<AnalysisJob, UUID> {

    List<AnalysisJob> findByUserIdOrderByCreatedAtDesc(Long userId);

    long countByUserIdAndStatus(Long userId, JobStatus status);
}
