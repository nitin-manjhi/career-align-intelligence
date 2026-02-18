package com.nit.service;

import java.util.UUID;

public interface AiService {
    void analyzeResume(String resumeText, String jdText, UUID uuid);
    String categorizeSkills(java.util.List<String> skills);
}
