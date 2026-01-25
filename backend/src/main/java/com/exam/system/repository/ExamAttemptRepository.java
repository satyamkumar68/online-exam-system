package com.exam.system.repository;

import com.exam.system.entity.ExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ExamAttempt entity
 */
@Repository
public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {

    /**
     * Find all attempts by a specific user
     */
    List<ExamAttempt> findByUser_UserId(Long userId);

    /**
     * Find all attempts for a specific exam
     */
    List<ExamAttempt> findByExam_ExamId(Long examId);

    /**
     * Find attempt by user and exam
     */
    Optional<ExamAttempt> findByUser_UserIdAndExam_ExamId(Long userId, Long examId);

    /**
     * Check if user has already attempted an exam
     */
    Boolean existsByUser_UserIdAndExam_ExamId(Long userId, Long examId);

    /**
     * Find all attempts by status
     */
    List<ExamAttempt> findByStatus(ExamAttempt.AttemptStatus status);

    /**
     * Find user's attempts by status
     */
    List<ExamAttempt> findByUser_UserIdAndStatus(Long userId, ExamAttempt.AttemptStatus status);
}
