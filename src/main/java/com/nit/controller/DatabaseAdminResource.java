package com.nit.controller;

import com.nit.service.DatabaseAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/db")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class DatabaseAdminResource {

    private final DatabaseAdminService databaseAdminService;

    @PostMapping("/backup")
    public ResponseEntity<String> createBackup() {
        String backupPath = databaseAdminService.backupDatabase();
        return ResponseEntity.ok("Backup created successfully at: " + backupPath);
    }

    @PostMapping("/restore")
    public ResponseEntity<String> restoreFromBackup(@RequestParam String backupFilePath) {
        databaseAdminService.restoreDatabase(backupFilePath);
        return ResponseEntity.ok("Database restored successfully from: " + backupFilePath);
    }

    @PostMapping("/restore-upload")
    public ResponseEntity<String> restoreFromUpload(@RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        databaseAdminService.restoreFromUpload(file);
        return ResponseEntity.ok("Database restored successfully from uploaded file: " + file.getOriginalFilename());
    }
}
