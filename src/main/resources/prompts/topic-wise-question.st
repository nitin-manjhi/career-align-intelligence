Task: Act as a Senior Technical Interview Panelist for {companyName}.

Candidate Profile:
Role: {role}
Experience: {experience}
Domain: {domain} (Optional)

Input JD: {jdText}
Resume: {resumeText}

Objective: > 1. Generate 15 topic-based interview questions (strictly no scenarios) that are frequently asked at this company for a candidate with {experience} of experience.
2. Categories must include: Java 17+ Internals, Spring Boot & Microservices, Angular 10+ Architecture, API Security (OAuth2/JWT), and SQL/Database Optimization.

CRITICAL RULES:
- You MUST return each question as a valid JSON object on a SINGLE LINE.
- DO NOT wrap the output in a JSON array.
- DO NOT include markdown backticks (```json).
- Each line must be exactly one JSON object with these fields: topic, question, technicalExpectation, goldenAnswer.

OUTPUT STRUCTURE:
{"topic": "...", "question": "...", "technicalExpectation": "...", "goldenAnswer": "..."}
{"topic": "...", "question": "...", "technicalExpectation": "...", "goldenAnswer": "..."}
