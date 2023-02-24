package com.example.RCCC03.transaction.dto;

import com.example.RCCC03.transaction.model.Transaction;
import lombok.Data;

@Data
public class DebitEmployees extends Transaction {
    private String amount;
    private boolean all_employees;
}
