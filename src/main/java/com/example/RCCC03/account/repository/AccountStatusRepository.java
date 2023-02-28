package com.example.RCCC03.account.repository;

import com.example.RCCC03.account.model.AccountStatus;
import com.example.RCCC03.account.model.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountStatusRepository extends JpaRepository<AccountStatus,Long> {
}
