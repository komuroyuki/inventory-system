package com.inventory.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleNotFound(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage() + System.lineSeparator());
    }
}
