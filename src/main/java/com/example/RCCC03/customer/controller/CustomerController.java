package com.example.RCCC03.customer.controller;

import com.example.RCCC03.account.Account;
import com.example.RCCC03.account.AccountRepository;
import com.example.RCCC03.account.CustomersResponse;
import com.example.RCCC03.customer.model.Customer;
import com.example.RCCC03.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerController {
    private final CustomerRepository customerRepo ;
    private final AccountRepository accountRepo ;

    @GetMapping("/all")
    public CustomersResponse getAll(){
        return CustomersResponse.builder()
                .customers(customerRepo.findAll())
                .build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Customer>  getOne(
            @PathVariable long id
    ){
        return ResponseEntity.ok(customerRepo.findById(id).orElseThrow());
    }
    @GetMapping("/{customer_id}/accounts")
    public Iterable<Account> getAccounts(
            @PathVariable long customer_id
    ){
        var customer = customerRepo.findById(customer_id).orElseThrow();
        return customer.getAccounts();
    }

    @PostMapping("/create") ResponseEntity<Customer> create(@RequestBody Customer body){
        return ResponseEntity.ok(customerRepo.save(body));
    }
    @PostMapping("/update/{id}") ResponseEntity<Optional<Customer>> update(
            @RequestBody Customer body,
            @PathVariable long id
    ){
        return ResponseEntity.ok(
                customerRepo.findById(id).map(customer->{
                    customer.setName(body.getName());
                    customer.setEmail(body.getEmail());
                    customer.setLastname(body.getLastname());
                    customer.setPhone(body.getPhone());
                    return customerRepo.save(customer);
                })
        );
    }
    @DeleteMapping ("/delete/{id}") ResponseEntity<String> delete(
            @PathVariable long id
    ){
        customerRepo.deleteById(id);
        return ResponseEntity.ok(
                "user deleted successfully"
        );
    }
}
