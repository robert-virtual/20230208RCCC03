package com.example.RCCC03.transaction.controller;

import com.example.RCCC03.account.model.Account;
import com.example.RCCC03.config.BasicResponse;
import com.example.RCCC03.transaction.dto.DebitEmployees;
import com.example.RCCC03.transaction.model.Transaction;
import com.example.RCCC03.transaction.model.TransactionDetail;
import com.example.RCCC03.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping("/all/{account_number}")
    public BasicResponse<List<Transaction>> all(@PathVariable long account_number) {
        return transactionService.all(
                Account
                        .builder()
                        .account_number(account_number)
                        .build()
        );
    }

    @PostMapping("/authorize")
    public BasicResponse<Transaction> authorize(@RequestBody TransactionDetail body) {
        return transactionService.authorize(body);
    }

    @PostMapping("/create")
    public BasicResponse<Transaction> create(@RequestBody Transaction body) {
        return transactionService.create(body);
    }

    @PostMapping("/debit/employees")
    public BasicResponse<Transaction> debitEmployees(@RequestBody DebitEmployees body) {
        return transactionService.debitEmployees(body);
    }
}
