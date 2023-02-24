package com.example.RCCC03.transaction.service;

import com.example.RCCC03.account.model.Account;
import com.example.RCCC03.account.repository.AccountRepository;
import com.example.RCCC03.auth.model.User;
import com.example.RCCC03.auth.repository.UserRepository;
import com.example.RCCC03.config.BasicResponse;
import com.example.RCCC03.customer.model.Customer;
import com.example.RCCC03.customer.repository.CustomerRepository;
import com.example.RCCC03.transaction.model.Transaction;
import com.example.RCCC03.transaction.model.TransactionDetail;
import com.example.RCCC03.transaction.model.TransactionStatus;
import com.example.RCCC03.transaction.repository.TransactionDetailRepository;
import com.example.RCCC03.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepo;
    private final TransactionDetailRepository transactionDetailsRepo;
    private final CustomerRepository customerRepo;
    private final UserRepository userRepo;
    private final AccountRepository accountRepo;

    public BasicResponse<Transaction> create(Transaction transaction) {
        try {
            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepo.findByEmail(userEmail).orElseThrow();
            Account source_account = accountRepo.findById(transaction.getAccount().getAccount_number()).orElseThrow();
            if (
                    source_account.getCustomerId() != user.getCustomerId()
            ) return BasicResponse
                    .<Transaction>builder()
                    .error("the account does not belong to the user requesting the action")
                    .build();

            transaction.setDate(LocalDateTime.now());
            transaction.setStatus(
                    TransactionStatus
                            .builder()
                            .id(1)
                            .build()
            );
            List<TransactionDetail> details = transaction.getDetails();
            transaction.setDetails(new ArrayList<>());
            long transaction_id = transactionRepo.save(transaction).getId();
            double total_debit = details.stream().mapToDouble(
                    detail -> {
                        return Double.parseDouble(detail.getAmount());
                    }
            ).sum();
            details = details.stream().map(detail->{
                detail.setTransaction_id(transaction_id);
                return detail;
            }).toList();

            transactionDetailsRepo.saveAll(details);
            //-----perform credit to target accounts--------
            details.forEach(detail -> {
                accountRepo.findById(detail.getTarget_account()).map(target_account -> {
                    double credit = Double.parseDouble(
                            target_account.getAvailable_balance()
                    ) + Double.parseDouble(
                            detail.getAmount()
                    );
                    double held_balance = Double.parseDouble(target_account.getHeld_balance()) + credit;
                    target_account.setHeld_balance(
                            Double.toString(held_balance)
                    );
                    return accountRepo.save(target_account);
                });
            });
            // --------------------------------------------
            // -------perform debit to source account-------
            double held_balance = Double.parseDouble(source_account.getHeld_balance()) - total_debit;
            source_account.setHeld_balance(
                    Double.toString(held_balance)
            );
            accountRepo.save(source_account);
            // --------------------------------------------
            transaction.setDetails(details);
            return BasicResponse
                    .<Transaction>builder()
                    .data(transaction)
                    .build();

        } catch (Exception e) {
            return BasicResponse
                    .<Transaction>builder()
                    .error(e.getMessage())
                    .build();

        }
    }
}
