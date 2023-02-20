package com.example.RCCC03.payroll.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payroll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long company_id;
    private String note;
    private LocalDateTime date;


}
