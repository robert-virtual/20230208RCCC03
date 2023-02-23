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

    public BasicResponse<Payroll> createPayrollWithDetails(Payroll payroll) {
        try {
            return BasicResponse.<Payroll>builder()
                    .data_count(1)
                    .data(payrollRepo.save(payroll))
                    .build(

                    );
        } catch (Exception e) {
            return BasicResponse.<Payroll>builder()
                    .error(e.getMessage())
                    .build();
        }
    }
}
