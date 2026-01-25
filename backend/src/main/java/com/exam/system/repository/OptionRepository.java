package com.exam.system.repository;

import com.exam.system.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Option entity
 */
@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {

    /**
     * Find all options for a specific question
     */
    List<Option> findByQuestion_QuestionId(Long questionId);

    /**
     * Delete all options for a specific question
     */
    void deleteByQuestion_QuestionId(Long questionId);

    /**
     * Count options for a specific question
     */
    Long countByQuestion_QuestionId(Long questionId);
}
