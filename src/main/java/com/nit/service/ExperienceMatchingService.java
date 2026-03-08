package com.nit.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ExperienceMatchingService {

    private static final int DEFAULT_VAL = 1;
    private static final int REQUIRED_YEARS = 2;
    private static final int MAX_YEARS = 40;
    private static final int WINDOW_SIZE = 100;

    // Pattern for years of experience: "3 years", "5+ years", "2 yrs"
    private static final Pattern YEARS_PATTERN = Pattern.compile("(\\d+)\\s*\\+?\\s*(?:years?|yrs?)",
            Pattern.CASE_INSENSITIVE);

    /**
     * Estimates years of experience per skill and computes an experience score.
     *
     * @param resumeText The full extracted resume text.
     * @param jdSkills   List of skills extracted from the Job Description.
     * @return ExperienceResult containing skillExperience map and experienceScore.
     */
    public ExperienceResult computeExperience(String resumeText, List<String> jdSkills) {
        log.info("Computing experience matching for {} skills", jdSkills.size());

        if (resumeText == null || jdSkills == null || jdSkills.isEmpty()) {
            return new ExperienceResult(new HashMap<>(), 0);
        }

        Map<String, Integer> skillExperience = new HashMap<>();
        int validExperienceMatches = 0;

        for (String skill : jdSkills) {
            int maxYearsForSkill = -1;
            String lowerResume = resumeText.toLowerCase();
            String lowerSkill = skill.toLowerCase();

            int skillPos = lowerResume.indexOf(lowerSkill);

            if (skillPos != -1) {
                // Default value if found but no explicit years are found
                maxYearsForSkill = DEFAULT_VAL;

                // Search all occurrences of the skill
                int currentPos = skillPos;
                while (currentPos != -1) {
                    // Look within a ±100 character window
                    int start = Math.max(0, currentPos - WINDOW_SIZE);
                    int end = Math.min(resumeText.length(), currentPos + skill.length() + WINDOW_SIZE);
                    String window = resumeText.substring(start, end);

                    Matcher matcher = YEARS_PATTERN.matcher(window);
                    while (matcher.find()) {
                        try {
                            int years = Integer.parseInt(matcher.group(1));
                            // Ignore negative or unrealistic values (>40 years)
                            if (years > 0 && years <= MAX_YEARS) {
                                if (years > maxYearsForSkill) {
                                    maxYearsForSkill = years;
                                }
                            }
                        } catch (NumberFormatException ignored) {
                        }
                    }
                    currentPos = lowerResume.indexOf(lowerSkill, currentPos + 1);
                }

                skillExperience.put(skill, maxYearsForSkill);

                // Experience Validation
                if (maxYearsForSkill >= REQUIRED_YEARS) {
                    validExperienceMatches++;
                }
            }
        }

        // Formula: experienceScore = (validExperienceMatches / totalJDSkills) * 100
        int experienceScore = (int) Math.round(((double) validExperienceMatches / jdSkills.size()) * 100);

        log.info("Experience Matching Complete. Score: {}% (Valid Matches: {})", experienceScore,
                validExperienceMatches);

        return new ExperienceResult(skillExperience, experienceScore);
    }

    @Data
    public static class ExperienceResult {
        private final Map<String, Integer> skillExperience;
        private final int experienceScore;
    }
}
