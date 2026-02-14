package com.nit.service;

import org.springframework.web.multipart.MultipartFile;

public interface ResumeProcessingService {
    /**
     * Extracts structured information from a resume file.
     *
     * @param file The resume file to be processed, typically in PDF or DOCX format.
     * @return A structured representation of the resume data, such as a JSON string or a custom object.
     */
    String extractResumeData(final MultipartFile file);

}

