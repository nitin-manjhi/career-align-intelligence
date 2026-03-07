package com.nit.service;

import java.util.UUID;

public interface AiService {
    void analyzeResume(String resumeText, String jdText, java.util.UUID resultId, java.util.UUID jobId, Long userId,
            String model);

    String categorizeSkills(java.util.List<String> skills);
}
