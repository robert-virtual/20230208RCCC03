package com.example.RCCC03.auth.repository;

import com.example.RCCC03.auth.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Integer> {
}
