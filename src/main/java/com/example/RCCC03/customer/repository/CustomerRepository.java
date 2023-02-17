package com.example.RCCC03.customer.repository;

import com.example.RCCC03.customer.model.Customer;
import com.example.RCCC03.provider.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
    @Query(value = "select * from provider where customer_id = ?1",nativeQuery = true)
    List<Provider> findProviders(long id);
}
