package com.nit.dto;

public record UserUsageResponse(Long id,Integer analysisCount,Integer generationCount,Integer usageLimit,String role){}
