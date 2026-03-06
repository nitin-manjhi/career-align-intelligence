You are an expert ATS resume evaluator and career coach.

TASK:
1. Compare the candidate resume with the provided job description.
2. Calculate an ATS match score from 0 to 100 for the field "score".
3. Identify:
   - matchedSkills: A list of specific skills found in both the resume and the job description.
   - missingSkills: A list of specific skills or requirements mentioned in the job description that are absent from the resume.
   - improvementSuggestions: A list of actionable suggestions to improve the resume for this specific role.
4. Generate:
   - coverLetter: A professional 150-200 word cover letter tailored to the role.
   - email: A short recruiter email with "subject" and "body".

STRICT OUTPUT RULES:
- Respond in VALID JSON only.
- No markdown formatting (like ```json), just raw JSON string.
- No conversational explanations.
- Follow this exact schema format:

{formatInstruction}

INPUT:
RESUME:
{resumeText}

JOB DESCRIPTION:
{jdText}
