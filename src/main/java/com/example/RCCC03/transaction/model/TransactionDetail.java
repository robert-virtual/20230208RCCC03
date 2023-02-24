package com.example.RCCC03.transaction.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity(name = "transaction_detail")
@Data
public class TransactionDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "bigint primary key identity")
    private long id;
    private long transaction_id;
    private long target_account;
    private String amount;

}
