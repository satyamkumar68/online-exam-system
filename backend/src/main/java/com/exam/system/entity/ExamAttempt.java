package com.exam.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity class representing an Exam Attempt by a Student
 */
@Entity
@Table(name = "exam_attempts", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "exam_id" }))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attempt_id")
    private Long attemptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "score", precision = 5, scale = 2)
    private BigDecimal score = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AttemptStatus status = AttemptStatus.IN_PROGRESS;

    @Column(name = "is_proctored")
    private Boolean isProctored = true;

    @PrePersist
    protected void onCreate() {
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }
    }

    public enum AttemptStatus {
        IN_PROGRESS, COMPLETED, AUTO_SUBMITTED
    }
}
