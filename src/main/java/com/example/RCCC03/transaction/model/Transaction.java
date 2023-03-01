package com.example.RCCC03.transaction.model;

import com.example.RCCC03.account.model.Account;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "transactions")
@Audited
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
    @NotAudited
    private TransactionType transaction_type;
    // private int transaction_type;
    private String currency;
    private LocalDateTime operated_at;
    private LocalDateTime authorized_at;
    @ManyToOne
    @JoinColumn(name = "status",referencedColumnName = "id")
    @NotAudited
    private TransactionStatus status;
    //private int status;
    private String notes;
    private String authorized_by;
    private String operated_by;

    @OneToMany
    @JoinColumn(name = "transaction_id",referencedColumnName = "id")
    private List<TransactionDetail> details = new ArrayList<>();

    public void addDetail(TransactionDetail detail){
       details.add(detail);
    }



    // not mapped properties
    @Transient
    private String amount;
}
