package com.exam.system.service;

import com.exam.system.dto.CreateExamRequest;
import com.exam.system.dto.QuestionDTO;
import com.exam.system.dto.OptionDTO;
import com.exam.system.entity.Exam;
import com.exam.system.entity.User;
import com.exam.system.exception.UserNotFoundException;
import com.exam.system.repository.ExamRepository;
import com.exam.system.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ExamService
 * This proves the improved code works correctly
 */
@ExtendWith(MockitoExtension.class)
class ExamServiceTest {

    @Mock
    private ExamRepository examRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExamService examService;

    @Test
    void testCreateExam_Success() {
        // Given
        String username = "admin";
        User admin = new User();
        admin.setUserId(1L);
        admin.setUsername(username);
        admin.setRole(User.UserRole.ADMIN);

        CreateExamRequest request = new CreateExamRequest();
        request.setExamTitle("Test Exam");
        request.setExamDescription("Test Description");
        request.setDurationMinutes(60);
        request.setTotalMarks(100);
        request.setPassingMarks(40);

        List<QuestionDTO> questions = new ArrayList<>();
        QuestionDTO question = new QuestionDTO();
        question.setQuestionText("What is 2+2?");
        question.setQuestionType("MCQ");
        question.setMarks(5);
        question.setCorrectOption("A");

        List<OptionDTO> options = new ArrayList<>();
        options.add(new OptionDTO("A", "4"));
        options.add(new OptionDTO("B", "5"));
        question.setOptions(options);

        questions.add(question);
        request.setQuestions(questions);

        Exam savedExam = new Exam();
        savedExam.setExamId(1L);
        savedExam.setExamTitle("Test Exam");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(admin));
        when(examRepository.save(any(Exam.class))).thenReturn(savedExam);

        // When
        Exam result = examService.createExam(request, username);

        // Then
        assertNotNull(result);
        assertEquals("Test Exam", result.getExamTitle());
        verify(userRepository, times(1)).findByUsername(username);
        verify(examRepository, times(1)).save(any(Exam.class));
    }

    @Test
    void testCreateExam_UserNotFound_ThrowsException() {
        // Given
        String username = "nonexistent";
        CreateExamRequest request = new CreateExamRequest();

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> {
            examService.createExam(request, username);
        });
    }

    @Test
    void testGetAllExams() {
        // Given
        List<Exam> exams = new ArrayList<>();
        Exam exam1 = new Exam();
        exam1.setExamId(1L);
        exam1.setExamTitle("Exam 1");
        exams.add(exam1);

        when(examRepository.findAll()).thenReturn(exams);

        // When
        List<Exam> result = examService.getAllExams();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Exam 1", result.get(0).getExamTitle());
    }
}
