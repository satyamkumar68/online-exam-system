package com.exam.system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exams")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "max_time_minutes")
    private Integer maxTimeMinutes;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // Optional: OneToMany relationship if you want to cascade operations
    // @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval =
    // true)
    // private List<Question> questions = new ArrayList<>();
    @Column(name = "start_time")
    private java.time.LocalDateTime startTime;

    @Column(name = "end_time")
    private java.time.LocalDateTime endTime;
}
