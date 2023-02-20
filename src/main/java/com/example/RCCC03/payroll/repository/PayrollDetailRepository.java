package com.example.RCCC03.payroll.repository;

import com.example.RCCC03.payroll.model.PayrollDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayrollDetailRepository extends JpaRepository<PayrollDetail,Long> {
}
