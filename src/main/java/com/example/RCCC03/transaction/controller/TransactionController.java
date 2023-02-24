package com.example.RCCC03.transaction.controller;

import com.example.RCCC03.config.BasicResponse;
import com.example.RCCC03.transaction.dto.DebitEmployees;
import com.example.RCCC03.transaction.model.Transaction;
import com.example.RCCC03.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    @PostMapping("/create")
    public BasicResponse<Transaction> create(@RequestBody Transaction body){
        return transactionService.create(body);
    }

    @PostMapping("/debit/employees")
    public BasicResponse<Transaction> debitEmployees(@RequestBody DebitEmployees body){
        return transactionService.debitEmployees(body);
    }
}
