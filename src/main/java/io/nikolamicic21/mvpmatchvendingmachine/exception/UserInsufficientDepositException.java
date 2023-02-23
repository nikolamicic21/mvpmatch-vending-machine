package io.nikolamicic21.mvpmatchvendingmachine.exception;

public class UserInsufficientDepositException extends RuntimeException {
    public UserInsufficientDepositException(String message) {
        super(message);
    }
}
