package com.nit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkedInJobDTO {
    private String title;
    private String company;
    private String location;
    private String postedDate;
    private String salary;
    private List<String> skills;
    private String description;
    private String applyLink;
}
