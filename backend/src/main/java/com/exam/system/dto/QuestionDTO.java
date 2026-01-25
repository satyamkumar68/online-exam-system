package com.exam.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * DTO for question data within exam creation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {

    @NotBlank(message = "Question text is required")
    private String questionText;

    private String questionType = "MCQ"; // Default to MCQ

    @NotNull(message = "Marks are required")
    @Min(value = 1, message = "Marks must be at least 1")
    private Integer marks;

    @NotBlank(message = "Correct option is required")
    private String correctOption; // A, B, C, D

    @NotEmpty(message = "Options are required")
    @Valid
    private List<OptionDTO> options;
}
