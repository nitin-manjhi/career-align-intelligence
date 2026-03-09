package com.nit.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class SkillWeightingService {

    private static final int WEIGHT_REQUIRED = 3;
    private static final int WEIGHT_PREFERRED = 2;
    private static final int WEIGHT_OPTIONAL = 1;

    public static class WeightingResult {
        private final Map<String, String> skillImportance;
        private final double weightedSkillScore;

        public WeightingResult(Map<String, String> skillImportance, double weightedSkillScore) {
            this.skillImportance = skillImportance;
            this.weightedSkillScore = weightedSkillScore;
        }

        public Map<String, String> getSkillImportance() {
            return skillImportance;
        }

        public double getWeightedSkillScore() {
            return weightedSkillScore;
        }
    }

    public WeightingResult calculateWeightedScore(String jdText, List<String> jdSkills, List<String> matchedSkills) {
        if (jdSkills == null || jdSkills.isEmpty()) {
            return new WeightingResult(new HashMap<>(), 0.0);
        }

        String lowerJd = jdText.toLowerCase();
        Map<String, String> skillImportance = new HashMap<>();
        Map<String, Integer> skillWeights = new HashMap<>();

        for (String skill : jdSkills) {
            String importance = classifyImportance(lowerJd, skill);
            skillImportance.put(skill, importance);
            skillWeights.put(skill, getWeight(importance));
        }

        double totalWeight = 0;
        for (int weight : skillWeights.values()) {
            totalWeight += weight;
        }

        double matchedWeight = 0;
        for (String matchedSkill : matchedSkills) {
            // Use matching from jdSkills to ensure correct weight is picked
            matchedWeight += skillWeights.getOrDefault(matchedSkill, 0);
        }

        double weightedScore = (totalWeight > 0) ? (matchedWeight / totalWeight) * 100 : 0;

        return new WeightingResult(skillImportance, weightedScore);
    }

    private String classifyImportance(String jdText, String skill) {
        String lowerSkill = skill.toLowerCase();
        String highestImportance = "OPTIONAL";

        int index = jdText.indexOf(lowerSkill);
        while (index >= 0) {
            // Extract context (e.g., 100 characters before the skill)
            int start = Math.max(0, index - 100);
            String context = jdText.substring(start, index);

            // Required markers
            if (containsAny(context, "required", "must have", "mandatory", "minimum", "highly required", "essential")) {
                return "REQUIRED"; // Highest possible, can return early
            }

            // Preferred markers
            if (containsAny(context, "preferred", "nice to have", "plus", "good to have", "desired", "advantage")) {
                highestImportance = "PREFERRED";
            }

            index = jdText.indexOf(lowerSkill, index + 1);
        }

        return highestImportance;
    }

    private boolean containsAny(String context, String... keywords) {
        for (String keyword : keywords) {
            if (context.contains(keyword))
                return true;
        }
        return false;
    }

    private int getWeight(String importance) {
        return switch (importance) {
            case "REQUIRED" -> WEIGHT_REQUIRED;
            case "PREFERRED" -> WEIGHT_PREFERRED;
            default -> WEIGHT_OPTIONAL;
        };
    }
}
