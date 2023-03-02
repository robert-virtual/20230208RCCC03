package com.example.RCCC03.account.service;

import com.example.RCCC03.account.model.Account;
import com.example.RCCC03.account.repository.AccountRepository;
import com.example.RCCC03.config.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AuditLogService auditLogService;
    private final AccountRepository accountRepository;

    public Account create(Account body) {
        Account account = accountRepository.save(
                Account
                        .builder()
                        .account_type(body.getAccount_type())
                        .customerId(body.getCustomerId())
                        .status(1)
                        .created_at(LocalDateTime.now())
                        .available_balance("0")
                        .held_balance("0")
                        .build()
        );
        auditLogService.audit("create account",account);
        return account;
    }
}
