package com.example.RCCC03.auth.service;

import com.example.RCCC03.auth.repository.UserRepository;
import com.example.RCCC03.auth.model.AuthResponse;
import com.example.RCCC03.auth.model.LoginRequest;
import com.example.RCCC03.auth.model.RegisterRequest;
import com.example.RCCC03.auth.model.User;
import com.example.RCCC03.customer.model.Customer;
import com.example.RCCC03.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
   private final JwtService jwtService;
   private final AuthenticationManager authenticationManager;
   private final PasswordEncoder passwordEncoder;

   private final UserRepository userRepository;
   private final CustomerRepository customerRepository;

   public AuthResponse login(LoginRequest loginRequest){
       authenticationManager.authenticate(
               new UsernamePasswordAuthenticationToken(
                       loginRequest.getEmail(),
                       loginRequest.getPassword()
               )
       );
       var user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow();
       var jwt = jwtService.generateToken(user);
       return AuthResponse.builder()
               .token(jwt)
               .build();

   }
    public AuthResponse register(RegisterRequest registerRequest){
       var customer = Customer.builder()
               .name(registerRequest.getName())
               .birthdate(registerRequest.getBirthdate())
               .phone(registerRequest.getPhone())
               .email(registerRequest.getEmail())
               .created_at(LocalDateTime.now())
               .status(true)
               .lastname(registerRequest.getLastname())
               .build();
        long customer_id = customerRepository.save(customer).getId();
        //Customer db_customer = customerRepository.findById(customer_id).orElseThrow();
        System.out.println("customer id: "+customer_id);
        var user = User.builder()
                .customer_id(customer_id)
                .email(registerRequest.getEmail())
                .role(registerRequest.getRole())
                .created_at(LocalDateTime.now())
                .failed_logins(0)
                .status(true)
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .build();
        userRepository.save(user);
        var jwt = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwt)
                .build();

    }


}
