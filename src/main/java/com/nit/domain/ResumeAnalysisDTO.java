package com.nit.domain;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ResumeAnalysisDTO {
    private Integer score;
    private List<String> scoreExplanation;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private List<String> improvementSuggestions;
    private String optimizedResume;

    // Experience Matching fields
    private Map<String, Integer> skillExperience;
    private Integer experienceScore;

    // Hybrid Scoring components
    private Double keywordScore;
    private Double semanticScore;
    private Double weightedSkillScore;
    private Map<String, String> skillImportance;

    // Structured data for UI rendering
    private Object structuredResume;
}
