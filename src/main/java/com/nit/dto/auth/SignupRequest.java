package com.nit.dto.auth;


public record SignupRequest(
        String username,
        String name,
        String password,
        String email
) {
}
