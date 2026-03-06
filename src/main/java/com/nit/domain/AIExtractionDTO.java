package com.nit.domain;

import lombok.Data;
import java.util.List;

@Data
public class AIExtractionDTO {
    private Integer score;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private List<String> improvementSuggestions;
    private String coverLetter;
    private EmailTemplate email;
}
