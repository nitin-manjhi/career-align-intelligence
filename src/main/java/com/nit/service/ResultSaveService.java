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
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResultSaveService {

    private final AnalysisResultRepository repository;
    private final ObjectMapper objectMapper;

    public AIResponse saveResult(UUID resultId, String aiJson) {

        String cleanedJson = cleanJson(aiJson);
        AIResponse response;
        try {
            response = objectMapper.readValue(cleanedJson, AIResponse.class);
        } catch (JsonProcessingException e) {
            throw new ResponseParserException("Failed to parse AI response JSON", e);
        }

        Optional<AnalysisResultEntity> entity = repository.findById(resultId);
        entity.ifPresent(result -> {
            result.setScore(result.getScore());
            result.setAiResponse(cleanedJson);
            result.setCreatedAt(LocalDateTime.now());
            repository.save(result);
        });

        return response;
    }

    public AIResponse getResult(UUID uuid) {
        AnalysisResultEntity entity = repository.findById(uuid)
                .orElseThrow(() -> new ResponseParserException("Result not found for UUID: " + uuid));

        if (entity.getAiResponse() == null) {
            throw new ResponseParserException("Result data is not yet available for UUID: " + uuid);
        }

        try {
            return objectMapper.readValue(entity.getAiResponse(), AIResponse.class);
        } catch (JsonProcessingException e) {
            throw new ResponseParserException("Failed to parse AI response JSON from database", e);
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
