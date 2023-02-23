package io.nikolamicic21.mvpmatchvendingmachine.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
