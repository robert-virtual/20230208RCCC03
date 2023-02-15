package com.example.RCCC03.customer.repository;

import com.example.RCCC03.customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
}
