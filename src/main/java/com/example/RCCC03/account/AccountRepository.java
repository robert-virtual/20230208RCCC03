package com.example.RCCC03.account;


import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account,Long> {
    //Iterable<Account> getAllByCustomer_id(long customer_id);
}
