package com.nit.controller;

import com.nit.dto.LinkedInJobDTO;
import com.nit.dto.LinkedInJobResponse;
import com.nit.dto.LinkedInJobSearchRequest;
import com.nit.entity.AnalysisJob;
import com.nit.repository.AnalysisJobRepository;
import com.nit.security.AuthUtil;
import com.nit.service.ScraperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/scraper")
@RequiredArgsConstructor
public class ScraperResource {

    private final ScraperService scraperService;
    private final AnalysisJobRepository jobRepository;
    private final AuthUtil authUtil;

    @PostMapping("/search")
    public ResponseEntity<Map<String, String>> searchJobs(@RequestBody LinkedInJobSearchRequest request) {
        Long userId = authUtil.getCurrentUserId();
        UUID jobId = scraperService.initiateSearch(userId, request);
        return ResponseEntity.accepted().body(Map.of("jobId", jobId.toString()));
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<?> getJobStatus(@PathVariable UUID jobId) {
        AnalysisJob job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (job.getStatus() == com.nit.dto.JobStatus.DONE) {
            LinkedInJobResponse results = scraperService.getCachedResults(jobId);
            return ResponseEntity.ok(results);
        }

        return ResponseEntity.ok(Map.of(
            "status", job.getStatus(),
            "progress", job.getProgress(),
            "errorMessage", job.getErrorMessage() != null ? job.getErrorMessage() : ""
        ));
    }

    @PostMapping("/details")
    public Mono<ResponseEntity<LinkedInJobDTO>> getJobDetails(@RequestBody Map<String, String> request) {
        String url = request.get("url");
        return scraperService.fetchJobDetails(url)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
