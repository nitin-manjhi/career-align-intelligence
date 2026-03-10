package com.nit.service;

import com.nit.domain.AIResponse;
import org.springframework.web.multipart.MultipartFile;

public interface AtsService {
    AIResponse analyzeResume(MultipartFile file, String jdText, String model, String companyName);

    String extractResumeData(final MultipartFile file);

    // AIResponse generateReport(String resumeText, String jdText);

    void trackSkillsGeneration();

    AIResponse getAnalysisResult(java.util.UUID resultId);

    String categorizeSkills(java.util.List<String> skills);

    java.util.UUID generateCoverLetter(java.util.UUID resultId, String model);

    java.util.UUID generateEmail(java.util.UUID resultId, String model);
}
