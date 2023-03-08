package com.example.RCCC03.transaction.service;

import com.example.RCCC03.account.model.Account;
import com.example.RCCC03.account.repository.AccountRepository;
import com.example.RCCC03.auth.model.User;
import com.example.RCCC03.auth.repository.UserRepository;
import com.example.RCCC03.audit.AuditLogService;
import com.example.RCCC03.config.BasicResponse;
import com.example.RCCC03.customer.model.Customer;
import com.example.RCCC03.customer.repository.CustomerRepository;
import com.example.RCCC03.transaction.model.Transaction;
import com.example.RCCC03.transaction.model.TransactionDetail;
import com.example.RCCC03.transaction.model.TransactionStatus;
import com.example.RCCC03.transaction.model.TransactionType;
import com.example.RCCC03.transaction.repository.TransactionDetailRepository;
import com.example.RCCC03.transaction.repository.TransactionRepository;
import jakarta.transaction.Transactional;
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
    private final AuditLogService auditLogService;
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
        ) {
            auditLogService.audit("the user does not have the required role to perform the action", transaction, user);
            return BasicResponse.<Transaction>builder()
                    .error("the user does not have the required role to perform the action")
                    .build();
        }
        if (transaction.getStatus().getId() == 2) {
            auditLogService.audit("Transaction already authorized", transaction, user);
            return BasicResponse
                    .<Transaction>builder()
                    .error("Transaction already authorized")
                    .build();
        }

        transaction.setStatus(
                TransactionStatus
                        .builder()
                        .id(2)
                        .build()
        );
        transaction.setAuthorized_at(LocalDateTime.now());
        transaction.setAuthorizer(user);
        transactionRepo.save(transaction);
        auditLogService.audit("transaction authorized", transaction, user);
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
                accountRepo.save(source_account);
                auditLogService.audit("account updated", source_account, user);
                return source_account;
            });
        });
        Account target_account = accountRepo.findById(
                transaction
                        .getAccount()
                        .getAccount_number()
        ).orElseThrow();
        // --------------------------------------------
        // -------perform debit to source account-------
        double available_balance = Double.parseDouble(
                target_account.getAvailable_balance()
        ) + total_credit;
        target_account.setAvailable_balance(
                Double.toString(available_balance)
        );
        accountRepo.save(target_account);
        auditLogService.audit("account updated", target_account, user);
        // --------------------------------------------

        return BasicResponse
                .<Transaction>builder()
                .data(transaction)
                .data_type("transaction")
                .build();
    }

    @Transactional
    public BasicResponse<Transaction> debitEmployees(Transaction transaction) {
        try {
            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepo.findByEmail(userEmail).orElseThrow();
            Customer company = customerRepo.findById(user.getCustomerId()).orElseThrow();
            Account target_account = accountRepo.findById(transaction.getAccount().getAccount_number()).orElseThrow();
            if (
                    target_account.getCustomerId() != user.getCustomerId()
            ) {
                auditLogService.audit("the account does not belong to the user requesting the action", target_account, user);
                return BasicResponse
                        .<Transaction>builder()
                        .error("the account does not belong to the user requesting the action")
                        .build();
            }

            transaction.setOperated_at(LocalDateTime.now());
            transaction.setOperator(user);
            transaction.setStatus(
                    TransactionStatus
                            .builder()
                            .id(1)
                            .build()
            );
            List<TransactionDetail> details = new ArrayList<>();
            List<Customer> employees = company.getEmployees();
            transaction.setDetails(new ArrayList<>());
            transaction.setTransaction_type(
                    TransactionType
                            .builder()
                            .id(1)
                            .build()
            );
            // save transaction
            long transaction_id = transactionRepo.save(transaction).getId();
            auditLogService.audit("transaction created", transaction, user);
            employees.forEach(
                    employee -> {
                        details.add(
                                TransactionDetail
                                        .builder()
                                        .transaction_id(transaction_id)
                                        .amount(transaction.getAmount())
                                        .account_holder(employee.getName())
                                        .targetAccount(
                                                employee.getAccounts()
                                                        .stream()
                                                        .filter(
                                                                account -> Double.parseDouble(
                                                                        account.getAvailable_balance()
                                                                ) >= Double.parseDouble(
                                                                        transaction.getAmount()
                                                                )
                                                        )
                                                        .findFirst()
                                                        .orElseThrow()
                                                        .getAccount_number()
                                        )
                                        .build()
                        );

                    }
            );

            transactionDetailsRepo.saveAll(details);
            auditLogService.audit("transaction details created", details, user);
            transaction.setDetails(details);
            return BasicResponse
                    .<Transaction>builder()
                    .data(transaction)
                    .data_type("transaction")
                    .build();

        } catch (Exception e) {
            auditLogService.audit("error creating transaction", null);
            return BasicResponse
                    .<Transaction>builder()
                    .error(e.getMessage())
                    .build();

        }
    }

    @Transactional
    public BasicResponse<Transaction> create(Transaction transaction) {
        try {
            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepo.findByEmail(userEmail).orElseThrow();
            Account source_account = accountRepo.findById(transaction.getAccount().getAccount_number()).orElseThrow();
            if (
                    source_account.getCustomerId() != user.getCustomerId()
            ) {
                auditLogService.audit("the account does not belong to the user requesting the action", transaction, user);
                return BasicResponse
                        .<Transaction>builder()
                        .error("the account does not belong to the user requesting the action")
                        .build();
            }
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
            ) {
                auditLogService.audit("the account does not have the required balance to perform the transaction", transaction, user);
                return BasicResponse
                        .<Transaction>builder()
                        .error("the account does not have the required balance to perform the transaction")
                        .build();
            }

            transaction.setOperated_at(LocalDateTime.now());
            transaction.setOperator(user);
            transaction.setStatus(
                    TransactionStatus
                            .builder()
                            .id(1)
                            .build()
            );
            transaction.setDetails(new ArrayList<>());
            long transaction_id = transactionRepo.save(transaction).getId();
            auditLogService.audit("transaction saved", transaction, user);
            details = details.stream().peek(detail -> {
                // if the transaction is not ACH query account information
                System.out.println(transaction.getTransaction_type().getId());
                if (transaction.getTransaction_type().getId() != 3) {
                    Account target_account = accountRepo.findById(detail.getTargetAccount()).orElseThrow();
                    Customer target_customer = customerRepo.findById(
                            target_account.getCustomerId()
                    ).orElseThrow();
                    detail.setAccount_holder(target_customer.getName());
                }
                detail.setTransaction_id(transaction_id);
            }).toList();

            transactionDetailsRepo.saveAll(details);
            auditLogService.audit("transaction details saved", transaction, user);
            // just to return the details in the json response
            transaction.setDetails(details);
            return BasicResponse
                    .<Transaction>builder()
                    .data(transaction)
                    .data_type("transaction")
                    .build();

        } catch (Exception e) {
            auditLogService.audit("error creating transaction", transaction);
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
        ) {
            auditLogService.audit("the user does not have the required role to perform the action", user, user);
            return BasicResponse.<Transaction>builder()
                    .error("the user does not have the required role to perform the action")
                    .build();
        }
        Transaction transaction = transactionRepo.findById(body.getTransaction_id()).orElseThrow();
        if (transaction.getStatus().getId() == 2) {
            auditLogService.audit("Transaction already authorized", transaction, user);
            return BasicResponse
                    .<Transaction>builder()
                    .error("Transaction already authorized")
                    .build();
        }
        Account source_account = accountRepo.findById(transaction.getAccount().getAccount_number()).orElseThrow();
        if (source_account.getCustomerId() != user.getCustomerId()) {
            auditLogService.audit("transaction does not belongs to user requesting", transaction, user);
            return BasicResponse
                    .<Transaction>builder()
                    .error("The transaction does not belong to the user requesting")
                    .build();
        }

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
        ) {
            auditLogService.audit("the account does not have the required balance to perform the transaction", transaction, user);
            return BasicResponse
                    .<Transaction>builder()
                    .error("the account does not have the required balance to perform the transaction")
                    .build();
        }


        // -------perform debit to source account-------
        double available_balance = Double.parseDouble(
                source_account.getAvailable_balance()
        ) - total_debit;
        source_account.setAvailable_balance(
                Double.toString(available_balance)
        );
        accountRepo.save(source_account);
        auditLogService.audit("account updated", source_account, user);
        // --------------------------------------------
        if (transaction.getTransaction_type().getId() != 3) {
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
                    accountRepo.save(target_account);
                    auditLogService.audit("account updated", target_account, user);
                    return target_account;
                });
            });
        }
        // --------------------------------------------
        // update transaction
        transaction.setStatus(
                TransactionStatus
                        .builder()
                        .id(2)
                        .build()
        );
        transaction.setAuthorized_at(LocalDateTime.now());
        transaction.setAuthorizer(user);
        transactionRepo.save(transaction);
        auditLogService.audit("transaction authorized ", transaction, user);
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
