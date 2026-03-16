package com.nit.service;

import com.nit.dto.ApplicationStatus;
import com.nit.dto.JobApplicationDTO;
import com.nit.dto.PaginatedResponse;
import com.nit.entity.JobApplication;
import com.nit.repository.JobApplicationRepository;
import com.nit.security.AuthUtil;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobApplicationService {

    private final JobApplicationRepository repository;
    private final AuthUtil authUtil;
    private final ResultSaveService resultSaveService;

    @Transactional(readOnly = true)
    public PaginatedResponse<JobApplicationDTO> getApplications(String search, ApplicationStatus status,
            Pageable pageable) {
        Long userId = authUtil.getCurrentUserId();

        Specification<JobApplication> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("userId"), userId));

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (search != null && !search.trim().isEmpty()) {
                String searchLower = "%" + search.toLowerCase() + "%";
                Predicate companyPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("companyName")),
                        searchLower);
                Predicate hrPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("hrName")), searchLower);
                predicates.add(criteriaBuilder.or(companyPredicate, hrPredicate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<JobApplication> page = repository.findAll(spec, pageable);

        List<JobApplicationDTO> content = page.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return PaginatedResponse.<JobApplicationDTO>builder()
                .content(content)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .build();
    }

    @Transactional(readOnly = true)
    public List<JobApplicationDTO> getAllApplications() {
        Long userId = authUtil.getCurrentUserId();
        return repository
                .findAll((Specification<JobApplication>) (root, query, cb) -> cb.equal(root.get("userId"), userId))
                .stream()
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
        application.setJobTitle(dto.getJobTitle());
        application.setLocation(dto.getLocation());
        application.setSalary(dto.getSalary());
        application.setSkills(dto.getSkills());
        application.setApplyLink(dto.getApplyLink());
        application.setOriginalPostedDate(dto.getOriginalPostedDate());

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

    @Transactional(readOnly = true)
    public com.nit.domain.AIResponse getAnalysisResultByJobId(Long id) {
        Long userId = authUtil.getCurrentUserId();
        JobApplication application = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!application.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        if (application.getAnalysisId() == null) {
            return null;
        }

        return resultSaveService.getResult(application.getAnalysisId());
    }

    @Transactional(readOnly = true)
    public com.nit.dto.JobApplicationStatsDTO getStats() {
        Long userId = authUtil.getCurrentUserId();
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);

        List<Object[]> statusCounts = repository.countByStatus(userId);
        java.util.Map<String, Long> distribution = statusCounts.stream()
                .collect(Collectors.toMap(
                        row -> row[0].toString(),
                        row -> (Long) row[1]
                ));

        return com.nit.dto.JobApplicationStatsDTO.builder()
                .totalApplications(repository.countByUserId(userId))
                .statusDistribution(distribution)
                .applicationsLast7Days(repository.countByUserIdAndAppliedDateGreaterThanEqual(userId, sevenDaysAgo))
                .applicationsLast30Days(repository.countByUserIdAndAppliedDateGreaterThanEqual(userId, thirtyDaysAgo))
                .withAnalysisCount(repository.countWithAnalysis(userId))
                .build();
    }

    @Transactional
    public void updateApplicationAnalysisId(Long id, java.util.UUID analysisId) {
        JobApplication application = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        application.setAnalysisId(analysisId);
        repository.save(application);
    }

    @Transactional
    public void importApplications(List<JobApplicationDTO> dtos) {
        Long userId = authUtil.getCurrentUserId();
        List<JobApplication> applications = dtos.stream()
                .map(dto -> {
                    JobApplication app = toEntity(dto);
                    app.setUserId(userId);
                    app.setId(null); // Ensure they are treated as new entities
                    return app;
                })
                .collect(Collectors.toList());
        repository.saveAll(applications);
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
        dto.setJobTitle(application.getJobTitle());
        dto.setLocation(application.getLocation());
        dto.setSalary(application.getSalary());
        dto.setSkills(application.getSkills());
        dto.setApplyLink(application.getApplyLink());
        dto.setOriginalPostedDate(application.getOriginalPostedDate());
        dto.setAnalysisId(application.getAnalysisId());
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
                .jobTitle(dto.getJobTitle())
                .location(dto.getLocation())
                .salary(dto.getSalary())
                .skills(dto.getSkills())
                .applyLink(dto.getApplyLink())
                .originalPostedDate(dto.getOriginalPostedDate())
                .analysisId(dto.getAnalysisId())
                .build();
    }
}
