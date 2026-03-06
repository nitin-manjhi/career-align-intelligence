package com.nit.dto.auth;

public record UserProfileResponse(Long id, String username, String name, String email, Integer usageLimit,
        Integer analysisCount, Integer generationCount, String role, boolean premiumActive, Integer premiumUsageLimit,
        Integer premiumUsageCount) {
}
