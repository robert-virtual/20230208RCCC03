package com.example.RCCC03.transaction.model;

import com.example.RCCC03.account.model.Account;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "source_account",referencedColumnName = "account_number")
    private Account account;
    //private long source_account;
    @ManyToOne
    @JoinColumn(name = "transaction_type",referencedColumnName = "id")
    private TransactionType transactionType;
    // private int transaction_type;
    private String currency;
    private LocalDateTime date;
    @ManyToOne
    @JoinColumn(name = "status",referencedColumnName = "id")
    private TransactionStatus status;
    //private int status;
    private String notes;

    @OneToMany
    @JoinColumn(name = "transaction_id",referencedColumnName = "id")
    private List<TransactionDetail> details = new ArrayList<>();

    public void addDetail(TransactionDetail detail){
       details.add(detail);
    }
}
