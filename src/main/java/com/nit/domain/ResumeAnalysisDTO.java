package com.nit.domain;

import lombok.Data;
import java.util.List;

@Data
public class ResumeAnalysisDTO {
    private Integer score;
    private String scoreExplanation;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private List<String> improvementSuggestions;
    private String optimizedResume;
}
