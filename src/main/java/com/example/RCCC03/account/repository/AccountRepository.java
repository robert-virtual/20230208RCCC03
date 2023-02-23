package com.example.RCCC03.account.repository;


import com.example.RCCC03.account.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account,Long> {
    List<Account> findAllByCustomerId(long customerId);
}
