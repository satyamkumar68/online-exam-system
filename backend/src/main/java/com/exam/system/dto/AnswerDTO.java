package com.exam.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * DTO for student answer submission
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDTO {

    @NotNull(message = "Question ID is required")
    private Long questionId;

    @NotBlank(message = "Selected option is required")
    private String selectedOption; // A, B, C, D
}
