package com.nit.controller;

import com.nit.entity.InterviewPrepResult;
import com.nit.security.AuthUtil;
import com.nit.service.InterviewPrepService;
import com.nit.service.ResumeProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/interview-prep")
@RequiredArgsConstructor
public class InterviewPrepResource {

    private final InterviewPrepService interviewPrepService;
    private final ResumeProcessingService resumeProcessingService;
    private final AuthUtil authUtil;

    @PostMapping(path = "/generate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> generateQuestions(
            @RequestPart("file") MultipartFile file,
            @RequestParam("companyName") String companyName,
            @RequestParam("role") String role,
            @RequestParam("experience") String experience,
            @RequestParam(value = "domain", required = false) String domain,
            @RequestParam("jdText") String jdText,
            @RequestParam(value = "model", required = false, defaultValue = "ollama") String model,
            @RequestParam("type") String type) {

        Long userId = authUtil.getCurrentUserId();
        log.info("Interview prep request from user: {}, type: {}, company: {}", userId, type, companyName);

        // Extract resume text from uploaded file
        String resumeText = resumeProcessingService.extractResumeData(file);
        if (resumeText != null && resumeText.length() > 10000) {
            resumeText = resumeText.substring(0, 10000);
        }

        // Trigger async streaming - tokens will be sent via WebSocket
        interviewPrepService.generateQuestionsStream(
                resumeText, jdText, companyName, role, experience, domain, type, model, userId);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(Map.of("status", "STREAMING", "message", "Generation started. Tokens will stream via WebSocket."));
    }

    @GetMapping("/history")
    public ResponseEntity<List<InterviewPrepResult>> getHistory() {
        Long userId = authUtil.getCurrentUserId();
        return ResponseEntity.ok(interviewPrepService.getHistory(userId));
    }

    @GetMapping("/result/{resultId}")
    public ResponseEntity<InterviewPrepResult> getResult(@PathVariable UUID resultId) {
        return ResponseEntity.ok(interviewPrepService.getResult(resultId));
    }

    @DeleteMapping("/result/{resultId}")
    public ResponseEntity<Void> deleteResult(@PathVariable UUID resultId) {
        Long userId = authUtil.getCurrentUserId();
        interviewPrepService.deleteResult(resultId, userId);
        return ResponseEntity.noContent().build();
    }
}
