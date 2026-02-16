package com.nit.controller;

import com.nit.service.AtsService;
import com.nit.domain.AIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor
public class ResumeExtractionResource {

    private final AtsService atsService;

    @PostMapping(path = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AIResponse> analyseResume(@Valid @NotNull @RequestPart("file") MultipartFile file,
            @RequestPart("jdText") String jdText) {
        return ResponseEntity.ok(atsService.analyzeResume(file, jdText));
    }

    @PostMapping("/track-generation")
    public ResponseEntity<Void> trackGeneration() {
        atsService.trackGeneration();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/categorize-skills")
    public ResponseEntity<String> categorizeSkills(
            @org.springframework.web.bind.annotation.RequestBody java.util.List<String> skills) {
        return ResponseEntity.ok(atsService.categorizeSkills(skills));
    }

}
