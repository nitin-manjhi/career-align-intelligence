package com.nit.service;

import com.nit.dto.UpgradeRequestResponse;
import com.nit.dto.UserUsageResponse;
import com.nit.dto.auth.UserProfileResponse;
import java.util.List;

public interface AdminService {
    List<UserProfileResponse> getAllUsers();

    UserUsageResponse updateUserUsage(Long userId, Integer analysisCount, Integer generationCount, Integer usageLimit,
            String role, Boolean premiumActive, Integer premiumUsageLimit, Integer premiumUsageCount);

    List<UpgradeRequestResponse> getPendingUpgradeRequests();

    void processUpgradeRequest(Long requestId, String status, Integer newLimit);

    void createUpgradeRequest(String reason);
}
