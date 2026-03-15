package com.nit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkedInJobSearchRequest {
    private String keyword;
    private String location;
    private String experienceLevel;
    private String jobType;
    private String datePosted;
    private Boolean remote;
    private Integer limit;
}
