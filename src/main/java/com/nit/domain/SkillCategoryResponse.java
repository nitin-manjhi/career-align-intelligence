package com.nit.domain;

import lombok.Data;
import java.util.List;

@Data
public class SkillCategoryResponse {
    private List<CategorySkills> categories;

    @Data
    public static class CategorySkills {
        private String category;
        private List<String> skills;
    }
}
