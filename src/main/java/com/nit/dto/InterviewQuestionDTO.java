package com.nit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewQuestionDTO {
    private String topic;
    private String question;
    private String technicalExpectation;
    private String goldenAnswer;
}
