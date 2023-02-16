package com.example.RCCC03.account;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountRepository accountRepository;
   @PostMapping("/create") ResponseEntity<Account> create(
          @RequestBody Account body
   ){
       return ResponseEntity.ok(accountRepository.save(body));
   }
}
