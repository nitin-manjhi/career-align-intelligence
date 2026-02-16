package com.nit.service;

import com.nit.domain.AIResponse;
import com.nit.entity.User;
import com.nit.error.BadRequestException;
import com.nit.repository.UserRepository;
import com.nit.security.AuthUtil;
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
    private final UserRepository userRepository;
    private final AuthUtil authUtil;

    @Override
    public AIResponse analyzeResume(MultipartFile file, String jdText) {
        Long userId = authUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (user.getAnalysisCount() >= user.getUsageLimit()) {
            throw new BadRequestException("Analysis limit reached. Please upgrade your plan.");
        }

        String resumeText = extractResumeData(file);
        AIResponse response = generateReport(resumeText, jdText);

        // Increment analysis count
        user.setAnalysisCount(user.getAnalysisCount() + 1);
        userRepository.save(user);

        return response;
    }

    @Override
    public String extractResumeData(MultipartFile file) {
        return resumeProcessingService.extractResumeData(file);
    }

    @Override
    public AIResponse generateReport(String resumeText, String jdText) {
        // We could also track generation here if it's called independently
        return aiService.analyzeResume(resumeText, jdText);
    }

    @Override
    public void trackGeneration() {
        Long userId = authUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (user.getGenerationCount() >= user.getUsageLimit()) {
            throw new BadRequestException("Generation limit reached. Please upgrade your plan.");
        }

        user.setGenerationCount(user.getGenerationCount() + 1);
        userRepository.save(user);
    }

    @Override
    public String categorizeSkills(java.util.List<String> skills) {
        // We can add validation here if needed
        return aiService.categorizeSkills(skills);
    }
}
