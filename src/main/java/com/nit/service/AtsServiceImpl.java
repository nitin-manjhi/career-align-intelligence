package com.nit.service;

import com.nit.domain.AIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class AtsServiceImpl implements AtsService {
    private final ResumeProcessingService resumeProcessingService;
    private final AiService aiService;


    @Override
    public AIResponse analyzeResume(MultipartFile file, String jdText) {
        String resumeText = extractResumeData(file);
        return generateReport(resumeText, jdText);
    }

    @Override
    public String extractResumeData(MultipartFile file) {
        return resumeProcessingService.extractResumeData(file);
    }

    @Override
    public AIResponse generateReport(String resumeText, String jdText) {
        return aiService.analyzeResume(resumeText, jdText);
    }


}
