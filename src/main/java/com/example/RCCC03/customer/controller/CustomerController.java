package com.example.RCCC03.customer.controller;

import com.example.RCCC03.account.model.Account;
import com.example.RCCC03.config.BasicResponse;
import com.example.RCCC03.customer.model.Customer;
import com.example.RCCC03.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping("/me")
    public Customer me() {
        return customerService.me();
    }

    @GetMapping("/all")
    public BasicResponse<List<Customer>> getAll(@RequestParam(name = "company", defaultValue = "false") boolean company) {
        return customerService.getAll(company);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getOne(
            @PathVariable long id
    ) {
        return customerService.getOne(id);
    }

    @GetMapping("/account")
    public ResponseEntity<BasicResponse<List<Account>>> getCustomerAccounts(
    ) {
        return customerService.getCustomerAccounts();
    }

    @GetMapping("/{customer_id}/account")
    public ResponseEntity<BasicResponse<List<Account>>> getAccounts(
            @PathVariable long customer_id
    ) {
        return customerService.getAccounts(customer_id);
    }

    @PostMapping("/create")
    ResponseEntity<Customer> create(@RequestBody Customer body) {
        return customerService.create(body);
    }

    @PutMapping("/update")
    ResponseEntity<Customer> update(
            @RequestBody Customer body
    ) {
        return customerService.update(body);
    }

    @DeleteMapping("/disable")
    ResponseEntity<BasicResponse<Object>> disable(
    ) {
        return customerService.disable();
    }


    @GetMapping("/employee")
    public BasicResponse<List<Customer>> getEmployees() {
        return customerService.getEmployees();
    }

    @PostMapping("/employee")
    ResponseEntity<BasicResponse<String>> addEmployee(@RequestBody Map<String, String> body) {
        return customerService.addEmployee(body);
    }
}
