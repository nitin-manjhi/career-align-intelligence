package com.nit.entity;

import com.nit.dto.JobStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "analysis_job", indexes = {
                @Index(name = "idx_analysis_job_user_id", columnList = "user_id"),
                @Index(name = "idx_analysis_job_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisJob {

        @Id
        private UUID id;

        @Column(name = "user_id", nullable = false)
        private Long userId;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false, length = 20)
        private JobStatus status;

        @Column(nullable = false)
        private Integer progress = 0;

        @Column(name = "result_id")
        private UUID resultId;

        @Column(name = "error_message", columnDefinition = "TEXT")
        private String errorMessage;

        @Column(name = "created_at", nullable = false)
        private LocalDateTime createdAt;

        @Column(name = "completed_at")
        private LocalDateTime completedAt;
}
