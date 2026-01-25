package com.exam.system.controller;

import com.exam.system.dto.ApiResponse;
import com.exam.system.dto.SubmitExamRequest;
import com.exam.system.entity.Exam;
import com.exam.system.entity.ExamAttempt;
import com.exam.system.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Student endpoints
 */
@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    /**
     * Get available exams for students
     */
    @GetMapping("/exams/available")
    public ResponseEntity<?> getAvailableExams() {
        try {
            List<Exam> exams = studentService.getAvailableExams();
            return ResponseEntity.ok(new ApiResponse(true, "Available exams retrieved successfully", exams));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to retrieve exams: " + e.getMessage()));
        }
    }

    /**
     * Get exam with questions (when starting an exam)
     */
    @GetMapping("/exams/{id}")
    public ResponseEntity<?> getExamWithQuestions(@PathVariable Long id) {
        try {
            Map<String, Object> examData = studentService.getExamWithQuestions(id);
            return ResponseEntity.ok(new ApiResponse(true, "Exam retrieved successfully", examData));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to retrieve exam: " + e.getMessage()));
        }
    }

    /**
     * Start an exam (create exam attempt)
     */
    @PostMapping("/exams/{id}/start")
    public ResponseEntity<?> startExam(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            Map<String, Object> examData = studentService.startExam(id, username);
            return ResponseEntity.ok(new ApiResponse(true, "Exam started successfully", examData));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to start exam: " + e.getMessage()));
        }
    }

    /**
     * Submit exam answers
     */
    @PostMapping("/attempts/submit")
    public ResponseEntity<?> submitExam(@Valid @RequestBody SubmitExamRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            Map<String, Object> result = studentService.submitExam(request.getAttemptId(), request.getAnswers(),
                    username);
            return ResponseEntity.ok(new ApiResponse(true, "Exam submitted successfully", result));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to submit exam: " + e.getMessage()));
        }
    }

    /**
     * Get student's exam results
     */
    @GetMapping("/results")
    public ResponseEntity<?> getResults() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            List<ExamAttempt> results = studentService.getStudentResults(username);
            return ResponseEntity.ok(new ApiResponse(true, "Results retrieved successfully", results));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to retrieve results: " + e.getMessage()));
        }
    }

    /**
     * Get student dashboard statistics
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardStats() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            Map<String, Object> stats = studentService.getStudentDashboardStats(username);
            return ResponseEntity.ok(new ApiResponse(true, "Dashboard stats retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to retrieve dashboard stats: " + e.getMessage()));
        }
    }
}
