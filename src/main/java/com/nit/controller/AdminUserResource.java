package com.nit.controller;

import com.nit.dto.auth.UserProfileResponse;
import com.nit.entity.User;
import com.nit.exception.ResourceNotFoundException;
import com.nit.mapper.UserMapper;
import com.nit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import com.nit.dto.auth.PasswordResetRequestResponse;
import com.nit.entity.PasswordResetRequest;
import com.nit.repository.PasswordResetRequestRepository;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserResource {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordResetRequestRepository passwordResetRequestRepository;
    private final org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;

    private void notifyUser(Long userId, String message, String type) {
        String destination = "/topic/notifications-" + userId;
        java.util.Map<String, Object> payload = new java.util.HashMap<>();
        payload.put("type", type);
        payload.put("message", message);
        messagingTemplate.convertAndSend(destination, payload);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<UserProfileResponse>> getPendingUsers() {
        List<UserProfileResponse> pendingUsers = userRepository.findAllByEnabledFalse()
                .stream()
                .map(u -> userMapper.toUserProfileResponse(u))
                .collect(Collectors.toList());
        return ResponseEntity.ok(pendingUsers);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<String> approveUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id.toString()));
        user.setEnabled(true);
        userRepository.save(user);
        
        notifyUser(user.getId(), "Your account has been approved! You can now login.", "ACCOUNT_APPROVED");
        
        return ResponseEntity.ok("User approved successfully: " + user.getUsername());
    }

    @DeleteMapping("/{id}/reject")
    public ResponseEntity<String> rejectUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id.toString()));
        if (!user.isEnabled()) {
            notifyUser(user.getId(), "Your registration request was rejected.", "ACCOUNT_REJECTED");
            userRepository.delete(user);
            return ResponseEntity.ok("User registration rejected and account deleted.");
        }
        return ResponseEntity.badRequest().body("Cannot reject an already enabled user.");
    }

    @GetMapping("/password-resets")
    public ResponseEntity<List<PasswordResetRequestResponse>> getPendingPasswordResets() {
        List<PasswordResetRequestResponse> resets = passwordResetRequestRepository.findAll()
                .stream()
                .map(r -> new PasswordResetRequestResponse(r.getId(), r.getUser().getId(), r.getUser().getUsername(), r.getRequestedAt()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(resets);
    }

    @PostMapping("/password-resets/{id}/approve")
    public ResponseEntity<String> approvePasswordReset(@PathVariable Long id) {
        PasswordResetRequest request = passwordResetRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reset Request", id.toString()));
        
        User user = request.getUser();
        user.setPassword(request.getNewPassword());
        userRepository.save(user);
        
        notifyUser(user.getId(), "Your password reset has been approved! You can now login with your new password.", "PASSWORD_RESET_APPROVED");
        
        passwordResetRequestRepository.delete(request);
        return ResponseEntity.ok("Password reset approved for user: " + user.getUsername());
    }

    @DeleteMapping("/password-resets/{id}/reject")
    public ResponseEntity<String> rejectPasswordReset(@PathVariable Long id) {
        PasswordResetRequest request = passwordResetRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reset Request", id.toString()));
        
        notifyUser(request.getUser().getId(), "Your password reset request was rejected. Please contact an admin if you think this is a mistake.", "PASSWORD_RESET_REJECTED");
        
        passwordResetRequestRepository.delete(request);
        return ResponseEntity.ok("Password reset request rejected.");
    }
}
