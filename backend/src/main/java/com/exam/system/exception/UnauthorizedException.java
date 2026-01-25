package com.exam.system.exception;

/**
 * Exception thrown when user is not authorized to perform an action
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException() {
        super("You are not authorized to perform this action");
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}
