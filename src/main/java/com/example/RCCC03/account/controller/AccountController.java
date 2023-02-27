package com.example.RCCC03.account.controller;

import com.example.RCCC03.account.repository.AccountRepository;
import com.example.RCCC03.account.model.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountRepository accountRepository;

    @PostMapping("/create")
    ResponseEntity<Account> create(
            @RequestBody Account body
    ) {
        System.out.println(body.getCustomerId());
        return ResponseEntity.ok(
                accountRepository.save(
                        Account
                                .builder()
                                .account_type(body.getAccount_type())
                                .customerId(body.getCustomerId())
                                .status(1)
                                .created_at(LocalDateTime.now())
                                .available_balance("0")
                                .held_balance("0")
                                .build()
                )
        );
    }
}
