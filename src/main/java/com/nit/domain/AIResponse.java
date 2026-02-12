package com.nit.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Data
@Setter
@Getter
public class AIResponse {
    UUID uuid;
    Integer score;
    List<String> matchedSkills;
    List<String> missingSkills;
    List<String> improvements;
    String newResume;
    String coverLetter;
    EmailTemplate email;
}
