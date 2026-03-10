package com.nit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
public class JobApplicationDTO {
    private Long id;

    @NotBlank(message = "Company name is required")
    private String companyName;

    @Size(max = 2000, message = "Job description cannot exceed 2000 characters")
    private String jobDescription;

    @NotNull(message = "Status is required")
    private ApplicationStatus status;

    private String hrName;
    private String hrEmail;
    private String phone;
    private String resumePath;
    private LocalDate closingDate;
    private LocalDate appliedDate;
    private Instant createdAt;
    private Instant updatedAt;
}
