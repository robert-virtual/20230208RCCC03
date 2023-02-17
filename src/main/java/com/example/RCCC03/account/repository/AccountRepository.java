package com.example.RCCC03.account.repository;


import com.example.RCCC03.account.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account,Long> {
    //Iterable<Account> getAllByCustomer_id(long customer_id);
}
