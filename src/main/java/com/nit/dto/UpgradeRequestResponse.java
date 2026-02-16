package com.nit.dto;

import java.time.Instant;

public record UpgradeRequestResponse(Long id,Long userId,String username,String userEmail,String reason,String status,Instant createdAt){}
