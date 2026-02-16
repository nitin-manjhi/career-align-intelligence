package com.nit.security;

import com.nit.dto.auth.AuthResponse;
import com.nit.dto.auth.LoginRequest;
import com.nit.dto.auth.SignupRequest;
import com.nit.dto.auth.ForgotPasswordRequest;

public interface AuthService {
    AuthResponse signup(SignupRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh();

    void forgotPassword(ForgotPasswordRequest request);

    void logout();
}
