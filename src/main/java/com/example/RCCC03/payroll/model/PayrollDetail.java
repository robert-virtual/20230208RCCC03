package com.example.RCCC03.payroll.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "payroll_detail")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayrollDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long payroll_id;
    private long employee_id;
    private String amount;
}
