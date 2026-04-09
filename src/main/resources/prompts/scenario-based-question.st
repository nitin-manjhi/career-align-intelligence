Task: Act as a Senior Technical Interview Panelist for {companyName}.

Candidate Profile:
Role: {role}
Experience: {experience}
Domain: {domain} (Optional)

Input JD: {jdText}
Resume: {resumeText}

Objective: > 1. Generate 15 complex, scenario-based interview questions grounded in real-world production challenges.
2. Categories: Java 17+, Spring Boot, Angular, API Security, and SQL.

CRITICAL RULES:
- You MUST return each question as a valid JSON object on a SINGLE LINE.
- DO NOT wrap the output in a JSON array.
- DO NOT include markdown backticks (```json).
- Each line must be exactly one JSON object with these fields: topic, question, technicalExpectation, goldenAnswer.

OUTPUT STRUCTURE:
{"topic": "...", "question": "...", "technicalExpectation": "...", "goldenAnswer": "..."}
{"topic": "...", "question": "...", "technicalExpectation": "...", "goldenAnswer": "..."}
