package com.nit.ats;

import com.nit.domain.AIResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface AtsService {
    AIResponse analyzeResume(final MultipartFile file, String jdText);

    String extractResumeData(final MultipartFile file);

    AIResponse generateReport(String resumeText, String jdText);

}
