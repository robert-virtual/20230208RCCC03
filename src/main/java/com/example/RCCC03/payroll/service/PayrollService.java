package com.example.RCCC03.payroll.service;

import com.example.RCCC03.payroll.model.Payroll;
import com.example.RCCC03.payroll.repository.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PayrollService {
   private final PayrollRepository payrollRepository;
    public Payroll createPayrollWithDetails(Payroll payroll){
        return payrollRepository.save(payroll);
    }
}
