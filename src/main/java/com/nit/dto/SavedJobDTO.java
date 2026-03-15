package com.nit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedJobDTO {
    private Long id;
    private String companyName;
    private String jobTitle;
    private String location;
    private String salary;
    private String skills;
    private String jobDescription;
    private String applyLink;
    private String originalPostedDate;
    private Instant createdAt;
}
