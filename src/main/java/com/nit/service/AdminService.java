package com.nit.service;

import com.nit.entity.User;
import com.nit.dto.UpgradeRequestResponse;
import java.util.List;

public interface AdminService {
    List<User> getAllUsers();

    User updateUserUsage(Long userId, Integer analysisCount, Integer generationCount, Integer usageLimit);

    List<UpgradeRequestResponse> getPendingUpgradeRequests();

    void processUpgradeRequest(Long requestId, String status);

    void createUpgradeRequest(String reason);
}
