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

    @Override
    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserProfileResponse)
                .toList();
    }

    @Override
    @Transactional
    public UserUsageResponse updateUserUsage(Long userId, Integer analysisCount, Integer generationCount,
            Integer usageLimit, String role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));

        if (analysisCount != null)
            user.setAnalysisCount(analysisCount);
        if (generationCount != null)
            user.setGenerationCount(generationCount);
        if (usageLimit != null)
            user.setUsageLimit(usageLimit);
        if (role != null) {
            user.setRole(Role.valueOf(role.toUpperCase()));
        }

        User savedUser = userRepository.save(user);
        return new UserUsageResponse(
                savedUser.getId(),
                savedUser.getAnalysisCount(),
                savedUser.getGenerationCount(),
                savedUser.getUsageLimit(),
                savedUser.getRole().name());
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
}
