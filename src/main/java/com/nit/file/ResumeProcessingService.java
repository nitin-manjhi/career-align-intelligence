package com.nit.file;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ResumeProcessingService {
    /**
     * Extracts structured information from a resume file.
     *
     * @param file The resume file to be processed, typically in PDF or DOCX format.
     * @return A structured representation of the resume data, such as a JSON string or a custom object.
     */
    String extractResumeData(final MultipartFile file);

    byte[] generateOnePageResume(UUID uuid);

    byte[] generateCoverLetterPdf(UUID uuid);
}

