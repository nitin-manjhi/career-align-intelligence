package com.nit.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AIResponse {
    UUID uuid;
    Integer score;
    List<String> scoreExplanation;
    List<String> matchedSkills;
    List<String> missingSkills;
    List<String> improvementSuggestions;
    String optimizedResume;
    String coverLetter;
    EmailTemplate email;
}
