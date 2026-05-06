package com.skeli.authservice.controller;

import com.skeli.authservice.dto.AuthResponseDto;
import com.skeli.authservice.dto.LoginRequest;
import com.skeli.authservice.dto.RegisterRequest;
import com.skeli.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/validate")
    public ResponseEntity<AuthResponseDto> validate(@RequestHeader("Authorization") String authHeader){
        String token = authHeader.substring(7); //Bearer_ - 7 characters to remove
        return ResponseEntity.ok(authService.validateAndGetUser(token));
    }
}
