package com.nit.controller;

import com.nit.dto.ApplicationStatus;
import com.nit.dto.JobApplicationDTO;
import com.nit.dto.PaginatedResponse;
import com.nit.service.JobApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/job-applications")
@RequiredArgsConstructor
public class JobApplicationResource {

    private final JobApplicationService service;

    @GetMapping
    public ResponseEntity<PaginatedResponse<JobApplicationDTO>> getAllApplications(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) ApplicationStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(service.getApplications(search, status, pageable));
    }

    @GetMapping("/stats")
    public ResponseEntity<com.nit.dto.JobApplicationStatsDTO> getStats() {
        return ResponseEntity.ok(service.getStats());
    }

    @PostMapping
    public ResponseEntity<JobApplicationDTO> createApplication(@Valid @RequestBody JobApplicationDTO dto) {
        return ResponseEntity.ok(service.createApplication(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobApplicationDTO> updateApplication(@PathVariable Long id,
            @Valid @RequestBody JobApplicationDTO dto) {
        return ResponseEntity.ok(service.updateApplication(id, dto));
    }

    @GetMapping("/export")
    public ResponseEntity<List<JobApplicationDTO>> exportApplications() {
        return ResponseEntity.ok(service.getAllApplications());
    }

    @PostMapping("/import")
    public ResponseEntity<Void> importApplications(@RequestBody List<JobApplicationDTO> dtos) {
        service.importApplications(dtos);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        service.deleteApplication(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/analysis-result")
    public ResponseEntity<com.nit.domain.AIResponse> getAnalysisResult(@PathVariable Long id) {
        return ResponseEntity.ok(service.getAnalysisResultByJobId(id));
    }
}
