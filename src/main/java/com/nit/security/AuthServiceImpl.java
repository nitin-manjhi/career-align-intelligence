package com.nit.security;

import com.nit.dto.auth.AuthResponse;
import com.nit.dto.auth.LoginRequest;
import com.nit.dto.auth.SignupRequest;
import com.nit.dto.auth.ForgotPasswordRequest;
import com.nit.entity.Role;
import com.nit.entity.User;
import com.nit.error.BadRequestException;
import com.nit.mapper.UserMapper;
import com.nit.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthServiceImpl implements AuthService {

    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    AuthUtil authUtil;
    AuthenticationManager authenticationManager;

    @Override
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
        }
        user = userRepository.save(user);

        String token = authUtil.generateAccessToken(user);
        return new AuthResponse(token, userMapper.toUserProfileResponse(user));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        User user = (User) authentication.getPrincipal();

        String token = authUtil.generateAccessToken(user);
        return new AuthResponse(token, userMapper.toUserProfileResponse(user));
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
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadRequestException("User not found with email: " + request.email()));

        // In a real application, you would generate a reset token and send an email.
        // For now, we'll just simulate it.
        System.out.println("Sending password reset link to: " + request.email());
        // TODO: Implement email sending logic here
    }

    @Override
    public void logout() {
        SecurityContextHolder.clearContext();
    }
}
