package com.nit.dto.auth;

import java.time.Instant;

public record PasswordResetRequestResponse(
    Long id,
    Long userId,
    String username,
    Instant requestedAt
) {}
