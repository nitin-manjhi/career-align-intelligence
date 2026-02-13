package com.nit.dto.auth;

public record AuthResponse(
        String token,
        UserProfileResponse user
) {

}
