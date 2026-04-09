package com.nit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "interview_prep_result")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewPrepResult {
    @Id
    private UUID id;

    private Long userId;
    private String companyName;
    private String role;
    private String experience;
    private String domain;
    private String type; // TOPIC_WISE or SCENARIO_BASED
    private String model;

    @Column(columnDefinition = "TEXT")
    private String generatedContent;

    private LocalDateTime createdAt;
}
