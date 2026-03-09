package com.nit.service;

import com.nit.domain.AIResponse;
import com.nit.entity.AnalysisJob;
import com.nit.entity.AnalysisResultEntity;
import com.nit.entity.User;
import com.nit.exception.BadRequestException;
import com.nit.repository.AnalysisResultRepository;
import com.nit.repository.UserRepository;
import com.nit.security.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AtsServiceImpl implements AtsService {
    private final ResumeProcessingService resumeProcessingService;
    private final AiService aiService;
    private final UserRepository userRepository;
    private final AuthUtil authUtil;
    private final AnalysisJobService analysisJobService;
    private final KafkaProducerService kafkaProducerService;
    private final AnalysisResultRepository repository;
    private final ResultSaveService resultSaveService;

    @Override
    @Transactional
    public AIResponse analyzeResume(MultipartFile file, String jdText, String model) {
        Long userId = authUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        boolean isAdmin = user.getRole() == com.nit.entity.Role.ADMIN;
        boolean isPremiumModel = model != null && (model.equalsIgnoreCase("openai") || model.equalsIgnoreCase("gemini")
                || model.equalsIgnoreCase("openrouter"));

        if (!isAdmin) {
            if (isPremiumModel) {
                if (!user.isPremiumActive() || user.getPremiumUsageCount() >= user.getPremiumUsageLimit()) {
                    throw new BadRequestException("Premium analysis quota exceeded. Contact admin for more.");
                }
            } else {
                if (user.getAnalysisCount() >= user.getUsageLimit()) {
                    throw new BadRequestException("Standard analysis limit reached. Please upgrade your plan.");
                }
            }
        }

        String resumeText = extractResumeData(file);

        // save information related to the analysis job in the database and publish a
        // message to Kafka for asynchronous processing

        AnalysisResultEntity entity = new AnalysisResultEntity();
        entity.setId(UUID.randomUUID());
        entity.setResumeText(resumeText);
        entity.setJdText(jdText);
        entity.setCreatedAt(LocalDateTime.now());
        AnalysisResultEntity analysisResultEntity = repository.save(entity);

        AnalysisJob job = analysisJobService.createJob(userId, analysisResultEntity.getId(), model);

        kafkaProducerService.publishJobForResumeAnalysis(job.getId());

        // Increment of usage handled in AiServiceImpl after successful analysis

        // return job.getId() or some placeholder response since actual analysis will be
        // done asynchronously
        AIResponse response = new AIResponse();
        response.setUuid(job.getId());
        return response;

    }

    @Override
    public String categorizeSkills(java.util.List<String> skills) {
        // We can add validation here if needed
        return aiService.categorizeSkills(skills);
    }

    @Override
    public String extractResumeData(MultipartFile file) {
        String resumeData = resumeProcessingService.extractResumeData(file);
        if (resumeData != null && resumeData.length() > 10000) {
            return resumeData.substring(0, 10000);
        }
        return resumeData;
    }

    @Override
    public void trackSkillsGeneration() {
        Long userId = authUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (user.getRole() == com.nit.entity.Role.ADMIN) {
            user.setGenerationCount(user.getGenerationCount() + 1);
            userRepository.save(user);
            return;
        }

        if (user.getGenerationCount() >= user.getGenerationLimit()) {
            throw new BadRequestException("Generation limit reached. Please upgrade your plan.");
        }

        user.setGenerationCount(user.getGenerationCount() + 1);
        userRepository.save(user);
    }

    @Override
    public AIResponse getAnalysisResult(UUID resultId) {
        return resultSaveService.getResult(resultId);
    }

}
