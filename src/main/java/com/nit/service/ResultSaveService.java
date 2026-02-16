package com.nit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nit.domain.AIResponse;
import com.nit.entity.AnalysisResultEntity;
import com.nit.exception.ResponseParserException;
import com.nit.repository.AnalysisResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResultSaveService {


    private final AnalysisResultRepository repository;

    public AIResponse saveResult(String resumeText, String jdText, String aiJson) {

        ObjectMapper mapper = new ObjectMapper();
        String cleanedJson = cleanJson(aiJson);
        AIResponse response;
        try {
            response = mapper.readValue(cleanedJson, AIResponse.class);
        } catch (JsonProcessingException e) {
            throw new ResponseParserException("Failed to parse AI response JSON", e);
        }

        AnalysisResultEntity entity = new AnalysisResultEntity();
        entity.setId(UUID.randomUUID());
        entity.setResumeText(resumeText);
        entity.setJdText(jdText);
        entity.setScore(response.getScore());
        entity.setAiResponse(cleanedJson);
        entity.setCreatedAt(LocalDateTime.now());

        repository.save(entity);
        response.setUuid(entity.getId());
        return response;
    }

    public String cleanJson(String raw) {

        if (raw == null) return "{}";

        // remove markdown fences
        raw = raw.replaceAll("```json", "")
                .replaceAll("```", "");

        // remove trailing commas before } or ]
        raw = raw.replaceAll(",\\s*([}\\]])", "$1");

        return raw.trim();
    }

}
