package com.exam.system.service;

import com.exam.system.dto.CreateExamRequest;
import com.exam.system.dto.OptionDTO;
import com.exam.system.dto.QuestionDTO;
import com.exam.system.entity.Exam;
import com.exam.system.entity.Option;
import com.exam.system.entity.Question;
import com.exam.system.entity.User;
import com.exam.system.exception.ExamNotFoundException;
import com.exam.system.exception.UserNotFoundException;
import com.exam.system.repository.ExamRepository;

import com.exam.system.repository.QuestionRepository;
import com.exam.system.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Service layer for exam business logic
 */
@Service
public class ExamService {

    private static final Logger logger = LoggerFactory.getLogger(ExamService.class);

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new exam with questions and options
     */
    @Transactional
    public Exam createExam(CreateExamRequest request, String username) {
        logger.info("Creating exam: {} by user: {}", request.getExamTitle(), username);

        // Validation
        if (request.getDurationMinutes() <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        if (request.getTotalMarks() <= 0) {
            throw new IllegalArgumentException("Total marks must be positive");
        }
        if (request.getPassingMarks() > request.getTotalMarks()) {
            throw new IllegalArgumentException("Passing marks cannot exceed total marks");
        }
        if (request.getQuestions() == null || request.getQuestions().isEmpty()) {
            throw new IllegalArgumentException("Exam must have at least one question");
        }

        // Get the admin user
        User admin = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        // Create exam entity
        Exam exam = new Exam();
        exam.setExamTitle(request.getExamTitle());
        exam.setExamDescription(request.getExamDescription());
        exam.setDurationMinutes(request.getDurationMinutes());
        exam.setTotalMarks(request.getTotalMarks());
        exam.setPassingMarks(request.getPassingMarks());
        exam.setCreatedBy(admin);
        exam.setIsActive(true);

        // Save exam first to get the ID
        exam = examRepository.save(exam);

        // Create questions with options
        List<Question> questions = new ArrayList<>();

        for (QuestionDTO questionDTO : request.getQuestions()) {
            Question question = new Question();
            question.setExam(exam);
            question.setQuestionText(questionDTO.getQuestionText());
            try {
                question.setQuestionType(Question.QuestionType.valueOf(questionDTO.getQuestionType()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid question type: " + questionDTO.getQuestionType());
            }
            question.setMarks(questionDTO.getMarks());
            question.setCorrectOption(questionDTO.getCorrectOption());

            // Create options and add to question
            List<Option> options = new ArrayList<>();
            for (OptionDTO optionDTO : questionDTO.getOptions()) {
                Option option = new Option();
                option.setQuestion(question);
                option.setOptionLabel(optionDTO.getLabel());
                option.setOptionText(optionDTO.getText());
                options.add(option);
            }
            question.setOptions(options);

            questions.add(question);
        }

        // Batch save questions (cascades to options)
        questionRepository.saveAll(questions);

        logger.info("Successfully created exam with ID: {}", exam.getExamId());

        return exam;
    }

    /**
     * Get all exams
     */
    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }

    /**
     * Get exam by ID
     */
    public Exam getExamById(Long id) {
        logger.debug("Fetching exam with ID: {}", id);
        return examRepository.findById(id)
                .orElseThrow(() -> new ExamNotFoundException(id));
    }

    /**
     * Update exam status (activate/deactivate)
     */
    @Transactional
    public Exam updateExamStatus(Long id, Boolean isActive) {
        Exam exam = getExamById(id);
        exam.setIsActive(isActive);
        return examRepository.save(exam);
    }

    /**
     * Delete exam
     */
    @Transactional
    public void deleteExam(Long id) {
        Exam exam = getExamById(id);
        examRepository.delete(exam);
    }

    /**
     * Get total exam count
     */
    public long getTotalExamCount() {
        return examRepository.count();
    }

    /**
     * Upload questions from CSV file
     */
    @Transactional
    public int uploadQuestions(Long examId, org.springframework.web.multipart.MultipartFile file) {
        Exam exam = getExamById(examId);

        try (java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(file.getInputStream()))) {
            List<Question> questions = new ArrayList<>();

            // Skip Header
            String header = reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;

                // Simple split by comma
                String[] parts = line.split(",");

                // Expecting at least 3 columns: Text, Type, Marks
                if (parts.length < 3)
                    continue;

                String text = parts[0];
                String typeStr = parts[1];
                String marksStr = parts[2];
                // Column 7 is Correct Option (0-indexed)
                String correct = parts.length > 7 ? parts[7] : null;

                Question question = new Question();
                question.setExam(exam);
                question.setQuestionText(text);
                try {
                    question.setQuestionType(Question.QuestionType.valueOf(typeStr.toUpperCase().trim()));
                } catch (IllegalArgumentException e) {
                    question.setQuestionType(Question.QuestionType.MCQ);
                }

                try {
                    question.setMarks(Integer.parseInt(marksStr.trim()));
                } catch (NumberFormatException e) {
                    question.setMarks(1);
                }
                question.setCorrectOption(correct != null ? correct.trim() : null);

                List<Option> options = new ArrayList<>();
                String[] optionLabels = { "A", "B", "C", "D" };
                // Options are at indices 3, 4, 5, 6
                for (int i = 0; i < 4; i++) {
                    int colIndex = 3 + i;
                    if (colIndex < parts.length) {
                        String optText = parts[colIndex];
                        if (optText != null && !optText.trim().isEmpty()) {
                            Option option = new Option();
                            option.setQuestion(question);
                            option.setOptionLabel(optionLabels[i]);
                            option.setOptionText(optText.trim());
                            options.add(option);
                        }
                    }
                }
                question.setOptions(options);
                questions.add(question);
            }

            questionRepository.saveAll(questions);
            return questions.size();

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage());
        }
    }

    /**
     * Get active exam count
     */
    public long getActiveExamCount() {
        return examRepository.countByIsActive(true);
    }
}
