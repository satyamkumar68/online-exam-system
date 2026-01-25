package com.exam.system.controller;

import com.exam.system.dto.ApiResponse;
import com.exam.system.dto.CreateExamRequest;
import com.exam.system.entity.Exam;
import com.exam.system.entity.User;
import com.exam.system.repository.ExamAttemptRepository;
import com.exam.system.repository.UserRepository;
import com.exam.system.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Admin endpoints
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ExamService examService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExamAttemptRepository examAttemptRepository;

    /**
     * Create a new exam
     */
    @PostMapping("/exams")
    public ResponseEntity<?> createExam(@Valid @RequestBody CreateExamRequest request) {
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            Exam exam = examService.createExam(request, username);
            return ResponseEntity.ok(new ApiResponse(true, "Exam created successfully", exam));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to create exam: " + e.getMessage()));
        }
    }

    /**
     * Get all exams
     */
    @GetMapping("/exams")
    public ResponseEntity<?> getAllExams() {
        try {
            List<Exam> exams = examService.getAllExams();
            return ResponseEntity.ok(new ApiResponse(true, "Exams retrieved successfully", exams));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to retrieve exams: " + e.getMessage()));
        }
    }

    /**
     * Get exam by ID
     */
    @GetMapping("/exams/{id}")
    public ResponseEntity<?> getExamById(@PathVariable Long id) {
        try {
            Exam exam = examService.getExamById(id);
            return ResponseEntity.ok(new ApiResponse(true, "Exam retrieved successfully", exam));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to retrieve exam: " + e.getMessage()));
        }
    }

    /**
     * Update exam status (activate/deactivate)
     */
    @PutMapping("/exams/{id}/status")
    public ResponseEntity<?> updateExamStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> request) {
        try {
            Boolean isActive = request.get("isActive");
            Exam exam = examService.updateExamStatus(id, isActive);
            return ResponseEntity.ok(new ApiResponse(true, "Exam status updated successfully", exam));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to update exam status: " + e.getMessage()));
        }
    }

    /**
     * Delete exam
     */
    @DeleteMapping("/exams/{id}")
    public ResponseEntity<?> deleteExam(@PathVariable Long id) {
        try {
            examService.deleteExam(id);
            return ResponseEntity.ok(new ApiResponse(true, "Exam deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to delete exam: " + e.getMessage()));
        }
    }

    /**
     * Get admin dashboard statistics
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalExams", examService.getTotalExamCount());
            stats.put("activeExams", examService.getActiveExamCount());
            stats.put("totalStudents", userRepository.countByRole(User.UserRole.STUDENT));
            stats.put("totalAttempts", examAttemptRepository.count());

            return ResponseEntity.ok(new ApiResponse(true, "Dashboard stats retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to retrieve dashboard stats: " + e.getMessage()));
        }
    }

    @PostMapping("/exams/{id}/questions/upload")
    public ResponseEntity<?> uploadQuestions(@PathVariable Long id,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Please select a file to upload"));
            }

            int count = examService.uploadQuestions(id, file);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully imported " + count + " questions"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to upload questions: " + e.getMessage()));
        }
    }

    /**
     * Get all students
     */
    @GetMapping("/students")
    public ResponseEntity<?> getAllStudents() {
        try {
            List<User> students = userRepository.findByRole(User.UserRole.STUDENT);
            return ResponseEntity.ok(new ApiResponse(true, "Students retrieved successfully", students));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to retrieve students: " + e.getMessage()));
        }
    }

    /**
     * Get all attempts for a specific exam
     */
    @GetMapping("/exams/{id}/attempts")
    public ResponseEntity<?> getExamAttempts(@PathVariable Long id) {
        try {
            List<com.exam.system.entity.ExamAttempt> attempts = examAttemptRepository.findByExam_ExamId(id);

            // Map to simplified object to avoid recursion/lazy loading issues
            List<Map<String, Object>> result = new java.util.ArrayList<>();
            for (com.exam.system.entity.ExamAttempt attempt : attempts) {
                Map<String, Object> map = new HashMap<>();
                map.put("attemptId", attempt.getAttemptId());
                map.put("studentName", attempt.getUser().getFullName());
                map.put("studentUsername", attempt.getUser().getUsername());
                map.put("score", attempt.getScore());
                map.put("status", attempt.getStatus());
                map.put("startTime", attempt.getStartTime());
                map.put("endTime", attempt.getEndTime());
                result.add(map);
            }

            return ResponseEntity.ok(new ApiResponse(true, "Attempts retrieved successfully", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to retrieve attempts: " + e.getMessage()));
        }
    }
}
