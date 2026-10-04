package com.whistledrop.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    ResponseEntity<?> handleApi(ApiException e) { return ResponseEntity.status(e.getStatus()).body(body(e.getStatus().value(), e.getMessage())); }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) errors.put(error.getField(), error.getDefaultMessage());
        return ResponseEntity.badRequest().body(Map.of("timestamp", Instant.now(), "status", 400, "error", "Validation failed", "details", errors));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<?> handleOther(Exception e) { return ResponseEntity.internalServerError().body(body(500, "An unexpected error occurred")); }

    private Map<String, Object> body(int status, String message) { return Map.of("timestamp", Instant.now(), "status", status, "message", message); }
}
