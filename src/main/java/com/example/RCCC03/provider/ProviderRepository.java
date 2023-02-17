package com.example.RCCC03.provider;

import com.example.RCCC03.provider.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderRepository extends JpaRepository<Provider,Long> {
}
