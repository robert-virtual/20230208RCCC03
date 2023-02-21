package com.example.RCCC03.customer.repository;

import com.example.RCCC03.customer.model.CompanyEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyEmployeeRepository extends JpaRepository<CompanyEmployee,Long> {
}
