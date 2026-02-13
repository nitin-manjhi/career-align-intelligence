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

}
