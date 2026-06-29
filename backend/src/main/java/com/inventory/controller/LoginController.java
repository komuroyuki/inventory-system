package com.inventory.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.dto.LoginRequest;
import com.inventory.dto.LoginResponse;
import com.inventory.service.LoginService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService service;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest requestBody) {

        try {
            LoginResponse response = service.authenticate(requestBody);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error_code", "AUTH_FAILED");
            error.put("message", e.getMessage());
            // ロック時のメッセージなら 423 を返す
        if (e.getMessage().contains("ロック")) {
            return new ResponseEntity<>(error, HttpStatus.LOCKED); 
        }
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }
    }
}