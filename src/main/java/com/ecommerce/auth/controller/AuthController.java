package com.ecommerce.auth.controller;

import com.ecommerce.auth.dto.Requests.LoginRequest;
import com.ecommerce.auth.dto.Responses.LoginResponse;
import com.ecommerce.auth.dto.Requests.RegisterRequest;
import com.ecommerce.auth.dto.Responses.RegisterResponse;
import com.ecommerce.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Register request received for email: {}", registerRequest.getEmail());
       RegisterResponse response= authService.register(registerRequest);
        log.info("User registered successfully with email: {}", registerRequest.getEmail());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login request received for email: {}", loginRequest.getEmail());
        LoginResponse loginResponse = authService.login(loginRequest);
        log.info("User logged in successfully with email: {}", loginRequest.getEmail());
        return ResponseEntity.ok(loginResponse);
    }
}
