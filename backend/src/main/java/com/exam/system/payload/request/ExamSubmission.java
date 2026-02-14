package com.exam.system.payload.request;

import lombok.Data;
import java.util.Map;

@Data
public class ExamSubmission {
    private Long examId;
    // Map of QuestionId -> SelectedOption (e.g., "option1")
    private Map<Long, String> answers;
}
