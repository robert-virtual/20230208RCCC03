package com.example.RCCC03.customer.controller;

import com.example.RCCC03.account.model.Account;
import com.example.RCCC03.config.BasicResponse;
import com.example.RCCC03.customer.model.Customer;
import com.example.RCCC03.customer.model.ProviderType;
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
    public BasicResponse<Customer> me() {
        return customerService.me();
    }

    @GetMapping("/all")
    public BasicResponse<List<Customer>> getAll(@RequestParam(name = "company", defaultValue = "false") boolean company) {
        return customerService.getAll(company);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BasicResponse<Customer>> getOne(
            @PathVariable long id
    ) {
        return ResponseEntity.ok(customerService.getOne(id));
    }

    @GetMapping("/account")
    public ResponseEntity<BasicResponse<List<Account>>> getCustomerAccounts(
    ) {
        return ResponseEntity.ok(customerService.getCustomerAccounts());
    }

    @GetMapping("/{customer_id}/account")
    public ResponseEntity<BasicResponse<List<Account>>> getAccounts(
            @PathVariable long customer_id
    ) {
        return ResponseEntity.ok(customerService.getAccounts(customer_id));
    }

    @PostMapping("/create")
    ResponseEntity<BasicResponse<Customer>> create(@RequestBody Customer body) {
        return ResponseEntity.ok(customerService.create(body));
    }

    @PutMapping("/update")
    ResponseEntity<BasicResponse<Customer>> update(
            @RequestBody Customer body
    ) {
        return ResponseEntity.ok(customerService.update(body));
    }

    @DeleteMapping("/disable")
    ResponseEntity<BasicResponse<Object>> disable(
    ) {
        return ResponseEntity.ok(customerService.disable());
    }


    @GetMapping("/employee")
    public BasicResponse<List<Customer>> getEmployees() {
        return customerService.getEmployees();
    }
    @GetMapping("/types")
    public BasicResponse<List<ProviderType>> getCustomerTypes() {
        return customerService.getCustomerTypes();
    }

    @PostMapping("/employee")
    ResponseEntity<BasicResponse<String>> addEmployee(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(customerService.addEmployee(body));
    }
}
