package com.example.RCCC03.provider.repository;

import com.example.RCCC03.provider.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProviderRepository extends JpaRepository<Provider,Long> {
    List<Provider> findAllByCustomerId(long customerId);
}
