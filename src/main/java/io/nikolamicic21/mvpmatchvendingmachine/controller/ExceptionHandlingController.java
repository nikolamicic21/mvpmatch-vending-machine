package io.nikolamicic21.mvpmatchvendingmachine.controller;

import io.nikolamicic21.mvpmatchvendingmachine.exception.*;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
class ExceptionHandlingController {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        return ResponseEntity.of(ProblemDetail.forStatusAndDetail(BAD_REQUEST, "Request body not readable")).build();
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    ResponseEntity<String> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException exception) {
        return ResponseEntity.of(ProblemDetail.forStatusAndDetail(BAD_REQUEST, "Please specify correct content type")).build();
    }

    @ExceptionHandler(ProductNotFoundException.class)
    ResponseEntity<String> handleProductNotFoundException(ProductNotFoundException exception) {
        return ResponseEntity.of(ProblemDetail.forStatusAndDetail(NOT_FOUND, exception.getMessage())).build();
    }

    @ExceptionHandler(ProductCreationException.class)
    ResponseEntity<String> handleProductCreationException(ProductCreationException exception) {
        return ResponseEntity.of(ProblemDetail.forStatusAndDetail(BAD_REQUEST, exception.getMessage())).build();
    }

    @ExceptionHandler(ProductUpdateException.class)
    ResponseEntity<String> handleProductUpdateException(ProductUpdateException exception) {
        return ResponseEntity.of(ProblemDetail.forStatusAndDetail(BAD_REQUEST, exception.getMessage())).build();
    }

    @ExceptionHandler(ProductAmountNotAvailableException.class)
    ResponseEntity<String> handleProductAmountNotAvailableException(ProductAmountNotAvailableException exception) {
        return ResponseEntity.of(ProblemDetail.forStatusAndDetail(BAD_REQUEST, exception.getMessage())).build();
    }

    @ExceptionHandler(UserNotFoundException.class)
    ResponseEntity<String> handleUserNotFoundException(UserNotFoundException exception) {
        return ResponseEntity.of(ProblemDetail.forStatusAndDetail(BAD_REQUEST, exception.getMessage())).build();
    }

    @ExceptionHandler(UserCreationException.class)
    ResponseEntity<String> handleUserCreationException(UserCreationException exception) {
        return ResponseEntity.of(ProblemDetail.forStatusAndDetail(BAD_REQUEST, exception.getMessage()))
                .build();
    }

    @ExceptionHandler(UserDepositIncrementException.class)
    ResponseEntity<String> handleUserDepositIncrementException(UserDepositIncrementException exception) {
        return ResponseEntity.of(ProblemDetail.forStatusAndDetail(BAD_REQUEST, exception.getMessage()))
                .build();
    }

    @ExceptionHandler(UserInsufficientDepositException.class)
    ResponseEntity<String> handleUserInsufficientDepositException(UserInsufficientDepositException exception) {
        return ResponseEntity.of(ProblemDetail.forStatusAndDetail(BAD_REQUEST, exception.getMessage())).build();
    }
}
