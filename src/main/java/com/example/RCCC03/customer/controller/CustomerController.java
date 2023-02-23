package com.example.RCCC03.customer.controller;

import com.example.RCCC03.account.model.Account;
import com.example.RCCC03.auth.model.User;
import com.example.RCCC03.auth.repository.UserRepository;
import com.example.RCCC03.config.BasicResponse;
import com.example.RCCC03.customer.model.Customer;
import com.example.RCCC03.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerController {
    private final CustomerRepository customerRepo;
    private final UserRepository userRepo;

    @GetMapping("/me")
    public Customer me() {
        User user = userRepo.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow();
        return customerRepo.findById(user.getCustomerId()).orElseThrow();
    }
    @GetMapping("/all")
    public BasicResponse<List<Customer>> getAll(@RequestParam(name = "company",defaultValue = "false") boolean company ){
        System.out.println(company);
        List<Customer> customers = customerRepo.findAllByCompany(company);
        return  BasicResponse.<List<Customer>>builder()
                .data(customers)
                .data_count(customers.size())
                .build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Customer>  getOne(
            @PathVariable long id
    ){
        return ResponseEntity.ok(customerRepo.findById(id).orElseThrow());
    }
    @GetMapping("/{customer_id}/accounts")
    public ResponseEntity<BasicResponse<List<Account>>> getAccounts(
            @PathVariable long customer_id
    ){
        Customer customer = customerRepo.findById(customer_id).orElseThrow();
        List<Account> accounts = customer.getAccounts();
        return ResponseEntity.ok(
                BasicResponse.<List<Account>>builder()
                        .data_count(accounts.size())
                        .data(accounts)
                        .build()
        );
    }

    @PostMapping("/create") ResponseEntity<Customer> create(@RequestBody Customer body){
        return ResponseEntity.ok(customerRepo.save(body));
    }
    @PutMapping("/update") ResponseEntity<Customer> update(
            @RequestBody Customer body
    ){
        User user = userRepo.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow();
        long customer_id = user.getCustomerId();
        return ResponseEntity.ok(
                customerRepo.findById(customer_id).map(customer->{
                    if(body.getName() != null) customer.setName(body.getName());
                    if(body.getEmail() != null) customer.setEmail(body.getEmail());
                    if(body.getLastname() != null) customer.setLastname(body.getLastname());
                    if(body.getPhone() != null) customer.setPhone(body.getPhone());
                    if(body.getAddress_1() != null) customer.setAddress_1(body.getAddress_1());
                    if(body.getAddress_2() != null) customer.setAddress_2(body.getAddress_2());
                    return customerRepo.save(customer);
                }).orElseThrow()
        );
    }
    @DeleteMapping ("/disable") ResponseEntity<BasicResponse<Object>> disable(
    ){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByEmail(userEmail).orElseThrow();
        Customer customer = customerRepo.findById(user.getCustomerId()).orElseThrow();
        customer.setStatus("inactive");
        customerRepo.save(customer);
        return ResponseEntity.ok(
                BasicResponse
                        .builder()
                        .message("user disabled successfully")
                        .build()
        );
    }
    @DeleteMapping ("/delete") ResponseEntity<BasicResponse<Object>> delete(
    ){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByEmail(userEmail).orElseThrow();
        userRepo.deleteAllByCustomerId(user.getCustomerId());
        customerRepo.deleteById(user.getCustomerId());
        return ResponseEntity.ok(
               BasicResponse
                       .builder()
                       .message("customer and related users deleted successfully")
                       .build()
        );
    }
    @GetMapping("/employee")
    public Iterable<Customer> getEmployees(){
        String userEmail  = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByEmail(userEmail).orElseThrow();
        return customerRepo.findById(user.getCustomerId()).orElseThrow().getEmployees();
    }
    @PostMapping("/employee") ResponseEntity<Map<String,Object>> addEmployee(@RequestBody Map<String,String> body){
        String employee_dni = body.get("employee_dni");
        String userEmail  = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByEmail(userEmail).orElseThrow();
        Customer employee = customerRepo.findByDni(employee_dni);
        customerRepo.findById(user.getCustomerId()).map(company->{
            company.addEmployee(employee);
            return customerRepo.save(company);
        });
        Map<String,Object> res = new HashMap<>();
        res.put("message",employee.getName() + " successfully added to the company");
        return ResponseEntity.ok(
               res
        );
    }
}
