package com.example.RCCC03.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity(name = "users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int role;
    private String otp;
    private LocalDateTime otp_expires_at;
    @JsonIgnore
    private String password;
    private String email;
    private int failed_logins;

    @Column(name = "customer_id")
    private long customerId;


    private boolean status;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private LocalDateTime last_login;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<String> roles = new ArrayList<>();
        switch (role) {
            case 1 -> roles.add("operator");
            case 2 -> roles.add("authorizer");
            case 3 -> roles.add("accounts_creator");
        }
        return roles.stream().map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
