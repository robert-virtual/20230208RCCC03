package com.example.RCCC03.auth.repository;


import com.example.RCCC03.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    void deleteAllByCustomerId(long customerId);
}
