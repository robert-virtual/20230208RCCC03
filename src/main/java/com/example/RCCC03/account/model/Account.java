package com.example.RCCC03.account.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@Builder
public class Account {
    public Account(){
        this.available_balance = "0";
        this.held_balance = "0";
        this.status =1;
        this.created_at = LocalDateTime.now();
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long account_number;

    private int account_type;
    private String available_balance;
    private String held_balance;


    private long customer_id;
    private LocalDateTime created_at;
    private int status;

}