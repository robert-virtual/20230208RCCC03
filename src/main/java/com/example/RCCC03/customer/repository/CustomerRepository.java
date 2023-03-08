package com.example.RCCC03.customer.repository;

import com.example.RCCC03.auth.model.User;
import com.example.RCCC03.customer.model.Customer;
import com.example.RCCC03.provider.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
    @Query(value = "select * from provider where customer_id = ?1",nativeQuery = true)
    List<Provider> findProviders(long id);
    List<Customer> findAllByIsCompany(boolean company);

    Customer findByDni(String dni);

    Optional<Customer> findByEmail(String email);
}
