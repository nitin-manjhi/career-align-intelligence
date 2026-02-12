package com.nit.controller;

import com.nit.ats.AtsService;
import com.nit.domain.AIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ResumeExtractionResource {

    private final AtsService atsService;

    @PostMapping(path = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AIResponse> getResume(@Valid @NotNull @RequestPart("file") MultipartFile file, @RequestPart("jdText") String jdText) {
        return ResponseEntity.ok(atsService.analyzeResume(file, jdText));
    }

    @GetMapping("/result/{id}/resume-onepage")
    public ResponseEntity<byte[]> downloadOnePage(@PathVariable UUID id) {
        byte[] pdf = atsService.downloadOnePageResume(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=resume.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);

    }

    @GetMapping("/result/{id}/cover-letter")
    public ResponseEntity<byte[]> downloadCoverLetter(@PathVariable UUID id) {
        byte[] pdf = atsService.downloadCoverLetterPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=resume.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);

    }


}
