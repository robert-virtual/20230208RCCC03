package com.example.RCCC03.transaction.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "transaction_detail")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "bigint primary key identity")
    private long id;
    private long transaction_id;
    private String account_holder;
    @JsonProperty("target_account")
    @Column(name = "target_account")
    private long targetAccount;
    private String amount;

}
