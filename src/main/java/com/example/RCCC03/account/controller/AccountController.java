package com.example.RCCC03.account.controller;

import com.example.RCCC03.account.repository.AccountRepository;
import com.example.RCCC03.account.model.Account;
import com.example.RCCC03.account.service.AccountService;
import com.example.RCCC03.config.BasicResponse;
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
    private final AccountService accountService;

    @PostMapping("/create")
    ResponseEntity<BasicResponse<Account>> create(
            @RequestBody Account body
    ) {
        return ResponseEntity.ok(accountService.create(body));
    }
}
