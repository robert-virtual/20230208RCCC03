package com.example.RCCC03.customer.controller;

import com.example.RCCC03.account.controller.DataCountResponse;
import com.example.RCCC03.account.model.Account;
import com.example.RCCC03.customer.model.CompanyEmployee;
import com.example.RCCC03.customer.model.Customer;
import com.example.RCCC03.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerController {
    private final CustomerRepository customerRepo ;

    @GetMapping("/all")
    public DataCountResponse<Customer> getAll(){
        List<Customer> customers = customerRepo.findAll();
        return new DataCountResponse<>(customers.size(),customers);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Customer>  getOne(
            @PathVariable long id
    ){
        return ResponseEntity.ok(customerRepo.findById(id).orElseThrow());
    }
    @GetMapping("/{customer_id}/accounts")
    public ResponseEntity<DataCountResponse<Account>> getAccounts(
            @PathVariable long customer_id
    ){
        Customer customer = customerRepo.findById(customer_id).orElseThrow();
        List<Account> accounts = customer.getAccounts();
        return ResponseEntity.ok(new DataCountResponse<>(accounts.size(),accounts));
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
    @PostMapping("/employee") ResponseEntity<String> addEmployee(@RequestBody CompanyEmployee body){

        return ResponseEntity.ok(
                "user deleted successfully"
        );
    }
}
