package com.example.RCCC03.customer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "company_employee")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyEmployee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long company_id;
    private long employee_id;
}
