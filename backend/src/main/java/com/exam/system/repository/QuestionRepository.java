package com.exam.system.repository;

import com.exam.system.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Question entity
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    /**
     * Find all questions for a specific exam
     */
    List<Question> findByExam_ExamId(Long examId);

    /**
     * Find all questions for a specific exam with options eagerly loaded
     * (optimized)
     * This prevents N+1 query problem by fetching options in a single query
     */
    @Query("SELECT DISTINCT q FROM Question q LEFT JOIN FETCH q.options WHERE q.exam.examId = :examId ORDER BY q.questionId")
    List<Question> findByExamIdWithOptions(@Param("examId") Long examId);

    /**
     * Count questions for a specific exam
     */
    Long countByExam_ExamId(Long examId);

    /**
     * Find questions by type for a specific exam
     */
    List<Question> findByExam_ExamIdAndQuestionType(Long examId, Question.QuestionType questionType);

    /**
     * Delete all questions for a specific exam
     */
    void deleteByExam_ExamId(Long examId);
}
