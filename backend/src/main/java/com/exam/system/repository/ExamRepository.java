package com.exam.system.repository;

import com.exam.system.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Exam entity
 */
@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    /**
     * Find all active exams
     */
    List<Exam> findByIsActive(Boolean isActive);

    /**
     * Find exams created by a specific user
     */
    List<Exam> findByCreatedBy_UserId(Long userId);

    /**
     * Find available exams (active and within time range)
     */
    @Query("SELECT e FROM Exam e WHERE e.isActive = true AND e.startTime <= :currentTime AND e.endTime >= :currentTime")
    List<Exam> findAvailableExams(LocalDateTime currentTime);

    /**
     * Find exams by title containing keyword
     */
    List<Exam> findByExamTitleContainingIgnoreCase(String keyword);

    /**
     * Count exams by active status
     */
    long countByIsActive(Boolean isActive);
}
