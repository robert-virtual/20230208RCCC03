package com.example.RCCC03.transaction.repository;

import com.example.RCCC03.transaction.model.Transaction;
import com.example.RCCC03.transaction.model.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionStatusRepository extends JpaRepository<TransactionStatus,Long> {
}
