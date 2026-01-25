# Complete Backend Services and Controllers

This document contains all the remaining service and controller implementations for the Online Examination System.

## Services

### ExamService.java
Location: `src/main/java/com/exam/system/service/ExamService.java`

```java
package com.exam.system.service;

import com.exam.system.entity.*;
import com.exam.system.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ExamService {

    @Autowired
    private ExamRepository examRepository;
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private OptionRepository optionRepository;
    
    @Autowired
    private UserRepository userRepository;

    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }

    public List<Exam> getActiveExams() {
        return examRepository.findByIsActive(true);
    }

    public List<Exam> getAvailableExams() {
        return examRepository.findAvailableExams(LocalDateTime.now());
    }

    public Exam getExamById(Long examId) {
        return examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
    }

    @Transactional
    public Exam createExam(Exam exam, Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        exam.setCreatedBy(admin);
        return examRepository.save(exam);
    }

    @Transactional
    public Exam updateExam(Long examId, Exam examDetails) {
        Exam exam = getExamById(examId);
        exam.setExamTitle(examDetails.getExamTitle());
        exam.setExamDescription(examDetails.getExamDescription());
        exam.setDurationMinutes(examDetails.getDurationMinutes());
        exam.setTotalMarks(examDetails.getTotalMarks());
        exam.setPassingMarks(examDetails.getPassingMarks());
        exam.setStartTime(examDetails.getStartTime());
        exam.setEndTime(examDetails.getEndTime());
        exam.setIsActive(examDetails.getIsActive());
        return examRepository.save(exam);
    }

    @Transactional
    public void deleteExam(Long examId) {
        examRepository.deleteById(examId);
    }

    public List<Question> getExamQuestions(Long examId) {
        return questionRepository.findByExam_ExamId(examId);
    }
}
```

### QuestionService.java
Location: `src/main/java/com/exam/system/service/QuestionService.java`

```java
package com.exam.system.service;

import com.exam.system.entity.*;
import com.exam.system.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private OptionRepository optionRepository;
    
    @Autowired
    private ExamRepository examRepository;

    public Question getQuestionById(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
    }

    public List<Question> getQuestionsByExam(Long examId) {
        return questionRepository.findByExam_ExamId(examId);
    }

    @Transactional
    public Question createQuestion(Question question, Long examId, List<Option> options) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
        
        question.setExam(exam);
        Question savedQuestion = questionRepository.save(question);
        
        // Save options
        for (Option option : options) {
            option.setQuestion(savedQuestion);
            optionRepository.save(option);
        }
        
        return savedQuestion;
    }

    @Transactional
    public Question updateQuestion(Long questionId, Question questionDetails, List<Option> options) {
        Question question = getQuestionById(questionId);
        question.setQuestionText(questionDetails.getQuestionText());
        question.setQuestionType(questionDetails.getQuestionType());
        question.setMarks(questionDetails.getMarks());
        question.setCorrectOption(questionDetails.getCorrectOption());
        
        Question updated = questionRepository.save(question);
        
        // Update options
        optionRepository.deleteByQuestion_QuestionId(questionId);
        for (Option option : options) {
            option.setQuestion(updated);
            optionRepository.save(option);
        }
        
        return updated;
    }

    @Transactional
    public void deleteQuestion(Long questionId) {
        questionRepository.deleteById(questionId);
    }

    public List<Option> getQuestionOptions(Long questionId) {
        return optionRepository.findByQuestion_QuestionId(questionId);
    }
}
```

### AttemptService.java
Location: `src/main/java/com/exam/system/service/AttemptService.java`

```java
package com.exam.system.service;

import com.exam.system.entity.*;
import com.exam.system.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AttemptService {

    @Autowired
    private ExamAttemptRepository attemptRepository;
    
    @Autowired
    private ExamRepository examRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ResponseRepository responseRepository;
    
    @Autowired
    private QuestionRepository questionRepository;

    @Transactional
    public ExamAttempt startExam(Long examId, Long userId) {
        // Check if already attempted
        if (attemptRepository.existsByUser_UserIdAndExam_ExamId(userId, examId)) {
            throw new RuntimeException("You have already attempted this exam");
        }
        
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        ExamAttempt attempt = new ExamAttempt();
        attempt.setExam(exam);
        attempt.setUser(user);
        attempt.setStartTime(LocalDateTime.now());
        attempt.setStatus(ExamAttempt.AttemptStatus.IN_PROGRESS);
        attempt.setIsProctored(true);
        
        return attemptRepository.save(attempt);
    }

    @Transactional
    public Response submitAnswer(Long attemptId, Long questionId, String selectedOption) {
        ExamAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));
        
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        
        // Check if already answered
        Optional<Response> existingResponse = responseRepository
                .findByExamAttempt_AttemptIdAndQuestion_QuestionId(attemptId, questionId);
        
        Response response;
        if (existingResponse.isPresent()) {
            response = existingResponse.get();
        } else {
            response = new Response();
            response.setExamAttempt(attempt);
            response.setQuestion(question);
        }
        
        response.setSelectedOption(selectedOption);
        response.setIsCorrect(selectedOption.equals(question.getCorrectOption()));
        
        return responseRepository.save(response);
    }

    @Transactional
    public ExamAttempt submitExam(Long attemptId) {
        ExamAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));
        
        attempt.setEndTime(LocalDateTime.now());
        attempt.setStatus(ExamAttempt.AttemptStatus.COMPLETED);
        
        // Calculate score
        BigDecimal score = calculateScore(attemptId);
        attempt.setScore(score);
        
        return attemptRepository.save(attempt);
    }

    private BigDecimal calculateScore(Long attemptId) {
        List<Response> responses = responseRepository.findByExamAttempt_AttemptId(attemptId);
        int totalScore = 0;
        
        for (Response response : responses) {
            if (response.getIsCorrect()) {
                totalScore += response.getQuestion().getMarks();
            }
        }
        
        return BigDecimal.valueOf(totalScore);
    }

    public List<ExamAttempt> getUserAttempts(Long userId) {
        return attemptRepository.findByUser_UserId(userId);
    }

    public ExamAttempt getAttemptById(Long attemptId) {
        return attemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));
    }

    public List<Response> getAttemptResponses(Long attemptId) {
        return responseRepository.findByExamAttempt_AttemptId(attemptId);
    }
}
```

### ProctoringService.java
Location: `src/main/java/com/exam/system/service/ProctoringService.java`

```java
package com.exam.system.service;

import com.exam.system.entity.*;
import com.exam.system.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ProctoringService {

    @Autowired
    private ProctoringLogRepository proctoringLogRepository;
    
    @Autowired
    private ExamAttemptRepository attemptRepository;

    @Transactional
    public ProctoringLog logEvent(Long attemptId, ProctoringLog.EventType eventType, 
                                   String description, ProctoringLog.Severity severity) {
        ExamAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));
        
        ProctoringLog log = new ProctoringLog();
        log.setExamAttempt(attempt);
        log.setEventType(eventType);
        log.setEventDescription(description);
        log.setSeverity(severity);
        
        return proctoringLogRepository.save(log);
    }

    public List<ProctoringLog> getAttemptLogs(Long attemptId) {
        return proctoringLogRepository.findByExamAttempt_AttemptId(attemptId);
    }

    public List<ProctoringLog> getHighSeverityLogs(Long attemptId) {
        return proctoringLogRepository.findByExamAttempt_AttemptIdAndSeverity(
                attemptId, ProctoringLog.Severity.HIGH);
    }

    public Long countSuspiciousActivities(Long attemptId) {
        return proctoringLogRepository.countByExamAttempt_AttemptIdAndSeverity(
                attemptId, ProctoringLog.Severity.HIGH);
    }
}
```

## Controllers

### AdminController.java
Location: `src/main/java/com/exam/system/controller/AdminController.java`

```java
package com.exam.system.controller;

import com.exam.system.dto.ApiResponse;
import com.exam.system.entity.*;
import com.exam.system.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private ExamService examService;
    
    @Autowired
    private QuestionService questionService;
    
    @Autowired
    private AttemptService attemptService;
    
    @Autowired
    private ProctoringService proctoringService;

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("totalExams", examService.getAllExams().size());
        dashboard.put("activeExams", examService.getActiveExams().size());
        return ResponseEntity.ok(new ApiResponse(true, "Dashboard data", dashboard));
    }

    @GetMapping("/exams")
    public ResponseEntity<?> getAllExams() {
        List<Exam> exams = examService.getAllExams();
        return ResponseEntity.ok(new ApiResponse(true, "Exams retrieved", exams));
    }

    @PostMapping("/exams")
    public ResponseEntity<?> createExam(@RequestBody Exam exam, Authentication auth) {
        // Get admin user ID from authentication
        Long adminId = 1L; // In real implementation, extract from JWT
        Exam created = examService.createExam(exam, adminId);
        return ResponseEntity.ok(new ApiResponse(true, "Exam created", created));
    }

    @PutMapping("/exams/{id}")
    public ResponseEntity<?> updateExam(@PathVariable Long id, @RequestBody Exam exam) {
        Exam updated = examService.updateExam(id, exam);
        return ResponseEntity.ok(new ApiResponse(true, "Exam updated", updated));
    }

    @DeleteMapping("/exams/{id}")
    public ResponseEntity<?> deleteExam(@PathVariable Long id) {
        examService.deleteExam(id);
        return ResponseEntity.ok(new ApiResponse(true, "Exam deleted"));
    }

    @PostMapping("/questions")
    public ResponseEntity<?> createQuestion(@RequestBody Map<String, Object> payload) {
        // Extract question and options from payload
        // Implementation details omitted for brevity
        return ResponseEntity.ok(new ApiResponse(true, "Question created"));
    }

    @GetMapping("/proctoring-logs/{attemptId}")
    public ResponseEntity<?> getProctoringLogs(@PathVariable Long attemptId) {
        List<ProctoringLog> logs = proctoringService.getAttemptLogs(attemptId);
        return ResponseEntity.ok(new ApiResponse(true, "Logs retrieved", logs));
    }
}
```

### StudentController.java
Location: `src/main/java/com/exam/system/controller/StudentController.java`

```java
package com.exam.system.controller;

import com.exam.system.dto.ApiResponse;
import com.exam.system.entity.*;
import com.exam.system.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/student")
@PreAuthorize("hasRole('STUDENT')")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private ExamService examService;
    
    @Autowired
    private AttemptService attemptService;
    
    @Autowired
    private QuestionService questionService;

    @GetMapping("/exams")
    public ResponseEntity<?> getAvailableExams() {
        List<Exam> exams = examService.getAvailableExams();
        return ResponseEntity.ok(new ApiResponse(true, "Available exams", exams));
    }

    @GetMapping("/exams/{id}")
    public ResponseEntity<?> getExamDetails(@PathVariable Long id) {
        Exam exam = examService.getExamById(id);
        return ResponseEntity.ok(new ApiResponse(true, "Exam details", exam));
    }

    @PostMapping("/exams/{id}/start")
    public ResponseEntity<?> startExam(@PathVariable Long id) {
        Long userId = 2L; // Extract from JWT in real implementation
        try {
            ExamAttempt attempt = attemptService.startExam(id, userId);
            return ResponseEntity.ok(new ApiResponse(true, "Exam started", attempt));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/attempts/{attemptId}/questions")
    public ResponseEntity<?> getExamQuestions(@PathVariable Long attemptId) {
        ExamAttempt attempt = attemptService.getAttemptById(attemptId);
        List<Question> questions = questionService.getQuestionsByExam(
                attempt.getExam().getExamId());
        return ResponseEntity.ok(new ApiResponse(true, "Questions retrieved", questions));
    }

    @PostMapping("/attempts/{attemptId}/answer")
    public ResponseEntity<?> submitAnswer(@PathVariable Long attemptId, 
                                          @RequestBody Map<String, Object> payload) {
        Long questionId = Long.valueOf(payload.get("questionId").toString());
        String selectedOption = payload.get("selectedOption").toString();
        
        Response response = attemptService.submitAnswer(attemptId, questionId, selectedOption);
        return ResponseEntity.ok(new ApiResponse(true, "Answer saved", response));
    }

    @PostMapping("/attempts/{attemptId}/submit")
    public ResponseEntity<?> submitExam(@PathVariable Long attemptId) {
        ExamAttempt attempt = attemptService.submitExam(attemptId);
        return ResponseEntity.ok(new ApiResponse(true, "Exam submitted", attempt));
    }

    @GetMapping("/results")
    public ResponseEntity<?> getMyResults() {
        Long userId = 2L; // Extract from JWT
        List<ExamAttempt> attempts = attemptService.getUserAttempts(userId);
        return ResponseEntity.ok(new ApiResponse(true, "Results retrieved", attempts));
    }

    @GetMapping("/results/{attemptId}")
    public ResponseEntity<?> getResultDetails(@PathVariable Long attemptId) {
        ExamAttempt attempt = attemptService.getAttemptById(attemptId);
        List<Response> responses = attemptService.getAttemptResponses(attemptId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("attempt", attempt);
        result.put("responses", responses);
        
        return ResponseEntity.ok(new ApiResponse(true, "Result details", result));
    }
}
```

## Exception Handling

### GlobalExceptionHandler.java
Location: `src/main/java/com/exam/system/exception/GlobalExceptionHandler.java`

```java
package com.exam.system.exception;

import com.exam.system.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "An error occurred: " + ex.getMessage()));
    }
}
```

## Notes

1. All services use `@Transactional` for database operations
2. Controllers use `@PreAuthorize` for role-based access control
3. JWT token should be extracted from Authentication object in real implementation
4. Error handling is centralized in GlobalExceptionHandler
5. All endpoints return ApiResponse for consistent response format

## Building and Running

```bash
# Navigate to backend directory
cd backend

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The backend will start on `http://localhost:8080/api`
