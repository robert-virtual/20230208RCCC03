package com.example.RCCC03.payroll.service;

import com.example.RCCC03.config.BasicResponse;
import com.example.RCCC03.payroll.model.Payroll;
import com.example.RCCC03.payroll.repository.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PayrollService {
   private final PayrollRepository payrollRepo;
    public BasicResponse<Payroll> createPayrollWithDetails(Payroll payroll){
        try {
            return new BasicResponse<>(
                    payrollRepo.save(payroll),
                    1,
                    null,
                    null
            );
        }catch (Exception e){
            return new BasicResponse<>(
                    null,
                    0,
                    null,
                   e.getMessage()
            );
        }
    }
}
