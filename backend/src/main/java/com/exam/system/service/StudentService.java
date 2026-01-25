package com.exam.system.service;

import com.exam.system.dto.AnswerDTO;
import com.exam.system.dto.ExamAttemptDTO;
import com.exam.system.entity.*;
import com.exam.system.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer for student exam operations
 */
@Service
public class StudentService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ExamAttemptRepository examAttemptRepository;

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get all available (active) exams
     */
    public List<Exam> getAvailableExams() {
        return examRepository.findByIsActive(true);
    }

    /**
     * Get exam with questions and options
     */
    public Map<String, Object> getExamWithQuestions(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found with id: " + examId));

        if (!exam.getIsActive()) {
            throw new RuntimeException("This exam is not currently active");
        }

        // Use optimized query with JOIN FETCH to prevent N+1 problem
        List<Map<String, Object>> questionsWithOptions = getQuestionsWithOptionsOptimized(examId);

        Map<String, Object> result = new HashMap<>();
        result.put("exam", exam);
        result.put("questions", questionsWithOptions);

        return result;
    }

    /**
     * Helper method to get questions with options using optimized query
     * Prevents N+1 query problem by using JOIN FETCH
     */
    private List<Map<String, Object>> getQuestionsWithOptionsOptimized(Long examId) {
        // Single query with JOIN FETCH - loads questions and options together
        List<Question> questions = questionRepository.findByExamIdWithOptions(examId);

        return questions.stream().map(question -> {
            Map<String, Object> questionMap = new HashMap<>();
            questionMap.put("questionId", question.getQuestionId());
            questionMap.put("questionText", question.getQuestionText());
            questionMap.put("questionType", question.getQuestionType());
            questionMap.put("marks", question.getMarks());

            // Options are already loaded via JOIN FETCH - no additional query
            questionMap.put("options", question.getOptions().stream().map(option -> {
                Map<String, Object> optionMap = new HashMap<>();
                optionMap.put("optionId", option.getOptionId());
                optionMap.put("optionLabel", option.getOptionLabel());
                optionMap.put("optionText", option.getOptionText());
                return optionMap;
            }).collect(Collectors.toList()));

            return questionMap;
        }).collect(Collectors.toList());
    }

    /**
     * Start an exam (create exam attempt)
     */
    @Transactional
    public Map<String, Object> startExam(Long examId, String username) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found with id: " + examId));

        if (exam.getIsActive() == null || !exam.getIsActive()) {
            throw new RuntimeException("This exam is not currently active");
        }

        // Validate exam time window
        LocalDateTime now = LocalDateTime.now();
        if (exam.getStartTime() != null && now.isBefore(exam.getStartTime())) {
            throw new RuntimeException("Exam has not started yet. Start time: " + exam.getStartTime());
        }
        if (exam.getEndTime() != null && now.isAfter(exam.getEndTime())) {
            throw new RuntimeException("Exam has ended. End time: " + exam.getEndTime());
        }

        // Check if student has already attempted this exam
        Optional<ExamAttempt> existingAttemptOpt = examAttemptRepository.findByUser_UserIdAndExam_ExamId(
                student.getUserId(), examId);

        ExamAttempt attempt;
        if (existingAttemptOpt.isPresent()) {
            ExamAttempt existingAttempt = existingAttemptOpt.get();
            // If there's a completed attempt, don't allow retaking
            if (existingAttempt.getStatus() == ExamAttempt.AttemptStatus.COMPLETED ||
                    existingAttempt.getStatus() == ExamAttempt.AttemptStatus.AUTO_SUBMITTED) {
                throw new RuntimeException("You have already completed this exam");
            }

            // If there's an IN_PROGRESS attempt, allow resuming
            attempt = existingAttempt;
        } else {
            // Create new exam attempt
            attempt = new ExamAttempt();
            attempt.setUser(student);
            attempt.setExam(exam);
            attempt.setStartTime(LocalDateTime.now());
            attempt.setStatus(ExamAttempt.AttemptStatus.IN_PROGRESS);
            attempt = examAttemptRepository.save(attempt);
        }

        // Get questions with options using optimized query (prevents N+1 problem)
        List<Map<String, Object>> questionsWithOptions = getQuestionsWithOptionsOptimized(examId);

        // Build exam object
        Map<String, Object> examMap = new HashMap<>();
        examMap.put("examId", exam.getExamId());
        examMap.put("examTitle", exam.getExamTitle());
        examMap.put("examDescription", exam.getExamDescription());
        examMap.put("durationMinutes", exam.getDurationMinutes());
        examMap.put("totalMarks", exam.getTotalMarks());
        examMap.put("passingMarks", exam.getPassingMarks());

        // Build response matching frontend expectations
        Map<String, Object> response = new HashMap<>();
        response.put("attemptId", attempt.getAttemptId());
        response.put("exam", examMap);
        response.put("questions", questionsWithOptions);

        return response;
    }

    /**
     * Submit exam answers and calculate score
     */
    @Autowired
    private EmailService emailService;

    /**
     * Submit exam answers and calculate score
     */
    @Transactional
    public Map<String, Object> submitExam(Long attemptId, List<AnswerDTO> answers, String username) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Exam attempt not found with id: " + attemptId));

        User user = attempt.getUser();
        if (!user.getUsername().equals(username)) {
            throw new RuntimeException("Security Violation: You are not authorized to submit this attempt");
        }

        if (attempt.getStatus() == ExamAttempt.AttemptStatus.COMPLETED) {
            throw new RuntimeException("This exam has already been submitted");
        }

        // Check if exam duration exceeded
        LocalDateTime deadline = attempt.getStartTime().plusMinutes(attempt.getExam().getDurationMinutes());
        if (LocalDateTime.now().isAfter(deadline)) {
            attempt.setStatus(ExamAttempt.AttemptStatus.AUTO_SUBMITTED);
        }

        try {
            // Get ALL questions for the exam to calculate total marks correctly
            List<Question> allQuestions = questionRepository.findByExam_ExamId(attempt.getExam().getExamId());
            int totalMarks = allQuestions.stream().mapToInt(Question::getMarks).sum();

            // Save responses and calculate score
            int totalScore = 0;

            for (AnswerDTO answerDTO : answers) {
                Question question = questionRepository.findById(answerDTO.getQuestionId())
                        .orElseThrow(
                                () -> new RuntimeException("Question not found with id: " + answerDTO.getQuestionId()));

                // Create response
                Response response = new Response();
                response.setExamAttempt(attempt);
                response.setQuestion(question);
                response.setSelectedOption(answerDTO.getSelectedOption());

                // Check if answer is correct
                boolean isCorrect = question.getCorrectOption() != null &&
                        question.getCorrectOption().equalsIgnoreCase(answerDTO.getSelectedOption());
                response.setIsCorrect(isCorrect);

                if (isCorrect) {
                    totalScore += question.getMarks();
                }

                responseRepository.save(response);
            }

            // Update attempt
            attempt.setEndTime(LocalDateTime.now());
            attempt.setScore(java.math.BigDecimal.valueOf(totalScore));
            if (attempt.getStatus() != ExamAttempt.AttemptStatus.AUTO_SUBMITTED) {
                attempt.setStatus(ExamAttempt.AttemptStatus.COMPLETED);
            }
            examAttemptRepository.save(attempt);

            // Calculate percentage
            double percentage = totalMarks > 0 ? (totalScore * 100.0 / totalMarks) : 0;
            boolean passed = totalScore >= attempt.getExam().getPassingMarks();

            Map<String, Object> result = new HashMap<>();
            result.put("attemptId", attemptId);
            result.put("score", totalScore);
            result.put("totalMarks", totalMarks);
            result.put("percentage", percentage);
            result.put("passed", passed);
            result.put("passingMarks", attempt.getExam().getPassingMarks());

            // Send Email Notification
            try {
                String subject = "Exam Result: " + attempt.getExam().getExamTitle();
                String body = String.format(
                        "Dear %s,\n\nYou have completed the exam '%s'.\n\nScore: %d/%d\nResult: %s\n\nView full details on your dashboard.",
                        user.getFullName(), attempt.getExam().getExamTitle(), totalScore, totalMarks,
                        passed ? "PASSED" : "FAILED");
                emailService.sendEmail(user.getEmail(), subject, body);
            } catch (Exception e) {
                // Don't fail submission if email fails
                System.err.println("Failed to send email: " + e.getMessage());
            }

            return result;
        } catch (Exception e) {
            // Ensure transaction is rolled back
            throw new RuntimeException("Failed to submit exam: " + e.getMessage(), e);
        }
    }

    /**
     * Get student's exam results
     */
    public List<ExamAttempt> getStudentResults(String username) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        return examAttemptRepository.findByUser_UserId(student.getUserId());
    }

    /**
     * Get student dashboard statistics
     */
    public Map<String, Object> getStudentDashboardStats(String username) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        List<ExamAttempt> attempts = examAttemptRepository.findByUser_UserId(student.getUserId());

        long completedExams = attempts.stream()
                .filter(a -> a.getStatus() == ExamAttempt.AttemptStatus.COMPLETED)
                .count();

        double averageScore = attempts.stream()
                .filter(a -> a.getStatus() == ExamAttempt.AttemptStatus.COMPLETED && a.getScore() != null)
                .mapToDouble(a -> a.getScore().doubleValue())
                .average()
                .orElse(0.0);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalExams", examRepository.countByIsActive(true));
        stats.put("completedExams", completedExams);
        stats.put("averageScore", Math.round(averageScore));

        return stats;
    }
}
