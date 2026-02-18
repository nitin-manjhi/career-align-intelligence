package com.nit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeProcessingServiceImpl implements ResumeProcessingService {

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
