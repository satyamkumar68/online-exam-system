package com.exam.system.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDTO {
    private Long id;
    private String content;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    // No answer field
}
