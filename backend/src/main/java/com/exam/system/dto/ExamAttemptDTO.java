package com.exam.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for exam attempt response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamAttemptDTO {

    private Long attemptId;
    private Long examId;
    private String examTitle;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer score;
    private String status; // IN_PROGRESS, COMPLETED
}
