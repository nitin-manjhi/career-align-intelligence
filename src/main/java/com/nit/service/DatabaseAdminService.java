package com.nit.service;

import org.springframework.web.multipart.MultipartFile;

public interface DatabaseAdminService {
    String backupDatabase();
    void restoreDatabase(String backupFilePath);
    void restoreFromUpload(MultipartFile file);
    void scheduleDailyBackup();
}
