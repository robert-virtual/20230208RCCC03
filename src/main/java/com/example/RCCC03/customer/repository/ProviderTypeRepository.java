package com.example.RCCC03.customer.repository;

import com.example.RCCC03.customer.model.Customer;
import com.example.RCCC03.customer.model.ProviderType;
import com.example.RCCC03.provider.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProviderTypeRepository extends JpaRepository<ProviderType,Integer> {
}
