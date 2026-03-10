package com.nit.entity;

import com.nit.dto.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "job_application")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "job_description", columnDefinition = "TEXT", length = 2000)
    private String jobDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApplicationStatus status;

    @Column(name = "hr_name")
    private String hrName;

    @Column(name = "hr_email")
    private String hrEmail;

    @Column(name = "phone")
    private String phone;

    @Column(name = "resume_path")
    private String resumePath;

    @Column(name = "closing_date")
    private LocalDate closingDate;

    @Column(name = "applied_date")
    private LocalDate appliedDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
