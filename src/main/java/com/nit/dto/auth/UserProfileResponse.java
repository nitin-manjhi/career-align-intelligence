package com.nit.dto.auth;

public record UserProfileResponse(
        Long id,
        String username,
        String name,
        String email
) {
}
