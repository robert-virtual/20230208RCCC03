package com.example.RCCC03.payroll.repository;


import com.example.RCCC03.payroll.model.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayrollRepository extends JpaRepository<Payroll,Long> {
}
