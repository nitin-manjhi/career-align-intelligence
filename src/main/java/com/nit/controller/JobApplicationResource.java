package com.nit.controller;

import com.nit.dto.JobApplicationDTO;
import com.nit.service.JobApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-applications")
@RequiredArgsConstructor
public class JobApplicationResource {

    private final JobApplicationService service;

    @GetMapping
    public ResponseEntity<List<JobApplicationDTO>> getAllApplications() {
        return ResponseEntity.ok(service.getAllApplications());
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        service.deleteApplication(id);
        return ResponseEntity.ok().build();
    }
}
