package com.example.RCCC03.customer.model;

import com.example.RCCC03.account.model.Account;
import com.example.RCCC03.auth.model.User;
import com.example.RCCC03.provider.model.Provider;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer {

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
    private String phone;
    private LocalDateTime created_at;

    @OneToMany
    @JoinColumn(name = "customer_id",referencedColumnName = "id")
    private List<Provider> providers;

    @OneToMany
    @JoinColumn(name = "customer_id",referencedColumnName = "id")
    private List<User> users;

}
