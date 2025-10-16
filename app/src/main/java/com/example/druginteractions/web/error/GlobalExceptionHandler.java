package com.example.druginteractions.web.error;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<ErrorDetail> details = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> new ErrorDetail(error.getField(), error.getDefaultMessage()))
            .collect(Collectors.toList());

        ErrorResponse error = new ErrorResponse(
            "VALIDATION_ERROR",
            "Invalid request parameters",
            details
        );
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        List<ErrorDetail> details = ex.getConstraintViolations().stream()
            .map(violation -> new ErrorDetail(
                violation.getPropertyPath().toString(),
                violation.getMessage()
            ))
            .collect(Collectors.toList());

        ErrorResponse error = new ErrorResponse(
            "VALIDATION_ERROR",
            "Invalid request parameters",
            details
        );
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ErrorResponse> handleWebClientError(WebClientResponseException ex) {
        ErrorResponse error = new ErrorResponse(
            "UPSTREAM_ERROR",
            "Error communicating with OpenFDA API",
            List.of()
        );
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error);
    }

    record ErrorResponse(String code, String message, List<ErrorDetail> details) {}
    record ErrorDetail(String field, String message) {}
}
