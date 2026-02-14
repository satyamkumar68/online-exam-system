package com.exam.system.repository;

import com.exam.system.entity.Exam;
import com.exam.system.entity.ExamResult;
import com.exam.system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {
    List<ExamResult> findByUser(User user);
    List<ExamResult> findByExam(Exam exam);
}
