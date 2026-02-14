package com.exam.system.controller;

import com.exam.system.entity.Exam;
import com.exam.system.entity.ExamResult;
import com.exam.system.entity.Question;
import com.exam.system.entity.User;
import com.exam.system.payload.request.ExamSubmission;
import com.exam.system.payload.response.MessageResponse;
import com.exam.system.payload.response.QuestionDTO;
import com.exam.system.repository.ExamRepository;
import com.exam.system.repository.ExamResultRepository;
import com.exam.system.repository.QuestionRepository;
import com.exam.system.repository.UserRepository;
import com.exam.system.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student")
@PreAuthorize("hasAuthority('STUDENT')")
public class StudentController {

    @Autowired
    ExamRepository examRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    ExamResultRepository examResultRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/exams")
    public ResponseEntity<List<Exam>> getActiveExams() {
        return ResponseEntity.ok(examRepository.findByIsActiveTrue());
    }

    @GetMapping("/exams/{examId}/questions")
    public ResponseEntity<List<QuestionDTO>> getExamQuestions(@PathVariable Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Error: Exam not found."));

        // Validate Time
        if (exam.getStartTime() != null && exam.getEndTime() != null) {
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            if (now.isBefore(exam.getStartTime())) {
                throw new RuntimeException("Error: Exam has not started yet.");
            }
            if (now.isAfter(exam.getEndTime())) {
                throw new RuntimeException("Error: Exam has expired.");
            }
        }

        List<Question> questions = questionRepository.findByExam(exam);
        List<QuestionDTO> questionDTOs = questions.stream()
                .map(q -> new QuestionDTO(
                        q.getId(),
                        q.getContent(),
                        q.getOption1(),
                        q.getOption2(),
                        q.getOption3(),
                        q.getOption4()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(questionDTOs);
    }

    @PostMapping("/exams/submit")
    public ResponseEntity<?> submitExam(@RequestBody ExamSubmission submission) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Error: User not found."));

        Exam exam = examRepository.findById(submission.getExamId())
                .orElseThrow(() -> new RuntimeException("Error: Exam not found."));

        // Check if user already submitted this exam
        List<ExamResult> existingResults = examResultRepository.findByUser(user);
        boolean alreadySubmitted = existingResults.stream()
                .anyMatch(result -> result.getExam().getId().equals(exam.getId()));

        if (alreadySubmitted) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: You have already submitted this exam."));
        }

        List<Question> questions = questionRepository.findByExam(exam);
        Map<Long, String> correctAnswers = questions.stream()
                .collect(Collectors.toMap(Question::getId, Question::getAnswer));

        int score = 0;
        for (Map.Entry<Long, String> entry : submission.getAnswers().entrySet()) {
            Long questionId = entry.getKey();
            String selectedOption = entry.getValue();

            if (correctAnswers.containsKey(questionId) && correctAnswers.get(questionId).equals(selectedOption)) {
                score++;
            }
        }

        ExamResult result = new ExamResult();
        result.setUser(user);
        result.setExam(exam);
        result.setScore(score);
        result.setTotalQuestions(questions.size());

        examResultRepository.save(result);

        return ResponseEntity
                .ok(new MessageResponse("Exam submitted successfully! Score: " + score + "/" + questions.size()));
    }

    @GetMapping("/results")
    public ResponseEntity<List<ExamResult>> getMyResults() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Error: User not found."));

        return ResponseEntity.ok(examResultRepository.findByUser(user));
    }
}
