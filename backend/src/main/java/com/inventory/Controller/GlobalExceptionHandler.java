package com.inventory.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleNotValid(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage() + System.lineSeparator());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage() + System.lineSeparator());
    }
}
