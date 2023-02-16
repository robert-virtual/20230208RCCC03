package com.example.RCCC03.auth.controller;

import com.example.RCCC03.auth.model.AuthResponse;
import com.example.RCCC03.auth.model.LoginRequest;
import com.example.RCCC03.auth.model.RegisterRequest;
import com.example.RCCC03.auth.model.User;
import com.example.RCCC03.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest body) {
       return ResponseEntity.ok(authService.login(body));
    }
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest body) {
        return ResponseEntity.ok(authService.register(body));
    }
    @GetMapping("/info")
    public ResponseEntity<Optional<User>> info() {
        var name = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(authService.info(name));
    }
}
