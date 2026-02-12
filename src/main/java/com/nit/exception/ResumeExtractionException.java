package com.nit.exception;

public class ResumeExtractionException extends RuntimeException {
    public ResumeExtractionException(String message) {
        super(message);
    }

    public ResumeExtractionException(String message, Throwable cause) {
        super(message, cause);
    }
}
