package com.exam.system.exception;

/**
 * Exception thrown when a user is not found
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String username) {
        super("User not found: " + username);
    }

    public UserNotFoundException(Long userId) {
        super("User not found with ID: " + userId);
    }
}
