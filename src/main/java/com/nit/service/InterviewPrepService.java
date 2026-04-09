package com.nit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nit.dto.InterviewQuestionDTO;
import com.nit.entity.InterviewPrepResult;
import com.nit.repository.InterviewPrepResultRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class InterviewPrepService {

    private final ChatModel ollamaChatModel;
    private final ChatModel openAiChatModel;
    private final ChatModel googleGenAiChatModel;
    private final PromptLoaderService promptLoaderService;
    private final SimpMessagingTemplate messagingTemplate;
    private final InterviewPrepResultRepository resultRepository;
    private final ObjectMapper objectMapper;

    public InterviewPrepService(
            @Qualifier("ollamaChatModel") ChatModel ollamaChatModel,
            @Qualifier("openAiChatModel") ObjectProvider<ChatModel> openAiChatModel,
            @Qualifier("googleGenAiChatModel") ObjectProvider<ChatModel> googleGenAiChatModel,
            PromptLoaderService promptLoaderService,
            SimpMessagingTemplate messagingTemplate,
            InterviewPrepResultRepository resultRepository,
            ObjectMapper objectMapper) {
        this.ollamaChatModel = ollamaChatModel;
        this.openAiChatModel = openAiChatModel.getIfAvailable();
        this.googleGenAiChatModel = googleGenAiChatModel.getIfAvailable();
        this.promptLoaderService = promptLoaderService;
        this.messagingTemplate = messagingTemplate;
        this.resultRepository = resultRepository;
        this.objectMapper = objectMapper;
    }

    @Async
    public void generateQuestionsStream(String resumeText, String jdText, String companyName,
                                        String role, String experience, String domain,
                                        String type, String model, Long userId) {
        String destination = "/topic/interview-stream-" + userId;

        try {
            log.info("Starting formatted interview question streaming for user: {}", userId);

            String promptFileName = "SCENARIO_BASED".equalsIgnoreCase(type)
                    ? "scenario-based-question.st"
                    : "topic-wise-question.st";

            String prompt = promptLoaderService.loadPrompt(promptFileName)
                    .replace("{companyName}", companyName != null ? companyName : "")
                    .replace("{role}", role != null ? role : "")
                    .replace("{experience}", experience != null ? experience : "")
                    .replace("{domain}", domain != null ? domain : "Not specified")
                    .replace("{jdText}", jdText != null ? jdText : "")
                    .replace("{resumeText}", resumeText != null ? resumeText : "");

            ChatClient chatClient = ChatClient.create(selectChatModel(model));

            StringBuilder buffer = new StringBuilder();
            List<InterviewQuestionDTO> allQuestions = new ArrayList<>();

            Flux<String> tokenStream = chatClient.prompt().user(prompt).stream().content();

            tokenStream
                    .doOnNext(token -> {
                        buffer.append(token);
                        
                        String currentBuffer = buffer.toString();
                        int lastNewline = currentBuffer.lastIndexOf('\n');
                        
                        if (lastNewline != -1) {
                            String toProcess = currentBuffer.substring(0, lastNewline);
                            String[] lines = toProcess.split("\\r?\\n");
                            for (String line : lines) {
                                processLine(line, destination, allQuestions);
                            }
                            // Keep the remainder
                            buffer.delete(0, lastNewline + 1);
                        }
                    })
                    .doOnComplete(() -> {
                        // Safety: Append newline to ensure the last object is flushed
                        buffer.append("\n");
                        
                        String lastBuffer = buffer.toString();
                        if (!lastBuffer.trim().isEmpty()) {
                            String[] remainingLines = lastBuffer.split("\n");
                            for (String line : remainingLines) {
                                processLine(line, destination, allQuestions);
                            }
                        }
                        
                        log.info("Finished streaming {} questions to user: {}", allQuestions.size(), userId);
                        
                        // Save full JSON array for history viewing
                        try {
                            String finalJson = objectMapper.writeValueAsString(allQuestions);
                            saveResult(userId, companyName, role, experience, domain, type, model, finalJson);
                        } catch (Exception e) {
                            log.error("Failed to serialize final questions for user: {}", userId, e);
                        }

                        messagingTemplate.convertAndSend(destination, Map.of("type", "COMPLETE"));
                    })
                    .doOnError(error -> {
                        log.error("Streaming error for user: {}", userId, error);
                        messagingTemplate.convertAndSend(destination, Map.of("type", "ERROR", "message", error.getMessage()));
                    })
                    .blockLast();

        } catch (Exception e) {
            log.error("Failed to generate questions for user: {}", userId, e);
            messagingTemplate.convertAndSend(destination, Map.of("type", "ERROR", "message", e.getMessage()));
        }
    }

    private void processLine(String line, String destination, List<InterviewQuestionDTO> allQuestions) {
        String cleanLine = line.trim();
        if (cleanLine.isEmpty()) return;

        // Strip markdown backticks if AI ignores rules
        if (cleanLine.startsWith("```")) cleanLine = cleanLine.replace("```json", "").replace("```", "").trim();
        if (cleanLine.isEmpty()) return;

        try {
            InterviewQuestionDTO question = objectMapper.readValue(cleanLine, InterviewQuestionDTO.class);
            allQuestions.add(question);
            
            log.info("Sending QUESTION_OBJECT to destination: {}", destination);
            // Send the structured object IMMEDIATELY to frontend
            messagingTemplate.convertAndSend(destination, Map.of(
                "type", "QUESTION_OBJECT", 
                "question", question
            ));
        } catch (Exception e) {
            log.warn("Could not parse JSON line: {}. Error: {}", cleanLine, e.getMessage());
        }
    }

    public List<InterviewPrepResult> getHistory(Long userId) {
        return resultRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public InterviewPrepResult getResult(UUID resultId) {
        return resultRepository.findById(resultId)
                .orElseThrow(() -> new RuntimeException("Interview prep result not found: " + resultId));
    }

    public void deleteResult(UUID resultId, Long userId) {
        InterviewPrepResult result = getResult(resultId);
        if (!result.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this result");
        }
        resultRepository.deleteById(resultId);
    }

    private void saveResult(Long userId, String companyName, String role, String experience,
                            String domain, String type, String model, String content) {
        try {
            InterviewPrepResult result = InterviewPrepResult.builder()
                    .id(UUID.randomUUID())
                    .userId(userId)
                    .companyName(companyName)
                    .role(role)
                    .experience(experience)
                    .domain(domain)
                    .type(type)
                    .model(model)
                    .generatedContent(content)
                    .createdAt(LocalDateTime.now())
                    .build();
            resultRepository.save(result);
        } catch (Exception e) {
            log.error("Failed to save result", e);
        }
    }

    private ChatModel selectChatModel(String model) {
        if (model == null) return ollamaChatModel;
        String modelLower = model.toLowerCase();
        if ("openai".equals(modelLower) || "openrouter".equals(modelLower)) return openAiChatModel != null ? openAiChatModel : ollamaChatModel;
        if ("gemini".equals(modelLower)) return googleGenAiChatModel != null ? googleGenAiChatModel : ollamaChatModel;
        return ollamaChatModel;
    }
}
