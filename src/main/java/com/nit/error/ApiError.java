package com.nit.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
@Getter
@Builder
@AllArgsConstructor
public class ApiError {

    private String error;        // short code
    private String message;      // human readable
    private int status;          // HTTP status
    private Instant timestamp;   // when happened
    private String path;         // request URI
}
