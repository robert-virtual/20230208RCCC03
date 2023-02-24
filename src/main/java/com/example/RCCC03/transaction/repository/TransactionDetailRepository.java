package com.example.RCCC03.transaction.repository;

import com.example.RCCC03.account.model.Account;
import com.example.RCCC03.transaction.model.Transaction;
import com.example.RCCC03.transaction.model.TransactionDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionDetailRepository extends JpaRepository<TransactionDetail,Long> {
    List<TransactionDetail> findAllByTargetAccount(String target_account);
}
