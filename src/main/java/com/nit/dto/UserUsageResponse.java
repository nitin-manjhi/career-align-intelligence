package com.nit.dto;

public record UserUsageResponse(Long id, Integer analysisCount, Integer generationCount, Integer usageLimit,
                Integer generationLimit, String role, boolean premiumActive, Integer premiumUsageLimit,
                Integer premiumUsageCount) {
}
