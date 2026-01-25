package com.exam.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * DTO for option data within questions
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionDTO {

    @NotBlank(message = "Option label is required")
    private String label; // A, B, C, D

    @NotBlank(message = "Option text is required")
    private String text;
}
