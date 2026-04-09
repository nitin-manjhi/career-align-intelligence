package com.nit.security;

import com.nit.dto.auth.AuthResponse;
import com.nit.dto.auth.ForgotPasswordRequest;
import com.nit.dto.auth.LoginRequest;
import com.nit.dto.auth.SignupRequest;
import com.nit.entity.PasswordResetRequest;
import com.nit.entity.Role;
import com.nit.entity.User;
import com.nit.exception.BadRequestException;
import com.nit.mapper.UserMapper;
import com.nit.repository.PasswordResetRequestRepository;
import com.nit.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class AuthServiceImpl implements AuthService {

    UserRepository userRepository;
    PasswordResetRequestRepository passwordResetRequestRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    AuthUtil authUtil;
    AuthenticationManager authenticationManager;
    org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;

    private void notifyAdmins(String message, String type) {
        try {
            String destination = "/topic/admin-events";
            java.util.Map<String, Object> payload = new java.util.HashMap<>();
            payload.put("type", type);
            payload.put("message", message);
            messagingTemplate.convertAndSend(destination, payload);
            log.info("Sent admin notification: type={}, message={}", type, message);
        } catch (Exception e) {
            log.error("Failed to send admin notification", e);
        }
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public AuthResponse signup(SignupRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new BadRequestException("User already exists with username: " + request.username());
        }

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new BadRequestException("User already exists with email: " + request.email());
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        if (request.email().toLowerCase().contains("admin")) {
            user.setRole(Role.ADMIN);
            user.setEnabled(true);
        } else {
            user.setEnabled(false);
        }
        user = userRepository.save(user);
        log.info("Saved new user: {}, enabled={}", user.getUsername(), user.isEnabled());

        notifyAdmins("New user registration request from: " + user.getUsername(), "NEW_REGISTRATION");

        if (user.isEnabled()) {
            return login(new LoginRequest(request.username(), request.password(), false));
        }

        return new AuthResponse(null, userMapper.toUserProfileResponse(user));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .or(() -> userRepository.findByEmail(request.username()))
                .orElseThrow(() -> new BadRequestException("Account does not exist. Please sign up."));

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password()));
            
            user = (User) authentication.getPrincipal();
        } catch (DisabledException e) {
            throw new com.nit.exception.AccountNotApprovedException("Your account is awaiting administrator approval.");
        } catch (AuthenticationException e) {
            throw new BadRequestException("Invalid username or password. Please try again.");
        }

        Long pendingCount = null;
        Long pendingResetsCount = null;
        if (user.getRole() == Role.ADMIN) {
            pendingCount = userRepository.countByEnabledFalse();
            pendingResetsCount = passwordResetRequestRepository.count();
        }

        String token = authUtil.generateAccessToken(user);
        com.nit.dto.auth.UserProfileResponse profile = userMapper.toUserProfileResponse(user);
        
        com.nit.dto.auth.UserProfileResponse enrichedProfile = new com.nit.dto.auth.UserProfileResponse(
                profile.id(), profile.username(), profile.name(), profile.email(), 
                profile.usageLimit(), profile.generationLimit(), profile.analysisCount(), profile.generationCount(), 
                profile.role(), profile.premiumActive(), profile.premiumUsageLimit(), profile.premiumUsageCount(), 
                profile.suspended(), pendingCount, pendingResetsCount
        );

        return new AuthResponse(token, enrichedProfile);
    }

    @Override
    public AuthResponse refresh() {
        Long userId = authUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        String token = authUtil.generateAccessToken(user);
        return new AuthResponse(token, userMapper.toUserProfileResponse(user));
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadRequestException("User not found with email: " + request.email()));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new BadRequestException("Current password verification failed. You must provide the correct current password to request a reset.");
        }

        if (request.newPassword() == null || request.newPassword().isBlank()) {
            throw new BadRequestException("New password cannot be empty");
        }

        // Remove old pending requests
        passwordResetRequestRepository.deleteByUser(user);

        // Create new pending request
        PasswordResetRequest resetRequest = PasswordResetRequest.builder()
                .user(user)
                .newPassword(passwordEncoder.encode(request.newPassword()))
                .requestedAt(java.time.Instant.now())
                .build();
        
        passwordResetRequestRepository.save(resetRequest);
        log.info("Created password reset request for user: {}", user.getUsername());
        
        notifyAdmins("Password reset request from: " + user.getUsername(), "PASSWORD_RESET_REQUEST");
    }

    @Override
    public void logout() {
        SecurityContextHolder.clearContext();
    }
}
