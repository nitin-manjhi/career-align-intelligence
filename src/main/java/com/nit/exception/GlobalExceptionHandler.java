package com.nit.exception;

import com.nit.error.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

        // ===== Helper to build response =====
        private ResponseEntity<ApiError> buildError(
                        HttpStatus status,
                        String error,
                        String message,
                        HttpServletRequest request) {

                ApiError body = ApiError.builder()
                                .error(error)
                                .message(message)
                                .status(status.value())
                                .timestamp(Instant.now())
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(status).body(body);
        }

        // ===== Rate limit =====
        @ExceptionHandler(RateLimitExceededException.class)
        public ResponseEntity<ApiError> handleRateLimit(
                        RateLimitExceededException ex,
                        HttpServletRequest request) {

                return buildError(
                                HttpStatus.TOO_MANY_REQUESTS,
                                "RATE_LIMIT_EXCEEDED",
                                ex.getMessage(),
                                request);
        }

        // ===== Resource not found =====
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiError> handleNotFound(
                        ResourceNotFoundException ex,
                        HttpServletRequest request) {

                return buildError(
                                HttpStatus.NOT_FOUND,
                                "RESOURCE_NOT_FOUND",
                                ex.getMessage(),
                                request);
        }

        // ===== Bad request =====
        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ApiError> handleBadRequest(
                        BadRequestException ex,
                        HttpServletRequest request) {

                return buildError(
                                HttpStatus.BAD_REQUEST,
                                "BAD_REQUEST",
                                ex.getMessage(),
                                request);
        }

        // ===== Catch-all (VERY IMPORTANT) =====
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiError> handleGeneric(
                        Exception ex,
                        HttpServletRequest request) {

                // log full error internally (never expose)
                log.error("Unhandled exception", ex);

                return buildError(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "INTERNAL_SERVER_ERROR",
                                "Something went wrong. Please try again later.",
                                request);
        }
}
