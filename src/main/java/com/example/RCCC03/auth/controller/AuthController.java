package com.example.RCCC03.auth.controller;

import com.example.RCCC03.auth.model.AuthResponse;
import com.example.RCCC03.auth.model.LoginRequest;
import com.example.RCCC03.auth.model.RegisterRequest;
import com.example.RCCC03.auth.model.User;
import com.example.RCCC03.auth.service.AuthService;
import com.example.RCCC03.config.BasicResponse;
import jakarta.persistence.Basic;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/login")
    public ResponseEntity<BasicResponse<AuthResponse>> login(@RequestBody LoginRequest body) {
       return ResponseEntity.ok(authService.login(body));
    }
    @PutMapping("/password")
    public ResponseEntity<BasicResponse<String>> updatePassword(
            @RequestBody User body
    ) {
        return authService.updatePassword(body);
    }
    @PostMapping("/password")
    public BasicResponse<String> forgotPassword(@RequestBody RegisterRequest body) throws Exception {
        return authService.forgotPassword(body);
    }
    @PostMapping("/register")
    public ResponseEntity<BasicResponse<User>> register(@RequestBody RegisterRequest body) throws Exception {
        return ResponseEntity.ok(authService.register(body));
    }
    @GetMapping("/info")
    public ResponseEntity<BasicResponse<User>> info() {
        var name = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(authService.info(name));
    }
}
