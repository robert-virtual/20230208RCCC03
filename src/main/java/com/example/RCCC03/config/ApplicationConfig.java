package com.example.RCCC03.config;

import com.example.RCCC03.account.model.AccountStatus;
import com.example.RCCC03.account.model.AccountType;
import com.example.RCCC03.account.repository.AccountStatusRepository;
import com.example.RCCC03.account.repository.AccountTypeRepository;
import com.example.RCCC03.auth.model.Role;
import com.example.RCCC03.auth.model.User;
import com.example.RCCC03.auth.repository.RoleRepository;
import com.example.RCCC03.auth.repository.UserRepository;
import com.example.RCCC03.customer.model.Customer;
import com.example.RCCC03.customer.repository.CustomerRepository;
import com.example.RCCC03.transaction.model.TransactionStatus;
import com.example.RCCC03.transaction.model.TransactionType;
import com.example.RCCC03.transaction.repository.TransactionStatusRepository;
import com.example.RCCC03.transaction.repository.TransactionTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private final UserRepository userRepo;
    private final CustomerRepository customerRepo;
    private final TransactionTypeRepository transactionTypeRepo;
    private final RoleRepository roleRepo;
    @Value("${app.admin.password}")
    private String adminPassword;
    private final TransactionStatusRepository transactionStatusRepo;
    private final AccountTypeRepository accountTypeRepo;
    private final AccountStatusRepository accountStatusRepo;

    @Value("${spring.mail.username}")
    private String mailUser;
    @Value("${spring.mail.password}")
    private String mailPassword;

    @EventListener
    public void seed(ContextRefreshedEvent event) {
        seedRoles();
        seedUsers();
        seedTransactionType();
        seedTransactionStatus();
        seedAccountType();
        seedAccountStatus();
    }

    private void seedAccountStatus() {
        List<AccountStatus> accountStatus = accountStatusRepo.findAll();
        List<String> seed_account_status = List.of(
                "active",
                "inactive"
        );
        seed_account_status.forEach(s -> {
            if (
                    accountStatus.stream().noneMatch(
                            status -> Objects.equals(
                                    status.getStatus(), s
                            )
                    )
            ) {
                accountStatusRepo.save(new AccountStatus(0, s));
            }
        });

    }

    private void seedAccountType() {
        List<AccountType> accountTypes = accountTypeRepo.findAll();
        List<String> seed_account_types = List.of(
                "saving",
                "checking"
        );
        seed_account_types.forEach(s -> {
            if (
                    accountTypes.stream().noneMatch(
                            type -> Objects.equals(
                                    type.getName(), s
                            )
                    )
            ) {
                accountTypeRepo.save(new AccountType(0, s));
            }
        });

    }

    private void seedTransactionStatus() {
        List<TransactionStatus> transactionStatus = transactionStatusRepo.findAll();
        List<String> seed_transaction_status = List.of(
                "pending",
                "authorized",
                "canceled"
        );
        seed_transaction_status.forEach(s -> {
            if (
                    transactionStatus.stream().noneMatch(
                            status -> Objects.equals(
                                    status.getName(), s
                            )
                    )
            ) {
                transactionStatusRepo.save(new TransactionStatus(0, s));
            }
        });

    }

    private void seedTransactionType() {
        List<TransactionType> transactionTypes = transactionTypeRepo.findAll();
        List<TransactionType> seed_transaction_types = List.of(
                new TransactionType(0, "CCA", "Credito a cuenta de ahorro", true),
                new TransactionType(0, "CCH", "Credito a cuenta de cheques", true),
                new TransactionType(0, "ACH", "Transferencia a otros bancos", true),
                new TransactionType(0, "PPA", "Pago de planillas", true),
                new TransactionType(0, "PPR", "Pago de proveedores", true)
        );
        seed_transaction_types.forEach(s -> {
            if (
                    transactionTypes.stream().noneMatch(
                            transactionType -> Objects.equals(
                                    transactionType.getName(), s.getName()
                            )
                    )
            ) {
                transactionTypeRepo.save(s);
            }
        });

    }

    private void seedRoles() {
        List<Role> roles = roleRepo.findAll();
        List<String> seed_roles = List.of("authorizer", "operator", "account_creator","user_creator");
        seed_roles.forEach(s -> {
            if (
                    roles.stream().noneMatch(role -> Objects.equals(role.getName(), s))
            ) {
                roleRepo.save(
                        Role
                                .builder()
                                .name(s)
                                .build());
            }
        });

    }

    private Customer createCustomer(){
            return customerRepo.save(
                    Customer
                            .builder()
                            .name("Admin")
                            .email("admin@admin.com")
                            .phone("98137600")
                            .dni("0703199001234")
                            .build()
            );
    }
    private void seedUsers() {
        Optional<User> user = userRepo.findByEmail("admin@admin.com");
        if (user.isEmpty()) {
            Optional<Customer> customer = customerRepo.findByEmail("admin@admin.com");
            long customer_id = customer.map(Customer::getId).orElseGet(() -> createCustomer().getId());
            userRepo.save(
                    User
                            .builder()
                            .email("admin@admin.com")
                            .password(passwordEncoder().encode(adminPassword))
                            .roles(
                                    List.of(
                                            Role.builder().id(3).build(),// account_creator
                                            Role.builder().id(4).build() // user_creator
                                    )
                            )
                            .status(true)
                            .customerId(customer_id)
                            .created_at(LocalDateTime.now())
                            .build()
            );
        }
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername(mailUser);
        mailSender.setPassword(mailPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");


        return mailSender;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepo
                .findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
