package com.example.RCCC03.payroll.controller;

import com.example.RCCC03.payroll.model.Payroll;
import com.example.RCCC03.payroll.repository.PayrollRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payroll")
public class PayrollController {

    private PayrollRepository payrollRepository;
    @PostMapping("/")
    private Payroll createPayroll(
            @RequestBody Payroll body
    ){
       return  payrollRepository.save(body);
    }
}
