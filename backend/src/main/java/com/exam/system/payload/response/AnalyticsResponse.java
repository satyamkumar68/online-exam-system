package com.exam.system.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnalyticsResponse {
    private Long totalStudents;
    private Long totalExams;
    private Double averageScore;
    private Integer passCount;
    private Integer failCount;
}
