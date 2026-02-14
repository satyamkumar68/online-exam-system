package com.exam.system.controller;

import com.exam.system.entity.Exam;
import com.exam.system.entity.Question;
import com.exam.system.entity.User;
import com.exam.system.entity.ExamResult;
import com.exam.system.payload.request.ExamRequest;
import com.exam.system.payload.request.QuestionRequest;
import com.exam.system.payload.response.MessageResponse;
import com.exam.system.repository.ExamRepository;
import com.exam.system.repository.ExamResultRepository;
import com.exam.system.repository.QuestionRepository;
import com.exam.system.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    @Autowired
    ExamRepository examRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    UserRepository userRepository;

    // --- Exam Management ---

    @PostMapping("/exams")
    public ResponseEntity<?> createExam(@Valid @RequestBody ExamRequest examRequest) {
        Exam exam = new Exam();
        exam.setTitle(examRequest.getTitle());
        exam.setDescription(examRequest.getDescription());
        exam.setMaxTimeMinutes(examRequest.getMaxTimeMinutes());
        exam.setStartTime(examRequest.getStartTime());
        exam.setEndTime(examRequest.getEndTime());
        exam.setIsActive(true);

        examRepository.save(exam);
        return ResponseEntity.ok(new MessageResponse("Exam created successfully!"));
    }

    @GetMapping("/exams")
    public ResponseEntity<List<Exam>> getAllExams() {
        return ResponseEntity.ok(examRepository.findAll());
    }

    // --- Question Management ---

    @PostMapping("/questions")
    public ResponseEntity<?> addQuestion(@Valid @RequestBody QuestionRequest questionRequest) {
        Exam exam = examRepository.findById(questionRequest.getExamId())
                .orElseThrow(() -> new RuntimeException("Error: Exam not found."));

        Question question = new Question();
        question.setContent(questionRequest.getContent());
        question.setOption1(questionRequest.getOption1());
        question.setOption2(questionRequest.getOption2());
        question.setOption3(questionRequest.getOption3());
        question.setOption4(questionRequest.getOption4());
        question.setAnswer(questionRequest.getAnswer());
        question.setExam(exam);

        questionRepository.save(question);
        return ResponseEntity.ok(new MessageResponse("Question added successfully!"));
    }

    @GetMapping("/exams/{examId}/questions")
    public ResponseEntity<List<Question>> getQuestionsByExam(@PathVariable Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Error: Exam not found."));

        return ResponseEntity.ok(questionRepository.findByExam(exam));
    }

    // --- Student Management ---

    @GetMapping("/students")
    public ResponseEntity<List<User>> getAllStudents() {
        List<User> students = userRepository.findAll().stream()
                .filter(user -> user.getRole() == User.Role.STUDENT)
                .collect(Collectors.toList());
        return ResponseEntity.ok(students);
    }

    // --- Result Management ---

    @Autowired
    ExamResultRepository examResultRepository;

    @GetMapping("/results")
    public ResponseEntity<List<ExamResult>> getAllResults() {
        return ResponseEntity.ok(examResultRepository.findAll());
    }

    // --- Analytics ---

    @GetMapping("/analytics")
    public ResponseEntity<com.exam.system.payload.response.AnalyticsResponse> getAnalytics() {
        List<User> students = userRepository.findAll().stream()
                .filter(user -> user.getRole() == User.Role.STUDENT)
                .collect(Collectors.toList());
        long totalStudents = students.size();

        List<Exam> exams = examRepository.findAll();
        long totalExams = exams.size();

        List<ExamResult> results = examResultRepository.findAll();

        double totalScorePercent = 0;
        int passCount = 0;
        int failCount = 0;

        for (ExamResult result : results) {
            double percentage = ((double) result.getScore() / result.getTotalQuestions()) * 100;
            totalScorePercent += percentage;

            if (percentage >= 50) {
                passCount++;
            } else {
                failCount++;
            }
        }

        double avgScore = results.isEmpty() ? 0 : totalScorePercent / results.size();

        return ResponseEntity.ok(new com.exam.system.payload.response.AnalyticsResponse(
                totalStudents, totalExams, avgScore, passCount, failCount));
    }

    // --- Question Bank ---

    @GetMapping("/question-bank")
    public ResponseEntity<List<Question>> getQuestionBank() {
        // Get all questions not assigned to any exam
        List<Question> bankQuestions = questionRepository.findAll().stream()
                .filter(q -> q.getExam() == null)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bankQuestions);
    }

    @GetMapping("/question-bank/categories")
    public ResponseEntity<List<String>> getCategories() {
        List<String> categories = questionRepository.findAll().stream()
                .map(Question::getCategory)
                .filter(cat -> cat != null && !cat.isEmpty())
                .distinct()
                .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }

    @PostMapping("/question-bank")
    public ResponseEntity<?> addToQuestionBank(@RequestBody QuestionRequest questionRequest) {
        Question question = new Question();
        question.setContent(questionRequest.getContent());
        question.setOption1(questionRequest.getOption1());
        question.setOption2(questionRequest.getOption2());
        question.setOption3(questionRequest.getOption3());
        question.setOption4(questionRequest.getOption4());
        question.setAnswer(questionRequest.getAnswer());
        question.setCategory(questionRequest.getCategory());
        question.setExam(null); // Bank question, not assigned to exam

        questionRepository.save(question);
        return ResponseEntity.ok(new MessageResponse("Question added to bank successfully!"));
    }

    @PostMapping("/exams/{examId}/add-bank-question/{questionId}")
    public ResponseEntity<?> addBankQuestionToExam(@PathVariable Long examId, @PathVariable Long questionId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Error: Exam not found."));
        Question originalQuestion = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Error: Question not found."));

        // Create a copy of the question for the exam (keep original in bank)
        Question questionCopy = new Question();
        questionCopy.setContent(originalQuestion.getContent());
        questionCopy.setOption1(originalQuestion.getOption1());
        questionCopy.setOption2(originalQuestion.getOption2());
        questionCopy.setOption3(originalQuestion.getOption3());
        questionCopy.setOption4(originalQuestion.getOption4());
        questionCopy.setAnswer(originalQuestion.getAnswer());
        questionCopy.setCategory(originalQuestion.getCategory());
        questionCopy.setExam(exam); // Assign copy to exam

        questionRepository.save(questionCopy);
        return ResponseEntity.ok(new MessageResponse("Question added to exam successfully!"));
    }
}
