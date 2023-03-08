package com.example.RCCC03.account.service;

import com.example.RCCC03.account.model.Account;
import com.example.RCCC03.account.model.AccountStatus;
import com.example.RCCC03.account.repository.AccountRepository;
import com.example.RCCC03.audit.AuditLogService;
import com.example.RCCC03.auth.model.User;
import com.example.RCCC03.auth.repository.UserRepository;
import com.example.RCCC03.config.BasicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AuditLogService auditLogService;
    private final AccountRepository accountRepo;
    private final UserRepository userRepo;

    public BasicResponse<Account> create(Account body) {
        if (body.getCustomerId() <= 0){
           String userEmail  = SecurityContextHolder.getContext().getAuthentication().getName();
           User user = userRepo.findByEmail(userEmail).orElseThrow();
           body.setCustomerId(user.getCustomerId());
        }
        Account account = accountRepo.save(
                Account
                        .builder()
                        .accountType(body.getAccountType())
                        .customerId(body.getCustomerId())
                        .accountStatus(AccountStatus.builder().id(1).build())
                        .created_at(LocalDateTime.now())
                        .available_balance("0")
                        .held_balance("0")
                        .build()
        );
        auditLogService.audit("create account",account);
        return BasicResponse.<Account>builder().data(account).build();
    }
}
