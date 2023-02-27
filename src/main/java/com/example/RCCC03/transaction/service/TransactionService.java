package com.example.RCCC03.transaction.service;

import com.example.RCCC03.account.model.Account;
import com.example.RCCC03.account.repository.AccountRepository;
import com.example.RCCC03.auth.model.AuthResponse;
import com.example.RCCC03.auth.model.User;
import com.example.RCCC03.auth.repository.UserRepository;
import com.example.RCCC03.config.BasicResponse;
import com.example.RCCC03.customer.model.Customer;
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
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepo;
    private final TransactionDetailRepository transactionDetailsRepo;
    private final CustomerRepository customerRepo;
    private final UserRepository userRepo;
    private final AccountRepository accountRepo;

    public BasicResponse<Transaction> authorizeDebitToEmployees(
            TransactionDetail body
    ) {

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByEmail(userEmail).orElseThrow();
        Transaction transaction = transactionRepo.findById(body.getTransaction_id()).orElseThrow();
        List<TransactionDetail> details = transaction.getDetails();
        if (
                user.getAuthorities().stream().noneMatch(
                        authority -> Objects.equals(authority.getAuthority(), "authorizer")
                )
        ) return BasicResponse.<Transaction>builder()
                .error("the user does not have the required role to perform the action")
                .build();
        if (transaction.getStatus().getId() == 2) return BasicResponse
                .<Transaction>builder()
                .error("Transaction already authorized")
                .build();

        transaction.setStatus(
                TransactionStatus
                        .builder()
                        .id(2)
                        .build()
        );
        transaction.setAuthorized_at(LocalDateTime.now());
        transaction.setAuthorized_by(userEmail);
        transactionRepo.save(transaction);
        double total_credit = details.stream().mapToDouble(
                detail -> Double.parseDouble(detail.getAmount())
        ).sum();
        //-----perform credit to target accounts--------
        details.forEach(detail -> {
            accountRepo.findById(detail.getTargetAccount()).map(source_account -> {
                double debit = Double.parseDouble(
                        detail.getAmount()
                );
                double available_balance = Double.parseDouble(
                        source_account.getAvailable_balance()
                ) - debit;
                source_account.setAvailable_balance(
                        Double.toString(available_balance)
                );
                return accountRepo.save(source_account);
            });
        });
        Account target_account = accountRepo.findById(transaction.getAccount().getAccount_number()).orElseThrow();
        // --------------------------------------------
        // -------perform debit to source account-------
        double available_balance = Double.parseDouble(
                target_account.getAvailable_balance()
        ) + total_credit;
        target_account.setAvailable_balance(
                Double.toString(available_balance)
        );
        accountRepo.save(target_account);
        // --------------------------------------------

        return BasicResponse
                .<Transaction>builder()
                .data(transaction)
                .data_type("transaction")
                .build();
    }

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
            transaction.setOperated_by(userEmail);
            transaction.setStatus(
                    TransactionStatus
                            .builder()
                            .id(1)
                            .build()
            );
            List<TransactionDetail> details = transaction.getDetails();
            transaction.setDetails(new ArrayList<>());
            // save transaction
            long transaction_id = transactionRepo.save(transaction).getId();
            details = details.stream().peek(
                    detail -> {
                        detail.setTransaction_id(transaction_id);
                        if (
                                transaction.isAll_employees()
                        ) detail.setAmount(transaction.getAmount());
                    }
            ).toList();

            transactionDetailsRepo.saveAll(details);
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
                    Double.parseDouble(
                            source_account
                                    .getAvailable_balance()
                    ) < total_debit
            ) return BasicResponse
                    .<Transaction>builder()
                    .error("the account does not have the required balance to perform the transaction")
                    .build();

            transaction.setOperated_at(LocalDateTime.now());
            transaction.setOperated_by(userEmail);
            transaction.setStatus(
                    TransactionStatus
                            .builder()
                            .id(1)
                            .build()
            );
            transaction.setDetails(new ArrayList<>());
            long transaction_id = transactionRepo.save(transaction).getId();
            details = details.stream().peek(detail -> {
                Account target_account = accountRepo.findById(detail.getTargetAccount()).orElseThrow();

                Customer target_customer = customerRepo.findById(
                        target_account.getCustomerId()
                ).orElseThrow();
                detail.setAccount_holder(target_customer.getName());
                detail.setTransaction_id(transaction_id);
            }).toList();

            transactionDetailsRepo.saveAll(details);
            // just to return the details in the json response
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

    public BasicResponse<Transaction> authorize(TransactionDetail body) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByEmail(userEmail).orElseThrow();
        if (
                user.getAuthorities().stream().noneMatch(
                        authority -> Objects.equals(authority.getAuthority(), "authorizer")
                )
        ) return BasicResponse.<Transaction>builder()
                .error("the user does not have the required role to perform the action")
                .build();
        Transaction transaction = transactionRepo.findById(body.getTransaction_id()).orElseThrow();
        if (transaction.getStatus().getId() == 2) return BasicResponse
                .<Transaction>builder()
                .error("Transaction already authorized")
                .build();
        Account source_account = accountRepo.findById(transaction.getAccount().getAccount_number()).orElseThrow();
        if (source_account.getCustomerId() != user.getCustomerId()) return BasicResponse
                .<Transaction>builder()
                .error("This transaction does not belongs you")
                .build();

        //-----perform credit to target accounts--------
        List<TransactionDetail> details = transaction.getDetails();
        double total_debit = details.stream().mapToDouble(
                detail -> Double.parseDouble(detail.getAmount())
        ).sum();
        // validate that the account has the required balance to perform the transaction
        if (
                Double.parseDouble(
                        source_account
                                .getAvailable_balance()
                ) < total_debit
        ) return BasicResponse
                .<Transaction>builder()
                .error("the account does not have the required balance to perform the transaction")
                .build();


        // -------perform debit to source account-------
        double available_balance = Double.parseDouble(
                source_account.getAvailable_balance()
        ) - total_debit;
        System.out.printf("total_debit: %s, available_balance: %s%n", total_debit, available_balance);
        source_account.setAvailable_balance(
                Double.toString(available_balance)
        );
        accountRepo.save(source_account);
        // --------------------------------------------
        details.forEach(detail -> {
            accountRepo.findById(detail.getTargetAccount()).map(target_account -> {
                double credit = Double.parseDouble(
                        detail.getAmount()
                );
                double target_available_balance = Double.parseDouble(
                        target_account.getAvailable_balance()
                ) + credit;
                target_account.setAvailable_balance(
                        Double.toString(target_available_balance)
                );
                return accountRepo.save(target_account);
            });
        });
        // --------------------------------------------
        // update transaction
        transaction.setStatus(
                TransactionStatus
                        .builder()
                        .id(2)
                        .build()
        );
        transaction.setAuthorized_at(LocalDateTime.now());
        transaction.setAuthorized_by(userEmail);
        transactionRepo.save(transaction);
        return BasicResponse
                .<Transaction>builder()
                .data(transaction)
                .data_type("transaction")
                .build();
    }

    public BasicResponse<List<Transaction>> all(Account account) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByEmail(userEmail).orElseThrow();
        Account account_data = accountRepo.findById(account.getAccount_number()).orElseThrow();
        if (account_data.getCustomerId() != user.getCustomerId()) return BasicResponse
                .<List<Transaction>>builder()
                .error("the account does not belong to the user requesting the information")
                .build();
        List<TransactionDetail> transactionDetails = transactionDetailsRepo.findAllByTargetAccount(
                Long.toString(account.getAccount_number())
        );
        List<Long> ids = transactionDetails.stream().mapToLong(TransactionDetail::getTransaction_id).boxed().toList();
        List<Transaction> transactions = transactionRepo.findAllByAccount(account);
        if (!ids.isEmpty()) {
            transactions.addAll(transactionRepo.findAllById(ids));
        }
        return BasicResponse.<List<Transaction>>builder()
                .data(transactions)
                .data_count(transactions.size())
                .data_type("Transaction[]")
                .build();
    }
}
