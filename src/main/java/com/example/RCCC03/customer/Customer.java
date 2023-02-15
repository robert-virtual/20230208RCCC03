package com.example.RCCC03.customer;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@Builder
public class Customer {

    public Customer(){
        this.created_at = LocalDateTime.now();
        this.status = true;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String lastname;
    private Date birthdate;
    private String email;
    private String phone;
    private LocalDateTime created_at;
    private boolean status;


}
