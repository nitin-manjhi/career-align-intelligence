package com.nit.security;

import com.nit.dto.auth.AuthResponse;
import com.nit.dto.auth.LoginRequest;
import com.nit.dto.auth.SignupRequest;

public interface AuthService {
    AuthResponse signup(SignupRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh();

    void logout();
}
