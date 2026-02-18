package com.nit.service;

import com.nit.domain.AIResponse;
import org.springframework.web.multipart.MultipartFile;

public interface AtsService {
    AIResponse analyzeResume(final MultipartFile file, String jdText);

    String extractResumeData(final MultipartFile file);

    // AIResponse generateReport(String resumeText, String jdText);

    void trackSkillsGeneration();

    AIResponse getAnalysisResult(java.util.UUID resultId);

    String categorizeSkills(java.util.List<String> skills);
}
