package com.nit.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AIResponse {
    UUID uuid;
    Integer score;
    String scoreExplanation;
    List<String> matchedSkills;
    List<String> missingSkills;
    List<String> improvementSuggestions;
    String newResume;
    String coverLetter;
    EmailTemplate email;
}
