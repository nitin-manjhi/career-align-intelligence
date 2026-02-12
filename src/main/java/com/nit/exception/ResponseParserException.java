package com.nit.exception;

public class ResponseParserException extends RuntimeException {
    public ResponseParserException(String message) {
        super(message);
    }

    public ResponseParserException(String message, Throwable cause) {
        super(message, cause);
    }
}
