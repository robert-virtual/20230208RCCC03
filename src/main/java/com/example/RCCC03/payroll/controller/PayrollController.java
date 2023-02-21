package com.example.RCCC03.payroll.controller;

import com.example.RCCC03.payroll.model.Payroll;
import com.example.RCCC03.payroll.service.PayrollService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payroll")
public class PayrollController {

    private PayrollService payrollService;
    @PostMapping("/create")
    private Payroll createPayroll(
            @RequestBody Payroll body
    ){
       return  payrollService.createPayrollWithDetails(body);
    }
}
