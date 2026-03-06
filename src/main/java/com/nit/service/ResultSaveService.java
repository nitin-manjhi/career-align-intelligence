package com.nit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nit.domain.AIResponse;
import com.nit.entity.AnalysisResultEntity;
import com.nit.exception.ResponseParserException;
import com.nit.repository.AnalysisResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResultSaveService {

    private final AnalysisResultRepository repository;
    private final ObjectMapper objectMapper;

    public void saveResult(UUID resultId, String aiJson) {

        String cleanedJson = cleanJson(aiJson);
        AIResponse response = parseJson(cleanedJson);
        AnalysisResultEntity entity = repository.findById(resultId)
                .orElseThrow(() -> new ResponseParserException("Analysis Information not found for UUID: " + resultId));
        entity.setScore(response.getScore());
        entity.setAiResponse(cleanedJson);
        repository.save(entity);

    }

    public AIResponse getResult(UUID uuid) {
        AnalysisResultEntity entity = repository.findById(uuid)
                .orElseThrow(() -> new ResponseParserException("Result not found for UUID: " + uuid));

        if (entity.getAiResponse() == null) {
            throw new ResponseParserException("Result data is not yet available for UUID: " + uuid);
        }

        AIResponse response = parseJson(entity.getAiResponse());
        response.setUuid(uuid);
        return response;
    }

    private AIResponse parseJson(String json) {
        try {
            return objectMapper.readValue(json, AIResponse.class);
        } catch (JsonProcessingException e) {
            throw new ResponseParserException("Failed to parse AI response JSON", e);
        }
    }

    public String cleanJson(String raw) {

        if (raw == null)
            return "{}";

        // remove markdown fences
        raw = raw.replaceAll("```json", "")
                .replaceAll("```", "");

        // remove trailing commas before } or ]
        raw = raw.replaceAll(",\\s*([}\\]])", "$1");

        return raw.trim();
    }

}
