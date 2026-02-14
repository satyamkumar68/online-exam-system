package com.exam.system.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QuestionRequest {
    private Long examId; // Optional for question bank

    @NotBlank
    private String content;

    @NotBlank
    private String option1;

    @NotBlank
    private String option2;

    @NotBlank
    private String option3;

    @NotBlank
    private String option4;

    @NotBlank
    private String answer;

    private String category; // For question bank categorization
}
