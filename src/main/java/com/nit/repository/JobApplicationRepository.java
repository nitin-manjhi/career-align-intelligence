package com.nit.repository;

import com.nit.entity.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface JobApplicationRepository
        extends JpaRepository<JobApplication, Long>, JpaSpecificationExecutor<JobApplication> {
    Page<JobApplication> findByUserId(Long userId, Pageable pageable);

    long countByUserId(Long userId);

    @org.springframework.data.jpa.repository.Query("SELECT j.status, COUNT(j) FROM JobApplication j WHERE j.userId = :userId GROUP BY j.status")
    java.util.List<Object[]> countByStatus(Long userId);

    long countByUserIdAndAppliedDateGreaterThanEqual(Long userId, java.time.LocalDate date);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(j) FROM JobApplication j WHERE j.userId = :userId AND j.analysisId IS NOT NULL")
    long countWithAnalysis(Long userId);
}
