package com.exam.system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 5000, nullable = false)
    private String content;

    private String option1;
    private String option2;
    private String option3;
    private String option4;

    @Column(nullable = false)
    private String answer; // Store correct option (e.g., "option1")

    @Column(length = 100)
    private String category; // For question bank categorization

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = true) // Nullable for question bank
    @JsonIgnore // Prevent infinite recursion in bidirectional relationships
    private Exam exam;
}
