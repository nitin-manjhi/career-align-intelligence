package com.nit.controller;

import com.nit.dto.auth.AuthResponse;
import com.nit.dto.auth.ForgotPasswordRequest;
import com.nit.dto.auth.LoginRequest;
import com.nit.dto.auth.SignupRequest;
import com.nit.dto.auth.UserProfileResponse;
import com.nit.security.AuthService;
import com.nit.security.UserService;
import com.nit.service.AdminService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthResource {

    AuthService authService;
    UserService userService;
    AdminService adminService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh() {
        return ResponseEntity.ok(authService.refresh());
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile() {
        return ResponseEntity.ok(userService.getProfile(null));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        authService.logout();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upgrade-requests")
    public ResponseEntity<Void> createUpgradeRequest(@RequestParam String reason) {
        adminService.createUpgradeRequest(reason);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/request-unsuspension")
    public ResponseEntity<Void> requestUnsuspension() {
        adminService.requestUnsuspension();
        return ResponseEntity.ok().build();
    }

}
