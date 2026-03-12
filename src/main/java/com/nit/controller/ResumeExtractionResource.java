package com.nit.controller;

import com.nit.domain.AIResponse;
import com.nit.service.AtsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ResumeExtractionResource {

    private final AtsService atsService;

    @PostMapping(path = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AIResponse> analyseResume(@Valid @NotNull @RequestPart("file") MultipartFile file,
            @RequestParam("jdText") String jdText,
            @RequestParam(value = "model", required = false) String model,
            @RequestParam(value = "companyName", required = false) String companyName,
            @RequestParam(value = "applicationId", required = false) Long applicationId) {
        return ResponseEntity.ok(atsService.analyzeResume(file, jdText, model, companyName, applicationId));
    }

    @PostMapping("/track-generation")
    public ResponseEntity<Void> trackGeneration() {
        atsService.trackSkillsGeneration();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/categorize-skills")
    public ResponseEntity<String> categorizeSkills(
            @RequestBody List<String> skills) {
        return ResponseEntity.ok(atsService.categorizeSkills(skills));
    }

    @GetMapping("/analysis-result/{resultId}")
    public ResponseEntity<AIResponse> getAnalysisResult(
            @PathVariable UUID resultId,
            @RequestParam(required = false) List<String> fields) {
        AIResponse response = atsService.getAnalysisResult(resultId);
        if (fields != null && !fields.isEmpty()) {
            AIResponse filtered = new AIResponse();
            filtered.setUuid(response.getUuid());

            if (fields.contains("score"))
                filtered.setScore(response.getScore());
            if (fields.contains("scoreExplanation"))
                filtered.setScoreExplanation(response.getScoreExplanation());
            if (fields.contains("matchedSkills"))
                filtered.setMatchedSkills(response.getMatchedSkills());
            if (fields.contains("missingSkills"))
                filtered.setMissingSkills(response.getMissingSkills());
            if (fields.contains("improvementSuggestions"))
                filtered.setImprovementSuggestions(response.getImprovementSuggestions());
            if (fields.contains("optimizedResume"))
                filtered.setOptimizedResume(response.getOptimizedResume());
            if (fields.contains("coverLetter"))
                filtered.setCoverLetter(response.getCoverLetter());
            if (fields.contains("email"))
                filtered.setEmail(response.getEmail());

            return ResponseEntity.ok(filtered);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/generate-cover-letter/{resultId}")
    public ResponseEntity<UUID> generateCoverLetter(@PathVariable UUID resultId,
            @RequestParam(required = false) String model) {
        return ResponseEntity.ok(atsService.generateCoverLetter(resultId, model));
    }

    @PostMapping("/generate-email/{resultId}")
    public ResponseEntity<UUID> generateEmail(@PathVariable UUID resultId,
            @RequestParam(required = false) String model) {
        return ResponseEntity.ok(atsService.generateEmail(resultId, model));
    }

    @PostMapping("/rewrite-summary")
    public ResponseEntity<String> rewriteSummary(
            @RequestBody java.util.Map<String, String> request) {
        String summary = request.get("summary");
        String model = request.get("model");
        return ResponseEntity.ok(atsService.rewriteSummary(summary, model));
    }

}
