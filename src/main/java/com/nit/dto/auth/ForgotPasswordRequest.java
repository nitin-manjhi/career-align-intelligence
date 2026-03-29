package com.nit.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ForgotPasswordRequest(
    @NotBlank @Email @JsonProperty("email") String email,
    @NotBlank @JsonProperty("oldPassword") String oldPassword,
    @NotBlank @Size(min = 6) @JsonProperty("newPassword") String newPassword
) {}
