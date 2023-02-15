package com.example.RCCC03.customer.model;

import com.example.RCCC03.auth.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Entity(name = "customers")
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
    private Date birthdate;
    private String email;
    private String phone;
    private LocalDateTime created_at;
    private boolean status;

    /*
    @OneToMany
    private List<User> users;
    */

}
