package com.example.RCCC03.transaction.service;

import com.example.RCCC03.account.model.Account;
import com.example.RCCC03.account.repository.AccountRepository;
import com.example.RCCC03.auth.model.User;
import com.example.RCCC03.auth.repository.UserRepository;
import com.example.RCCC03.config.BasicResponse;
import com.example.RCCC03.customer.repository.CustomerRepository;
import com.example.RCCC03.transaction.dto.DebitEmployees;
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

    public BasicResponse<Transaction> debitEmployees(DebitEmployees transaction) {
        try {
            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepo.findByEmail(userEmail).orElseThrow();
            Account target_account = accountRepo.findById(transaction.getAccount().getAccount_number()).orElseThrow();
            if (
                    target_account.getCustomerId() != user.getCustomerId()
            ) return BasicResponse
                    .<Transaction>builder()
                    .error("the account does not belong to the user requesting the action")
                    .build();

            transaction.setOperated_at(LocalDateTime.now());
            transaction.setStatus(
                    TransactionStatus
                            .builder()
                            .id(1)
                            .build()
            );
            List<TransactionDetail> details = transaction.getDetails();
            transaction.setDetails(new ArrayList<>());
            long transaction_id = transactionRepo.save(transaction).getId();
            double total_credit = details.stream().mapToDouble(
                    detail -> Double.parseDouble(detail.getAmount())
            ).sum();
            details = details.stream().map(detail -> {
                detail.setTransaction_id(transaction_id);
                return detail;
            }).toList();

            transactionDetailsRepo.saveAll(details);
            //-----perform credit to target accounts--------
            details.forEach(detail -> {
                accountRepo.findById(detail.getTargetAccount()).map(source_account -> {
                    double debit = Double.parseDouble(
                            source_account.getAvailable_balance()
                    ) + Double.parseDouble(
                            detail.getAmount()
                    );
                    double held_balance = Double.parseDouble(source_account.getHeld_balance()) - debit;
                    source_account.setHeld_balance(
                            Double.toString(held_balance)
                    );
                    return accountRepo.save(source_account);
                });
            });
            // --------------------------------------------
            // -------perform debit to source account-------
            double held_balance = Double.parseDouble(target_account.getHeld_balance()) + total_credit;
            target_account.setHeld_balance(
                    Double.toString(held_balance)
            );
            accountRepo.save(target_account);
            // --------------------------------------------
            transaction.setDetails(details);
            return BasicResponse
                    .<Transaction>builder()
                    .data(transaction)
                    .data_type("transaction")
                    .build();

        } catch (Exception e) {
            return BasicResponse
                    .<Transaction>builder()
                    .error(e.getMessage())
                    .build();

        }
    }

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
            List<TransactionDetail> details = transaction.getDetails();
            double total_debit = details.stream().mapToDouble(
                    detail -> Double.parseDouble(detail.getAmount())
            ).sum();
            // validate that the account has the required balance to perform the transaction
            if (
                    Double.parseDouble(source_account.getAvailable_balance()) < total_debit
            ) return BasicResponse
                    .<Transaction>builder()
                    .error("the account does not have the required balance to perform the transaction")
                    .build();

            transaction.setOperated_at(LocalDateTime.now());
            transaction.setStatus(
                    TransactionStatus
                            .builder()
                            .id(1)
                            .build()
            );
            transaction.setDetails(new ArrayList<>());
            long transaction_id = transactionRepo.save(transaction).getId();
            details = details.stream().map(detail -> {
                detail.setTransaction_id(transaction_id);
                return detail;
            }).toList();

            transactionDetailsRepo.saveAll(details);
            return BasicResponse
                    .<Transaction>builder()
                    .data(transaction)
                    .data_type("transaction")
                    .build();

        } catch (Exception e) {
            return BasicResponse
                    .<Transaction>builder()
                    .error(e.getMessage())
                    .build();

        }
    }

    public BasicResponse<Transaction> authorize(TransactionDetail body) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByEmail(userEmail).orElseThrow();
        if (
                user.getAuthorities().stream().noneMatch(
                        authority -> authority.getAuthority() == "authorizer"
                )
        ) return BasicResponse.<Transaction>builder()
                .error("the user does not have the required role to perform the action")
                .build();
        Transaction transaction = transactionRepo.findById(body.getTransaction_id()).orElseThrow();
        List<TransactionDetail> details = transaction.getDetails();
        double total_debit = details.stream().mapToDouble(
                detail -> Double.parseDouble(detail.getAmount())
        ).sum();
        //-----perform credit to target accounts--------
        Account source_account = accountRepo.findById(transaction.getAccount().getAccount_number()).orElseThrow();
        details.forEach(detail -> {
            accountRepo.findById(detail.getTargetAccount()).map(target_account -> {
                double credit = Double.parseDouble(
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
        double held_balance = Double.parseDouble(source_account.getHeld_balance()) + total_debit;
        double available_balance = Double.parseDouble(source_account.getAvailable_balance()) - total_debit;
        source_account.setAvailable_balance(
                Double.toString(available_balance)
        );
        source_account.setHeld_balance(
                Double.toString(held_balance)
        );
        accountRepo.save(source_account);
        // --------------------------------------------
        transaction.setDetails(details);
        // update transaction
        transaction.setStatus(
                TransactionStatus
                        .builder()
                        .id(2)
                        .build()
        );
        transaction.setAuthorized_at(LocalDateTime.now());
        transactionRepo.save(transaction);
        return BasicResponse
                .<Transaction>builder()
                .data(transaction)
                .data_type("transaction")
                .build();
    }

    public BasicResponse<List<Transaction>> all(Account account) {
        List<TransactionDetail> transactionDetails =  transactionDetailsRepo.findAllByTargetAccount(
                Long.toString(account.getAccount_number())
        );
        List<Long> ids = transactionDetails.stream().mapToLong(TransactionDetail::getTransaction_id).boxed().toList();
        List<Transaction> transactions  =  transactionRepo.findAllByAccount(account);
        if (!ids.isEmpty()){
            transactions.addAll(transactionRepo.findAllById(ids));
        }
        return BasicResponse.<List<Transaction>>builder()
                .data(transactions)
                .data_count(transactions.size())
                .data_type("Transaction[]")
                .build();
    }
}
