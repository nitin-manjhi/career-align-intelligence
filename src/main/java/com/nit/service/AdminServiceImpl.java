package com.nit.service;

import com.nit.entity.User;
import com.nit.entity.UpgradeRequest;
import com.nit.entity.Role;
import com.nit.dto.UpgradeRequestResponse;
import com.nit.dto.UserUsageResponse;
import com.nit.dto.auth.UserProfileResponse;
import com.nit.exception.ResourceNotFoundException;
import com.nit.repository.UpgradeRequestRepository;
import com.nit.repository.UserRepository;
import com.nit.security.AuthUtil;
import com.nit.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final UpgradeRequestRepository upgradeRequestRepository;
    private final AuthUtil authUtil;
    private final UserMapper userMapper;
    private final org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;

    private void notifyQuotaUpdate(Long userId, String message) {
        String destination = "/topic/notifications-" + userId;
        java.util.Map<String, Object> payload = new java.util.HashMap<>();
        payload.put("type", "QUOTA_UPDATE");
        payload.put("message", message);
        messagingTemplate.convertAndSend(destination, payload);
    }

    private void notifyAdmins(String message, String type) {
        String destination = "/topic/admin-events";
        java.util.Map<String, Object> payload = new java.util.HashMap<>();
        payload.put("type", type);
        payload.put("message", message);
        messagingTemplate.convertAndSend(destination, payload);
    }

    @Override
    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserProfileResponse)
                .toList();
    }

    @Override
    @Transactional
    public UserUsageResponse updateUserUsage(Long userId, Integer analysisCount, Integer generationCount,
            Integer usageLimit, Integer generationLimit, String role, Boolean premiumActive, Integer premiumUsageLimit,
            Integer premiumUsageCount, Boolean suspended) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));

        if (analysisCount != null)
            user.setAnalysisCount(analysisCount);
        if (generationCount != null)
            user.setGenerationCount(generationCount);
        if (usageLimit != null)
            user.setUsageLimit(usageLimit);
        if (generationLimit != null)
            user.setGenerationLimit(generationLimit);
        if (role != null) {
            user.setRole(Role.valueOf(role.toUpperCase()));
        }
        if (premiumActive != null) {
            user.setPremiumActive(premiumActive);
        }
        if (premiumUsageLimit != null) {
            user.setPremiumUsageLimit(premiumUsageLimit);
        }
        if (premiumUsageCount != null) {
            user.setPremiumUsageCount(premiumUsageCount);
        }
        if (suspended != null) {
            user.setSuspended(suspended);
        }

        User savedUser = userRepository.save(user);

        // Notify user about update via WebSocket
        notifyQuotaUpdate(savedUser.getId(), "Your account status or usage limits have been updated.");

        return new UserUsageResponse(
                savedUser.getId(),
                savedUser.getAnalysisCount(),
                savedUser.getGenerationCount(),
                savedUser.getUsageLimit(),
                savedUser.getGenerationLimit(),
                savedUser.getRole().name(),
                savedUser.isPremiumActive(),
                savedUser.getPremiumUsageLimit(),
                savedUser.getPremiumUsageCount(),
                savedUser.isSuspended());
    }

    @Override
    public List<UpgradeRequestResponse> getPendingUpgradeRequests() {
        return upgradeRequestRepository.findByStatusOrderByCreatedAtDesc(UpgradeRequest.RequestStatus.PENDING)
                .stream()
                .map(req -> new UpgradeRequestResponse(
                        req.getId(),
                        req.getUser().getId(),
                        req.getUser().getUsername(),
                        req.getUser().getEmail(),
                        req.getReason(),
                        req.getStatus().name(),
                        req.getCreatedAt()))
                .toList();
    }

    @Override
    @Transactional
    public void processUpgradeRequest(Long requestId, String status, Integer newLimit) {
        UpgradeRequest request = upgradeRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("UpgradeRequest", requestId.toString()));

        request.setStatus(UpgradeRequest.RequestStatus.valueOf(status.toUpperCase()));

        if (request.getStatus() == UpgradeRequest.RequestStatus.APPROVED && newLimit != null) {
            User user = request.getUser();
            user.setUsageLimit(newLimit);
            userRepository.save(user);
            notifyQuotaUpdate(user.getId(), "Your upgrade request has been approved!");
        } else if (request.getStatus() == UpgradeRequest.RequestStatus.REJECTED) {
            notifyQuotaUpdate(request.getUser().getId(), "Your upgrade request was reviewed by an admin.");
        }

        upgradeRequestRepository.save(request);
    }

    @Override
    @Transactional
    public void createUpgradeRequest(String reason) {
        Long userId = authUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));

        UpgradeRequest request = UpgradeRequest.builder()
                .user(user)
                .reason(reason)
                .status(UpgradeRequest.RequestStatus.PENDING)
                .build();

        upgradeRequestRepository.save(request);
    }

    @Override
    @Transactional
    public void requestUnsuspension() {
        Long userId = authUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));
        
        notifyAdmins("User " + user.getUsername() + " has requested account unsuspension.", "UNSUSPENSION_REQUEST");
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", userId.toString());
        }
        upgradeRequestRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
    }
}
