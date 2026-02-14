package com.exam.system.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExamRequest {
    @NotBlank
    private String title;

    private String description;

    @NotNull
    private Integer maxTimeMinutes;

    private java.time.LocalDateTime startTime;
    private java.time.LocalDateTime endTime;
}
