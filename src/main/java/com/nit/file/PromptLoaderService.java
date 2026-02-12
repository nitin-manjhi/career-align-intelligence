package com.nit.file;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class PromptLoaderService {
    public String loadPrompt(String fileName) {
        try (InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream("prompts/" + fileName)) {

            if (is == null) {
                throw new RuntimeException("Prompt file not found: " + fileName);
            }

            return new String(is.readAllBytes(), StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new RuntimeException("Failed to read prompt file", e);
        }
    }
}
