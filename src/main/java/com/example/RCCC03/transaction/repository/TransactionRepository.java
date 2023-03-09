package com.example.RCCC03.transaction.repository;

import com.example.RCCC03.account.model.Account;
import com.example.RCCC03.auth.model.User;
import com.example.RCCC03.transaction.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    List<Transaction> findAllByAccount(Account account);
    List<Transaction> findAllByOperator(User operator);
    List<Transaction> findAllByAuthorizer(User authorizer);
}
