package com.example.RCCC03.auth.service;

import com.example.RCCC03.auth.model.*;
import com.example.RCCC03.auth.repository.RoleRepository;
import com.example.RCCC03.auth.repository.UserRepository;
import com.example.RCCC03.config.BasicResponse;
import com.example.RCCC03.customer.model.Customer;
import com.example.RCCC03.customer.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JavaMailSender javaMailSender;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepo;
    private final CustomerRepository customerRepo;
    private final RoleRepository roleRepo;

    public User info(String email) {
        User user = userRepo.findByEmail(email).orElseThrow();
        user.setPassword(null);
        return user;
    }

    public String generateStrongPassword(){

        char[] possibleCharacters = (
                "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%^&()-_=+[{]}\\|;:'\",<.>/?"
        ).toCharArray();
        return RandomStringUtils.random(
                8,
                0,
                possibleCharacters.length - 1,
                false,
                false,
                possibleCharacters,
                new SecureRandom()
        );
    }
    public BasicResponse<AuthResponse> login(LoginRequest loginRequest) {
        User user;
        try {
            user = userRepo.findByEmail(loginRequest.getEmail())
                    .orElseThrow();
        } catch (Exception e) {
            return BasicResponse
                    .<AuthResponse>builder()
                    .error("Bad credentials")
                    .build();
        }
        if (user.getFailed_logins() >= 5) {
            user.setStatus(false);
            userRepo.save(user);
            return BasicResponse
                    .<AuthResponse>builder()
                    .error("User blocked after 5 failed logins attempts")
                    .build();
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

        } catch (Exception e) {
            user.setFailed_logins(user.getFailed_logins() + 1);
            userRepo.save(user);
            return BasicResponse
                    .<AuthResponse>builder()
                    .error(e.getMessage())
                    .build();
        }


        // check whether the customer is active or not
        Customer customer = customerRepo.findById(user.getCustomerId()).orElseThrow();
        boolean inactive = Objects.equals(customer.getStatus(), "inactive");
        if (inactive || !user.isStatus()) {
            List<String> entities = new ArrayList<>();
            if (inactive) entities.add("Customer");
            if (!user.isStatus()) entities.add("User");
            return BasicResponse.<AuthResponse>builder()
                    .error(String.join(" and ", entities) + " disabled")
                    .build();
        }
        var jwt = jwtService.generateToken(user);
        user.setLast_login(LocalDateTime.now());
        userRepo.save(user);
        // to avoid returning the user his encrypted password (security reasons)
        user.setPassword(null);
        return BasicResponse.<AuthResponse>builder()
                .data(
                        AuthResponse.builder()
                                .token(jwt)
                                .user(user)
                                .build()
                )
                .build();
    }

    @Transactional
    public ResponseEntity<BasicResponse<AuthResponse>> register(
            RegisterRequest registerRequest
    ) throws Exception {
        // verify that the user has permission to create accounts
        var authorities = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getAuthorities();
        if (
                authorities
                        .stream()
                        .noneMatch(
                                authority -> authority
                                        .getAuthority()
                                        .matches("accounts_creator")
                        )
        ) {

            return new ResponseEntity<>(
                    BasicResponse.<AuthResponse>builder()
                            .error("User does not have permission to create users")
                            .build(),
                    HttpStatus.UNAUTHORIZED
            );
        }
        String strongPassword = generateStrongPassword();
        var user = User.builder()
                .customerId(registerRequest.getCustomer_id())
                .email(registerRequest.getEmail())
                .role(registerRequest.getRole())
                .created_at(LocalDateTime.now())
                .failed_logins(0)
                .status(true)
                .password(
                        passwordEncoder.encode(strongPassword)
                )
                .build();
        userRepo.save(user);
        // remove encrypted password
        user.setPassword(null);
        // remove encrypted password

        // send email with user credentials
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("robertocastillodev@gmail.com");
        message.setTo(registerRequest.getEmail());
        message.setSubject("Tu usuario y contraseña para la banca en linea");
        message.setText(String.format("Usuario: %s, Contraseña: %s",registerRequest.getEmail(),strongPassword));
        javaMailSender.send(message);
        // send email with user credentials

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
