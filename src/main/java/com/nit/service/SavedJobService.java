package com.nit.service;

import com.nit.dto.SavedJobDTO;
import com.nit.entity.SavedJob;
import com.nit.repository.SavedJobRepository;
import com.nit.security.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavedJobService {

    private final SavedJobRepository repository;
    private final AuthUtil authUtil;

    @Transactional(readOnly = true)
    public List<SavedJobDTO> getSavedJobs() {
        Long userId = authUtil.getCurrentUserId();
        return repository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SavedJobDTO saveJob(SavedJobDTO dto) {
        Long userId = authUtil.getCurrentUserId();
        SavedJob job = toEntity(dto);
        job.setUserId(userId);
        return toDTO(repository.save(job));
    }

    @Transactional
    public void deleteSavedJob(Long id) {
        Long userId = authUtil.getCurrentUserId();
        SavedJob job = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Saved job not found"));
        
        if (!job.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        repository.delete(job);
    }

    private SavedJobDTO toDTO(SavedJob job) {
        return SavedJobDTO.builder()
                .id(job.getId())
                .companyName(job.getCompanyName())
                .jobTitle(job.getJobTitle())
                .location(job.getLocation())
                .salary(job.getSalary())
                .skills(job.getSkills())
                .jobDescription(job.getJobDescription())
                .applyLink(job.getApplyLink())
                .originalPostedDate(job.getOriginalPostedDate())
                .createdAt(job.getCreatedAt())
                .build();
    }

    private SavedJob toEntity(SavedJobDTO dto) {
        return SavedJob.builder()
                .companyName(dto.getCompanyName())
                .jobTitle(dto.getJobTitle())
                .location(dto.getLocation())
                .salary(dto.getSalary())
                .skills(dto.getSkills())
                .jobDescription(dto.getJobDescription())
                .applyLink(dto.getApplyLink())
                .originalPostedDate(dto.getOriginalPostedDate())
                .build();
    }
}
