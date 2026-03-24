package com.nit.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Service
@Slf4j
public class DatabaseAdminServiceImpl implements DatabaseAdminService {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${app.db.automation.container-name}")
    private String dbContainerName;

    @Value("${app.db.automation.backup-dir}")
    private String backupDir;

    private String getDbName() {
        // jdbc:postgresql://localhost:5433/resumeDb -> resumeDb
        return dbUrl.substring(dbUrl.lastIndexOf("/") + 1);
    }

    @Override
    public String backupDatabase() {
        String dbName = getDbName();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = dbName + "_" + timestamp + ".sql.gz";
        
        Path backupPath = Paths.get(backupDir);
        try {
            if (!Files.exists(backupPath)) {
                Files.createDirectories(backupPath);
            }
        } catch (IOException e) {
            log.error("Failed to create backup directory: {}", backupDir, e);
            throw new RuntimeException("Failed to create backup directory", e);
        }

        File backupFile = backupPath.resolve(fileName).toFile();
        
        // Command: docker exec -i <container> pg_dump -U <user> <db_name>
        // We set PGPASSWORD environment variable to avoid prompt
        ProcessBuilder pb = new ProcessBuilder(
                "docker", "exec", "-i", dbContainerName,
                "pg_dump", "-U", dbUser, dbName
        );
        pb.environment().put("PGPASSWORD", dbPassword);
        
        log.info("Starting database backup for {} to {}", dbName, backupFile.getAbsolutePath());

        try {
            Process process = pb.start();
            
            try (InputStream is = process.getInputStream();
                 FileOutputStream fos = new FileOutputStream(backupFile);
                 GZIPOutputStream gzos = new GZIPOutputStream(fos)) {
                
                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer)) > 0) {
                    gzos.write(buffer, 0, len);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                // Read error stream
                String errorMsg = new String(process.getErrorStream().readAllBytes());
                log.error("Backup failed with exit code {}: {}", exitCode, errorMsg);
                throw new RuntimeException("Backup failed: " + errorMsg);
            }
            
            log.info("Database backup completed successfully: {}", backupFile.getAbsolutePath());
            return backupFile.getAbsolutePath();
            
        } catch (IOException | InterruptedException e) {
            log.error("Error during database backup", e);
            throw new RuntimeException("Error during database backup", e);
        }
    }

    @Override
    public void restoreDatabase(String backupFilePath) {
        File backupFile = new File(backupFilePath);
        if (!backupFile.exists()) {
            throw new RuntimeException("Backup file not found: " + backupFilePath);
        }

        String dbName = getDbName();
        
        // Command: docker exec -i <container> psql -U <user> <db_name>
        ProcessBuilder pb = new ProcessBuilder(
                "docker", "exec", "-i", dbContainerName,
                "psql", "-U", dbUser, dbName
        );
        pb.environment().put("PGPASSWORD", dbPassword);

        log.info("Starting database restore for {} from {}", dbName, backupFilePath);

        try {
            Process process = pb.start();
            
            try (FileInputStream fis = new FileInputStream(backupFile);
                 GZIPInputStream gzis = new GZIPInputStream(fis);
                 OutputStream os = process.getOutputStream()) {
                
                byte[] buffer = new byte[1024];
                int len;
                while ((len = gzis.read(buffer)) > 0) {
                    os.write(buffer, 0, len);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                String errorMsg = new String(process.getErrorStream().readAllBytes());
                log.error("Restore failed with exit code {}: {}", exitCode, errorMsg);
                throw new RuntimeException("Restore failed: " + errorMsg);
            }
            
            log.info("Database restore completed successfully.");
            
        } catch (IOException | InterruptedException e) {
            log.error("Error during database restore", e);
            throw new RuntimeException("Error during database restore", e);
        }
    }

    @Override
    public void restoreFromUpload(org.springframework.web.multipart.MultipartFile file) {
        log.info("Received background restore upload for file: {}", file.getOriginalFilename());
        
        Path backupPath = Paths.get(backupDir);
        String tempFileName = "upload_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File tempFile = backupPath.resolve(tempFileName).toFile();
        
        try {
            if (!Files.exists(backupPath)) {
                Files.createDirectories(backupPath);
            }
            file.transferTo(tempFile);
            log.info("Saved uploaded backup to {}", tempFile.getAbsolutePath());
            
            // Perform restore
            restoreDatabase(tempFile.getAbsolutePath());
            
            // Clean up temp file
            Files.deleteIfExists(tempFile.toPath());
            
        } catch (IOException e) {
            log.error("Failed to handle backup upload", e);
            throw new RuntimeException("Failed to handle backup upload", e);
        }
    }

    @Override
    @Scheduled(cron = "${app.db.automation.cron}")
    public void scheduleDailyBackup() {
        log.info("Executing scheduled database backup...");
        try {
            backupDatabase();
        } catch (Exception e) {
            log.error("Scheduled backup failed", e);
        }
    }
}
