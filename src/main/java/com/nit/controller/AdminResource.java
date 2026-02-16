package com.nit.controller;

import com.nit.entity.User;
import com.nit.dto.UpgradeRequestResponse;
import com.nit.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminResource {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PutMapping("/users/{userId}/usage")
    public ResponseEntity<User> updateUserUsage(
            @PathVariable Long userId,
            @RequestParam(required = false) Integer analysisCount,
            @RequestParam(required = false) Integer generationCount,
            @RequestParam(required = false) Integer usageLimit) {
        return ResponseEntity.ok(adminService.updateUserUsage(userId, analysisCount, generationCount, usageLimit));
    }

    @GetMapping("/upgrade-requests")
    public ResponseEntity<List<UpgradeRequestResponse>> getPendingUpgradeRequests() {
        return ResponseEntity.ok(adminService.getPendingUpgradeRequests());
    }

    @PutMapping("/upgrade-requests/{requestId}")
    public ResponseEntity<Void> processUpgradeRequest(
            @PathVariable Long requestId,
            @RequestParam String status,
            @RequestParam(required = false) Integer newLimit) {
        adminService.processUpgradeRequest(requestId, status, newLimit);
        return ResponseEntity.ok().build();
    }

}
