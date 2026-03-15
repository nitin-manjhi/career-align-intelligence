package com.nit.controller;

import com.nit.dto.SavedJobDTO;
import com.nit.service.SavedJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saved-jobs")
@RequiredArgsConstructor
public class SavedJobResource {

    private final SavedJobService savedJobService;

    @GetMapping
    public ResponseEntity<List<SavedJobDTO>> getSavedJobs() {
        return ResponseEntity.ok(savedJobService.getSavedJobs());
    }

    @PostMapping
    public ResponseEntity<SavedJobDTO> saveJob(@RequestBody SavedJobDTO dto) {
        return ResponseEntity.ok(savedJobService.saveJob(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSavedJob(@PathVariable Long id) {
        savedJobService.deleteSavedJob(id);
        return ResponseEntity.noContent().build();
    }
}
