package com.nit.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nit.domain.AIResponse;
import com.nit.entity.AnalysisResultEntity;
import com.nit.repository.AnalysisResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeProcessingServiceImpl implements ResumeProcessingService {

    private final AnalysisResultRepository analysisResultRepository;
    private final ResumePdfService resumePdfService;
    private final CoverLetterPdfService coverLetterPdfService;

    @Override
    public String extractResumeData(MultipartFile file) {
        StringBuilder content = new StringBuilder();
        try {
            TikaDocumentReader documentReader = new TikaDocumentReader(file.getResource());
            documentReader.read().forEach(document -> content.append(document.getText()));

        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return content.toString();
    }

    @Override
    public byte[] generateOnePageResume(UUID uuid) {
        AIResponse response = getResumeDetail(uuid);
        try {
            return resumePdfService.generateOnePageResume(response);
        } catch (Exception e) {
            log.info(e.getMessage());
        }

        return new byte[0];
    }

    @Override
    public byte[] generateCoverLetterPdf(UUID uuid) {
        AIResponse response = getResumeDetail(uuid);
        try {
            return coverLetterPdfService.generateCoverLetterPdf(response);
        } catch (Exception e) {
            log.info(e.getMessage());
        }

        return new byte[0];
    }


    private AIResponse getResumeDetail(UUID uuid) {
        ObjectMapper mapper = new ObjectMapper();
        AIResponse response = new AIResponse();
        AnalysisResultEntity analysisResultEntity = analysisResultRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("Not found"));
        try {
            response = mapper.readValue(analysisResultEntity.getAiResponse(), AIResponse.class);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return response;
    }

}
