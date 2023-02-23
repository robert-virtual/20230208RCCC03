package com.example.RCCC03.auth.service;

import com.example.RCCC03.auth.model.*;
import com.example.RCCC03.auth.repository.RoleRepository;
import com.example.RCCC03.auth.repository.UserRepository;
import com.example.RCCC03.config.BasicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final RoleRepository roleRepo;

    public Optional<User> info(String email) {
        return userRepository.findByEmail(email);
    }

    public AuthResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .map(user1 -> {
                    user1.setLast_login(LocalDateTime.now());
                    return user1;
                })
                .orElseThrow();
        var jwt = jwtService.generateToken(user);

        // to avoid returning the user his encrypted password (security reasons)
        user.setPassword(null);
        return AuthResponse.builder()
                .token(jwt)
                .user(user)
                .build();

    }

    public ResponseEntity<BasicResponse<AuthResponse>> register(RegisterRequest registerRequest) throws Exception {
        // verify that the user has permission to create accounts
        var authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if (authorities.stream().noneMatch(authority -> authority.getAuthority().matches("accounts_creator"))) {

            return new ResponseEntity<>(
                    BasicResponse.<AuthResponse>builder()
                            .error("User does not have permission to create users")
                            .build(),
                    HttpStatus.UNAUTHORIZED
            );
        }
        var user = User.builder()
                .customerId(registerRequest.getCustomer_id())
                .email(registerRequest.getEmail())
                .role(registerRequest.getRole())
                .created_at(LocalDateTime.now())
                .failed_logins(0)
                .status(true)
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .build();
        userRepository.save(user);
        var jwt = jwtService.generateToken(user);
        return new ResponseEntity<>(
                BasicResponse.<AuthResponse>builder()
                        .data_count(1)
                        .data(
                                AuthResponse.builder()
                                        .token(jwt)
                                        .user(user)
                                        .build()
                        )
                        .build(),
                HttpStatus.CREATED
        );

    }

    public Role getRole(int id) {
        return roleRepo.findById(id).orElseThrow();
    }

}
