package com.example.RCCC03.transaction.repository;

import com.example.RCCC03.transaction.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {
}
