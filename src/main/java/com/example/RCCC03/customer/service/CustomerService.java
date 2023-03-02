package com.example.RCCC03.customer.service;

import com.example.RCCC03.account.model.Account;
import com.example.RCCC03.account.repository.AccountRepository;
import com.example.RCCC03.auth.model.User;
import com.example.RCCC03.auth.repository.UserRepository;
import com.example.RCCC03.config.AuditLogService;
import com.example.RCCC03.config.BasicResponse;
import com.example.RCCC03.customer.model.Customer;
import com.example.RCCC03.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final AuditLogService auditLogService;
    private final CustomerRepository customerRepo;
    private final AccountRepository accountRepo;
    private final UserRepository userRepo;
   public ResponseEntity<BasicResponse<List<Account>>> getCustomerAccounts(){

        try {
            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepo.findByEmail(userEmail).orElseThrow();
            List<Account> accounts = accountRepo.findAllByCustomerId(user.getCustomerId());
            return ResponseEntity.ok(
                    BasicResponse.<List<Account>>builder()
                            .data_count(accounts.size())
                            .data(accounts)
                            .build()
            );
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.ok(
                    BasicResponse.<List<Account>>builder()
                            .error(e.getMessage())
                            .build()
            );
        }
    }

    public ResponseEntity<BasicResponse<List<Account>>> getAccounts(long customer_id) {

        Customer customer = customerRepo.findById(customer_id).orElseThrow();
        List<Account> accounts = customer.getAccounts();
        return ResponseEntity.ok(
                BasicResponse.<List<Account>>builder()
                        .data_count(accounts.size())
                        .data(accounts)
                        .build()
        );
    }

    public ResponseEntity<Customer> update(Customer body) {
        User user = userRepo.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow();
        long customer_id = user.getCustomerId();

        Customer customer = customerRepo.findById(customer_id).map(customer_ -> {
            if (body.getName() != null) customer_.setName(body.getName());
            if (body.getEmail() != null) customer_.setEmail(body.getEmail());
            if (body.getLastname() != null) customer_.setLastname(body.getLastname());
            if (body.getPhone() != null) customer_.setPhone(body.getPhone());
            if (body.getAddress_1() != null) customer_.setAddress_1(body.getAddress_1());
            if (body.getAddress_2() != null) customer_.setAddress_2(body.getAddress_2());
            return customerRepo.save(customer_);
        }).orElseThrow();
       auditLogService.audit("update customer",customer,user);
        return ResponseEntity.ok(
               customer
        );
    }

    public ResponseEntity<BasicResponse<Object>> disable() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByEmail(userEmail).orElseThrow();
        Customer customer = customerRepo.findById(user.getCustomerId()).orElseThrow();
        customer.getUsers().forEach(x -> {
            x.setStatus(false);
            userRepo.save(x);
        });

        customer.setStatus("inactive");
        customerRepo.save(customer);
        auditLogService.audit("disable customer",customer,user);
        return ResponseEntity.ok(
                BasicResponse
                        .builder()
                        .message("user disabled successfully")
                        .build()
        );
    }


    public BasicResponse<List<Customer>> getEmployees() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByEmail(userEmail).orElseThrow();
        List<Customer> employees = customerRepo.findById(
                user.getCustomerId()
        ).orElseThrow().getEmployees();
        return BasicResponse
                .<List<Customer>>builder()
                .data_count(employees.size())
                .data(
                        employees
                )
                .data_type("Customer[]")
                .build();
    }

    public ResponseEntity<BasicResponse<String>> addEmployee(Map<String, String> body) {
        String employee_dni = body.get("employee_dni");
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByEmail(userEmail).orElseThrow();
        Customer employee = customerRepo.findByDni(employee_dni);
        customerRepo.findById(user.getCustomerId()).map(company -> {
            company.addEmployee(employee);
            return customerRepo.save(company);
        });
        return ResponseEntity.ok(
                BasicResponse
                        .<String>builder()
                        .message(employee.getName() + " successfully added to the company")
                        .build()
        );
    }

    public ResponseEntity<Customer> create(Customer body) {
       Customer customer = customerRepo.save(body);
        auditLogService.audit("disable customer",customer);
        return ResponseEntity.ok(customer);
    }

    public ResponseEntity<Customer> getOne(long id) {
        return ResponseEntity.ok(customerRepo.findById(id).orElseThrow());
    }

    public BasicResponse<List<Customer>> getAll(boolean company) {
        List<Customer> customers = customerRepo.findAllByCompany(company);
        return BasicResponse.<List<Customer>>builder()
                .data(customers)
                .data_count(customers.size())
                .build();
    }

    public Customer me() {
        User user = userRepo.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow();
        return customerRepo.findById(user.getCustomerId()).orElseThrow();
    }


}
