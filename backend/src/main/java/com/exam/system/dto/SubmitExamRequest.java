package com.exam.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * DTO for submitting exam answers
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitExamRequest {

    @NotNull(message = "Attempt ID is required")
    private Long attemptId;

    @NotEmpty(message = "At least one answer is required")
    @Valid
    private List<AnswerDTO> answers;
}
