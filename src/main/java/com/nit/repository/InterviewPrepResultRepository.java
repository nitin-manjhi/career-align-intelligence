package com.nit.repository;

import com.nit.entity.InterviewPrepResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InterviewPrepResultRepository extends JpaRepository<InterviewPrepResult, UUID> {
    List<InterviewPrepResult> findByUserIdOrderByCreatedAtDesc(Long userId);
}
