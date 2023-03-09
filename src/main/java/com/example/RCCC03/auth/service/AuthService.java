package com.example.RCCC03.auth.service;

import com.example.RCCC03.auth.model.*;
import com.example.RCCC03.auth.repository.RoleRepository;
import com.example.RCCC03.auth.repository.UserRepository;
import com.example.RCCC03.audit.AuditLogService;
import com.example.RCCC03.config.BasicResponse;
import com.example.RCCC03.customer.model.Customer;
import com.example.RCCC03.customer.repository.CustomerRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuditLogService auditLogService;

    @Value("${spring.mail.username}")
    private String mailUser;
    @Value("${app.otp.duration}")
    private long otp_duration;
    private final JavaMailSender javaMailSender;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepo;
    private final CustomerRepository customerRepo;
    private final RoleRepository roleRepo;


    public BasicResponse<String> forgotPassword(RegisterRequest registerRequest) throws MessagingException {

        User user = userRepo.findByEmail(registerRequest.getEmail()).orElseThrow();
        String otp = generateOtp();
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        helper.setFrom(mailUser);
        helper.setTo(registerRequest.getEmail());
        helper.setSubject("Evaluacion tecnica - Codigo de desbloqueo");
        helper.setText(
                String.format("""
                        <img style="width:70vw;" src="https://ci3.googleusercontent.com/proxy/FlGICNOLfVc2LF1W0xlETyPvVi5jXJxo6auBUbmEnekCLJj4TpSkrUsXZSINgdqgY9uWWStSSrf-rTGQ_1jjebcpLWJneVBg3D6oyKuSzkif5s9u=s0-d-e1-ft#https://www.bancatlan.hn/img/Encabezado_PS05_AOL_716x462px-01.png" alt="banner banco atlantidad"/>
                        <h1>Evaluacion tecnica Roberto Castillo</h1> 
                        <p>Estimado(a) %s </p>
                        <p>Este codigo permanecera activo durante %s minutos</p>
                        <p style="font-size:25px;">Usuario: %s <br/>Codigo de verificacion: <b>%s</b></p>
                        <a href="http://localhost:3000">Ingresa a la banca en linea y configura tu nueva contraseña</a>
                        """, registerRequest.getEmail(), otp_duration, registerRequest.getEmail(), otp),
                true
        );
        javaMailSender.send(message);
        user.setOtp(otp);
        user.setOtp_expires_at(LocalDateTime.now().plus(otp_duration, ChronoUnit.MINUTES));
        userRepo.save(user);
       auditLogService.audit("otp requested",user,user);
        return BasicResponse
                .<String>builder()
                .data("An OTP was send to the user email, send the OTP and the new password to PUT - /auth/password")
                .build();
    }

    public BasicResponse<User> info(String email) {
        return BasicResponse.<User>builder().data(userRepo.findByEmail(email).orElseThrow()).build();
    }

    public String generateOtp() {

        char[] possibleCharacters = (
                "0123456789"
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

    public String generateStrongPassword() {

        char[] possibleCharacters = (
                "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%-_+\\|;:'\"./"
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
            auditLogService.audit(
                    "login attempt failed email not found",
                    null
            );
            return BasicResponse
                    .<AuthResponse>builder()
                    .error("Bad credentials")
                    .build();
        }
        if (user.getFailed_logins() >= 5) {
            user.setStatus(false);
            userRepo.save(user);
            auditLogService.audit(
                    "User blocked after 5 failed logins attempts",
                    user,
                    user
            );
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
            auditLogService.audit(
                    "login attempt failed wrong password",
                    user,
                    user
            );
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
            auditLogService.audit(
                    "login attempt failed inactive user or customer",
                    user,
                    user
            );
            return BasicResponse.<AuthResponse>builder()
                    .error(String.join(" and ", entities) + " disabled")
                    .build();
        }
        var jwt = jwtService.generateToken(user);
        user.setLast_login(LocalDateTime.now());
        userRepo.save(user);
        auditLogService.audit(
                "login attempt successful",
                user,
                user
        );
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
    public BasicResponse<User> register(
            RegisterRequest registerRequest
    ) throws Exception {
        // verify that the user has permission to create accounts
        var authorities = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getAuthorities();
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User loggedUser = userRepo.findByEmail(email).orElseThrow();
        if (
                authorities
                        .stream()
                        .noneMatch(
                                authority -> authority
                                        .getAuthority()
                                        .matches("user_creator")
                        )
        ) {

            auditLogService.audit(
                    "User does not have permission to create users",
                    loggedUser,
                    loggedUser
            );
            return BasicResponse.<User>builder()
                            .error("User does not have permission to create users")
                            .build();
        }
        String strongPassword = generateStrongPassword();
        User user = User.builder()
                .customerId(registerRequest.getCustomer_id())
                .email(registerRequest.getEmail())
                .roles(registerRequest.getRoles())
                .created_at(LocalDateTime.now())
                .failed_logins(0)
                .status(true)
                .password(
                        passwordEncoder.encode(strongPassword)
                )
                .build();

        userRepo.save(user);
        auditLogService.audit(
                "user created",
                user,
                loggedUser
        );

        // send email with user credentials
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        helper.setFrom(mailUser);
        helper.setTo(registerRequest.getEmail());
        helper.setSubject("Tu usuario y contraseña para la banca en linea");
        helper.setText(
                String.format("""
                        <img style="width:70vw;" src="https://ci3.googleusercontent.com/proxy/FlGICNOLfVc2LF1W0xlETyPvVi5jXJxo6auBUbmEnekCLJj4TpSkrUsXZSINgdqgY9uWWStSSrf-rTGQ_1jjebcpLWJneVBg3D6oyKuSzkif5s9u=s0-d-e1-ft#https://www.bancatlan.hn/img/Encabezado_PS05_AOL_716x462px-01.png" alt="banner banco atlantidad"/>
                        <h1>Evaluacion tecnica Roberto Castillo</h1> 
                        <p>FELICIDADES %s </p>
                        <p>Ya tienes creado tu acceso creado de Banca en linea. Te damos la bienvenida  para que puedas realizar tus transacciones desde tu celular o computadora.</p>
                        <p style="font-size:25px;">Usuario: %s <br/>Contraseña: <b>%s</b></p>
                        <a href="http://localhost:3000">Ingresa a la banca en linea y configura tu nueva contraseña</a>
                        """, registerRequest.getEmail(), registerRequest.getEmail(), strongPassword),
                true
        );
        javaMailSender.send(message);
        // send email with user credentials

        return BasicResponse.<User>builder()
                        .data(user)
                        .build();

    }

    public Role getRole(int id) {
        return roleRepo.findById(id).orElseThrow();
    }

    public ResponseEntity<BasicResponse<String>> updatePassword(User body) {
        User user = userRepo.findByEmail(body.getEmail()).orElseThrow();
        if (
                user.getOtp_expires_at().isBefore(LocalDateTime.now())
        ) {
            auditLogService.audit("update password failed due to expired otp", user, user);
            return new ResponseEntity<>(
                    BasicResponse
                            .<String>builder()
                            .error("Otp expired")
                            .build()
                    , HttpStatus.UNAUTHORIZED);
        }
        if (
                !Objects.equals(user.getOtp(), body.getOtp())
        ) {
            auditLogService.audit("update password failed due to invalid otp", user, user);
            return new ResponseEntity<>(
                    BasicResponse
                            .<String>builder()
                            .error("invalid otp")
                            .build()
                    , HttpStatus.UNAUTHORIZED);
        }
        // prevent further usage of this otp
        user.setOtp_expires_at(LocalDateTime.now());
        user.setUpdated_at(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(body.getPassword()));
        userRepo.save(user);

        auditLogService.audit("update password successful", user, user);
        return new ResponseEntity<>(
                BasicResponse
                        .<String>builder()
                        .data("password updated successfully")
                        .build()
                , HttpStatus.UNAUTHORIZED);
    }
}
