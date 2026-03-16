package com.nit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationStatsDTO {
    private long totalApplications;
    private Map<String, Long> statusDistribution;
    private long applicationsLast7Days;
    private long applicationsLast30Days;
    private long withAnalysisCount;
}
