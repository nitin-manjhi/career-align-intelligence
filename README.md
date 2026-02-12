# CareerAlign Intelligence

**CareerAlign Intelligence** is an AI-powered resume analysis and generation platform that evaluates a candidate’s resume against a job description, produces an ATS-optimized one-page resume, generates a tailored cover letter, and provides downloadable professional PDFs — all through a clean end-to-end workflow.

---

## 🚀 Key Features

* **Resume–JD Matching Engine**

  * Upload resume (PDF/DOCX)
  * Paste job description
  * AI calculates ATS compatibility score
  * Identifies matched and missing skills
  * Provides improvement suggestions

* **AI Resume & Cover Letter Generation**

  * Generates **truthful, ATS-optimized one-page resume**
  * Creates **role-specific professional cover letter**
  * Produces **recruiter-ready email template**

* **Structured AI Output**

  * Deterministic JSON schema for stability
  * Eliminates fragile text parsing
  * Enables reliable document rendering

* **Professional PDF Export**

  * Separate downloadable PDFs:

    * Resume
    * Cover Letter
  * Clean one-page recruiter-friendly layout

* **Persistent Analysis History**

  * Stores resume text, JD, score, and AI response
  * PostgreSQL + JSONB for flexible structured storage

* **Minimal Angular UI**

  * Upload → Analyze → Download flow
  * Real product usability, not just backend APIs

---

## 🏗️ Architecture Overview

```
Resume Upload + JD Input
        ↓
Text Extraction (Apache Tika)
        ↓
AI Analysis (Spring AI + LLM)
        ↓
Structured Resume JSON
        ↓
PostgreSQL Persistence (JSONB)
        ↓
PDF Rendering (OpenPDF)
        ↓
Angular UI for End-to-End Usage
```

---

## 🛠️ Tech Stack

### Backend

* Java 17+
* Spring Boot 3
* Spring AI (LLM integration)
* PostgreSQL + JSONB
* Apache Tika (resume parsing)
* OpenPDF (PDF generation)
* Maven

### Frontend

* Angular
* TypeScript
* HTTP Client + Forms

---

## 🎯 Problem Solved

Manual resume tailoring for every job is:

* Time-consuming
* Error-prone
* Inconsistent with ATS expectations

**CareerAlign Intelligence automates this entire workflow** using AI, producing recruiter-ready documents in seconds.

---

## 📌 Real-World Value

This project demonstrates:

* End-to-end **AI application architecture**
* **Structured LLM output design**
* **Document generation pipelines**
* **Production-style persistence with JSONB**
* **Full-stack integration (Spring Boot + Angular)**

---

## 🔮 Future Enhancements

* RAG-based career memory using pgvector
* Advanced resume formatting & templates
* Public deployment with shareable links
* Multi-role resume versioning

---

## 👤 Author

**Nitin Manjhi**
Full-Stack Developer | Java • Spring Boot • Angular • AI Integration

---

