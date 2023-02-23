package io.nikolamicic21.mvpmatchvendingmachine.exception;

public class ProductAmountNotAvailableException extends RuntimeException {
    public ProductAmountNotAvailableException(String message) {
        super(message);
    }
}
