package com.example.RCCC03.customer.model;

import com.example.RCCC03.account.model.Account;
import com.example.RCCC03.auth.model.User;
import com.example.RCCC03.provider.model.Provider;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@Builder
public class Customer {

    public Customer(){
       isCompany = false;
        status = "active";
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String lastname;
    @OneToMany
    @JoinColumn(name = "customer_id",referencedColumnName = "id")
    private List<Account> accounts;

    private Date birthdate;
    private String email;
    @Column(unique = true,nullable = false)
    private String dni;

    @Column(nullable = true,name = "is_company")
    private boolean isCompany;
    private String phone;
    private String address_1;
    private String address_2;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    @Column(nullable = true)
    private String status;

    @OneToMany
    @JoinColumn(name = "customer_id",referencedColumnName = "id")
    private List<Provider> providers;

    @OneToMany
    @JoinColumn(name = "customer_id",referencedColumnName = "id")
    private List<User> users;

    @ManyToMany
    @JoinTable(
            name = "company_employee",
            joinColumns = @JoinColumn(name = "company_id",referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id",referencedColumnName = "id")
    )
    private List<Customer> employees = new ArrayList<>();
    public void addEmployee(Customer employee){
       employees.add(employee);
    }

}
