package com.nit.dto;

import lombok.Data;

@Data
public class InterviewPrepRequest {
    private String companyName;
    private String role;
    private String experience;
    private String domain;
    private String jdText;
    private String model;
    private String type; // TOPIC_WISE or SCENARIO_BASED
}
