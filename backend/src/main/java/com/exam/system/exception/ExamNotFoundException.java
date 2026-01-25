package com.exam.system.exception;

/**
 * Exception thrown when an exam is not found
 */
public class ExamNotFoundException extends RuntimeException {

    public ExamNotFoundException(Long examId) {
        super("Exam not found with ID: " + examId);
    }

    public ExamNotFoundException(String message) {
        super(message);
    }
}
