package com.example.RCCC03.payroll.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany
    @JoinColumn(name = "payroll_id",referencedColumnName = "id")
    private List<PayrollDetail> details = new ArrayList<>();

    public void addDetail(PayrollDetail detail){
      details.add(detail);
    }

}
