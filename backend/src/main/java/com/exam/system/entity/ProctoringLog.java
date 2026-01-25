package com.exam.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity class representing Proctoring Logs during an Exam
 */
@Entity
@Table(name = "proctoring_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProctoringLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    private ExamAttempt examAttempt;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    @Column(name = "event_description", columnDefinition = "TEXT")
    private String eventDescription;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity")
    private Severity severity = Severity.LOW;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }

    public enum EventType {
        MULTIPLE_FACES, NO_FACE, FACE_DETECTED, SUSPICIOUS_ACTIVITY
    }

    public enum Severity {
        LOW, MEDIUM, HIGH
    }
}
