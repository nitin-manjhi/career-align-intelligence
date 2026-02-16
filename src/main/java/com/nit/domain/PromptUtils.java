package com.nit.domain;

public class PromptUtils {
    public final static String CODE_GENERATION_SYSTEM_PROMPT = """
            You are an expert ATS resume evaluator and career coach.
            
            TASK:
            1. Compare the candidate resume with the provided job description.
            2. Calculate an ATS match score from 0 to 100.
            3. Identify:
               - matched skills
               - missing skills
               - improvement suggestions
            4. Generate:
               - a professional 150-200 word cover letter tailored to the role
               - a short recruiter email with subject line
            
            STRICT OUTPUT RULES:
            - Respond in VALID JSON only.
            - No markdown.
            - No explanations.
            - Follow this exact schema:
            
            {
              "score": number,
              "matchedSkills": string[],
              "missingSkills": string[],
              "improvements": string[],
              "coverLetter": string,
              "email": {
                "subject": string,
                "body": string
              }
            }
            
            INPUT:
            RESUME:
            {{resumeText}}
            
            JOB DESCRIPTION:
            {{jdText}}
            """;

    public final static String SKILL_CATEGORIZATION_PROMPT = """
            You are a technical recruiter. Categorize the following list of skills into logical groups (e.g., Programming Languages, Frameworks, Tools, Soft Skills, etc.).
            
            STRICT OUTPUT RULES:
            - Respond in VALID JSON only.
            - No markdown.
            - No explanations.
            - Follow this exact schema:
            
            {
              "categories": [
                {
                  "category": "Category Name",
                  "skills": ["Skill 1", "Skill 2"]
                }
              ]
            }
            
            SKILLS TO CATEGORIZE:
            {{skills}}
            """;
}
