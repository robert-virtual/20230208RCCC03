package com.example.RCCC03.transaction.repository;

import com.example.RCCC03.transaction.model.TransactionStatus;
import com.example.RCCC03.transaction.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionTypeRepository extends JpaRepository<TransactionType,Long> {
}
