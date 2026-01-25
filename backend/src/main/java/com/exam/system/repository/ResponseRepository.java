package com.exam.system.repository;

import com.exam.system.entity.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Response entity
 */
@Repository
public interface ResponseRepository extends JpaRepository<Response, Long> {

    /**
     * Find all responses for a specific attempt
     */
    List<Response> findByExamAttempt_AttemptId(Long attemptId);

    /**
     * Find response for a specific question in an attempt
     */
    Optional<Response> findByExamAttempt_AttemptIdAndQuestion_QuestionId(Long attemptId, Long questionId);

    /**
     * Count correct responses for an attempt
     */
    Long countByExamAttempt_AttemptIdAndIsCorrect(Long attemptId, Boolean isCorrect);

    /**
     * Count total responses for an attempt
     */
    Long countByExamAttempt_AttemptId(Long attemptId);

    /**
     * Delete all responses for an attempt
     */
    void deleteByExamAttempt_AttemptId(Long attemptId);
}
