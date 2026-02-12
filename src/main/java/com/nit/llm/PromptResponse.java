package com.nit.llm;

public class PromptResponse {
    public final static String RESPONSE = """
            ```json
                               {
                                 "score": 78,
                                 "matchedSkills": [
                                   "Java",
                                   "SQL",
                                   "Eclipse",
                                   "Oracle",
                                   "REST API Development",
                                   "Git",
                                   "JUnit",
                                   "MySQL",
                                   "PostgreSQL",
                                   "Docker"
                                 ],
                                 "missingSkills": [
                                   "Java 8+",
                                   "Spring Boot",
                                   "Spring MVC",
                                   "Spring Data JPA",
                                   "Spring Security",
                                   "Microservices Architecture",
                                   "Hibernate / JPA",
                                   "AWS: EC2",
                                   "AWS: S3",
                                   "AWS: RDS",
                                   "AWS: Lambda",
                                   "AWS: IAM",
                                   "CloudWatch",
                                   "CI/CD pipelines",
                                   "Jenkins",
                                   "GitHub Actions",
                                   "GitLab CI",
                                   "Kafka",
                                   "RabbitMQ",
                                   "MongoDB",
                                   "DynamoDB",
                                   "Swagger / Postman",
                                   "Redis",
                                   "Memcached",
                                   "ELK Stack",
                                   "Prometheus",
                                   "Grafana",
                                   "Kubernetes (K8s)",
                                   "API Gateway",
                                   "Serverless",
                                   "GraphQL basics",
                                   "OWASP Top 10 awareness",
                                   "Feature flagging tools",
                                   "LaunchDarkly",
                                   "Unleash"
                                 ],
                                 "improvements": [
                                   "Add specific versions of Java (Java 8+).",
                                   "Highlight Spring Boot and Spring Framework skills.",
                                   "Explicitly mention experience with Microservices architecture.",
                                   "Add experience with cloud platforms, specifically AWS.",
                                   "Mention experience with CI/CD tools (Jenkins, GitHub Actions, GitLab CI).",
                                   "Include experience with messaging queues (Kafka, RabbitMQ).",
                                   "Add experience with NoSQL databases (MongoDB, DynamoDB).",
                                   "Mention testing frameworks like JUnit and Mockito.",
                                   "Showcase experience with API testing tools like Postman/Swagger.",
                                   "Incorporate keywords like 'RESTful APIs,' 'ORM,' and 'scalable architecture.'",
                                   "Quantify achievements with more specific metrics and impact.",
                                   "Tailor work experience descriptions to align with the job description's responsibilities.",
                                   "Consider adding a section for 'Technical Proficiency' to showcase specific technologies and skill levels."
                                 ],
                                 "newResume": "Nadia Delgado\\nJava Developer\\n\\nn.delgado@email.com (123) 456-7890 Detroit, MI | LinkedIn\\n\\nSUMMARY\\n\\nHighly motivated Java Developer with 6+ years of experience in designing, developing, and maintaining scalable backend applications. Proven ability to deliver high-performance solutions utilizing Java, Spring Boot, and Microservices architectures. Expertise in relational databases (MySQL, PostgreSQL) and experience with cloud technologies like AWS. Committed to continuous learning and implementing best practices for secure and efficient code.\\n\\nEXPERIENCE\\n\\nDeloitte - Java Developer May 2018 - Current, Lansing, MI\\n• Designed and implemented software solutions, increasing system performance efficiency by 27%.\\n• Managed 100+ deliverables across short-term sprints and long-term software deployments, ensuring timely delivery.\\n• Developed and executed 300+ test procedures for software components, maintaining high quality standards.\\n\\nPerficient - Junior Java Developer January 2016 - April 2018, Ann Arbor\\n• Designed and coded 60+ unit/integration tests following Perficient methodology, enhancing code reliability.\\n• Optimized system performance through configuration management and version control during 20+ changes.\\n• Created 70+ detailed design documents, unit test plans, and well-documented code for a 12-week summer intern program.\\n\\nArup - Software Developer Intern June 2015 - January 2016, Ann Arbor, MI\\n• Collaborated with 15+ experts to analyze datasets and create data visualizations for improved decision-making.\\n• Conducted strategic research and identified emerging technologies to support R&D initiatives.\\n\\nEDUCATION\\n\\nUniversity of Michigan - B.S., Computer Science September 2011 - June 2015, Ann Arbor, MI\\n\\nSKILLS\\n\\nCore Java, Java 8+, Spring Boot, Spring MVC, Microservices, REST API Development, Hibernate/JPA, SQL, MySQL, PostgreSQL, Git, JUnit, Docker\\nCloud: AWS (EC2, S3, RDS),  \\nTools: Eclipse, Oracle, JavaScript, Angular.js, HTML, CSS, UNIX, React.js",
                                 "coverLetter": "Dear [Hiring Manager Name],\\n\\nI am writing to express my keen interest in the Java Developer position at [Company Name], as advertised on [Platform]. With over six years of experience in backend development, specializing in Java and Spring Boot, I am confident I possess the skills and experience to excel in this role and contribute significantly to your team.\\n\\nIn my previous role at Deloitte, I designed and implemented software solutions that resulted in a 27% increase in system performance, demonstrating my ability to build high-performance applications. I am proficient in developing RESTful APIs, managing microservices architectures, and working with relational databases like MySQL and PostgreSQL. I also have experience deploying applications on AWS. I am eager to leverage my expertise to build scalable and reliable backend applications.\\n\\nI am a fast learner, passionate about new technologies, and thrive in collaborative environments. Thank you for your time and consideration. I look forward to hearing from you soon.\\n\\nSincerely,\\nNadia Delgado",
                                 "email": {
                                   "subject": "Java Developer Application - Nadia Delgado",
                                   "body": "Dear [Recruiter Name],\\n\\nI am writing to express my interest in the Java Developer position at [Company Name]. My experience in Java, Spring Boot, and backend development aligns well with the requirements outlined in the job description.  I’ve attached my resume for your review and welcome the opportunity to discuss how I can contribute to your team.\\n\\nThank you for your time and consideration.\\n\\nSincerely,\\nNadia Delgado"
                                 }
                               }
                               ```
            """;
}
