package com.exam.system.repository;

import com.exam.system.entity.ProctoringLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for ProctoringLog entity
 */
@Repository
public interface ProctoringLogRepository extends JpaRepository<ProctoringLog, Long> {

    /**
     * Find all logs for a specific attempt
     */
    List<ProctoringLog> findByExamAttempt_AttemptId(Long attemptId);

    /**
     * Find logs by event type for an attempt
     */
    List<ProctoringLog> findByExamAttempt_AttemptIdAndEventType(Long attemptId, ProctoringLog.EventType eventType);

    /**
     * Find logs by severity for an attempt
     */
    List<ProctoringLog> findByExamAttempt_AttemptIdAndSeverity(Long attemptId, ProctoringLog.Severity severity);

    /**
     * Count high severity logs for an attempt
     */
    Long countByExamAttempt_AttemptIdAndSeverity(Long attemptId, ProctoringLog.Severity severity);

    /**
     * Find all high severity logs
     */
    List<ProctoringLog> findBySeverity(ProctoringLog.Severity severity);
}
