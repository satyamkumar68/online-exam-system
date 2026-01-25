package com.exam.system.repository;

import com.exam.system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if username exists
     */
    Boolean existsByUsername(String username);

    /**
     * Check if email exists
     */
    Boolean existsByEmail(String email);

    /**
     * Find all users by role
     */
    List<User> findByRole(User.UserRole role);

    /**
     * Find all active users
     */
    List<User> findByIsActive(Boolean isActive);

    /**
     * Count users by role
     */
    long countByRole(User.UserRole role);
}
