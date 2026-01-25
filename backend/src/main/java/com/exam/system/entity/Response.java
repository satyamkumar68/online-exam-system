package com.exam.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity class representing a Student's Response to a Question
 */
@Entity
@Table(name = "responses", uniqueConstraints = @UniqueConstraint(columnNames = { "attempt_id", "question_id" }))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_id")
    private Long responseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    private ExamAttempt examAttempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "selected_option", length = 1)
    private String selectedOption;

    @Column(name = "is_correct")
    private Boolean isCorrect = false;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    @PrePersist
    @PreUpdate
    protected void onSave() {
        answeredAt = LocalDateTime.now();
    }
}
