package io.nikolamicic21.mvpmatchvendingmachine.exception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String message) {
        super(message);
    }
}
