package com.nit.service;

import java.util.UUID;

public interface AiService {
    void analyzeResume(String resumeText, String jdText, java.util.UUID resultId, java.util.UUID jobId, Long userId,
            String model);

    String categorizeSkills(java.util.List<String> skills);

    String generateCoverLetter(java.util.UUID resultId, String model, java.util.UUID jobId, Long userId);

    void generateEmail(java.util.UUID resultId, String model, java.util.UUID jobId, Long userId);

    String rewriteSummary(String summary, String model, Long userId);
}
