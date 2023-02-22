package com.example.RCCC03.service.repository;

import com.example.RCCC03.service.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service,Long> {
}
