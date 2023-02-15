package com.example.RCCC03.auth;


import com.example.RCCC03.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByeEmail(String email);
}
