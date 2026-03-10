package com.nit.service;

import com.nit.dto.JobApplicationDTO;
import com.nit.entity.JobApplication;
import com.nit.repository.JobApplicationRepository;
import com.nit.security.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobApplicationService {

    private final JobApplicationRepository repository;
    private final AuthUtil authUtil;

    @Transactional(readOnly = true)
    public List<JobApplicationDTO> getAllApplications() {
        Long userId = authUtil.getCurrentUserId();
        return repository.findByUserId(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public JobApplicationDTO createApplication(JobApplicationDTO dto) {
        Long userId = authUtil.getCurrentUserId();
        JobApplication application = toEntity(dto);
        application.setUserId(userId);
        return toDTO(repository.save(application));
    }

    @Transactional
    public JobApplicationDTO updateApplication(Long id, JobApplicationDTO dto) {
        Long userId = authUtil.getCurrentUserId();
        JobApplication application = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!application.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to update this application");
        }

        application.setCompanyName(dto.getCompanyName());
        application.setJobDescription(dto.getJobDescription());
        application.setStatus(dto.getStatus());
        application.setHrName(dto.getHrName());
        application.setHrEmail(dto.getHrEmail());
        application.setPhone(dto.getPhone());
        application.setResumePath(dto.getResumePath());
        application.setClosingDate(dto.getClosingDate());
        application.setAppliedDate(dto.getAppliedDate());

        return toDTO(repository.save(application));
    }

    @Transactional
    public void deleteApplication(Long id) {
        Long userId = authUtil.getCurrentUserId();
        JobApplication application = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!application.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this application");
        }

        repository.delete(application);
    }

    private JobApplicationDTO toDTO(JobApplication application) {
        JobApplicationDTO dto = new JobApplicationDTO();
        dto.setId(application.getId());
        dto.setCompanyName(application.getCompanyName());
        dto.setJobDescription(application.getJobDescription());
        dto.setStatus(application.getStatus());
        dto.setHrName(application.getHrName());
        dto.setHrEmail(application.getHrEmail());
        dto.setPhone(application.getPhone());
        dto.setResumePath(application.getResumePath());
        dto.setClosingDate(application.getClosingDate());
        dto.setAppliedDate(application.getAppliedDate());
        dto.setCreatedAt(application.getCreatedAt());
        dto.setUpdatedAt(application.getUpdatedAt());
        return dto;
    }

    private JobApplication toEntity(JobApplicationDTO dto) {
        return JobApplication.builder()
                .id(dto.getId())
                .companyName(dto.getCompanyName())
                .jobDescription(dto.getJobDescription())
                .status(dto.getStatus())
                .hrName(dto.getHrName())
                .hrEmail(dto.getHrEmail())
                .phone(dto.getPhone())
                .resumePath(dto.getResumePath())
                .closingDate(dto.getClosingDate())
                .appliedDate(dto.getAppliedDate() != null ? dto.getAppliedDate() : LocalDate.now())
                .build();
    }
}
