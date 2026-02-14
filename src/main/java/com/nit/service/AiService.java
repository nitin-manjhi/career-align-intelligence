package com.nit.service;

import com.nit.domain.AIResponse;

public interface AiService {
    AIResponse analyzeResume(String resumeText, String jdText);
}
