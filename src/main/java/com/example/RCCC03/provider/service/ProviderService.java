package com.example.RCCC03.provider.service;


import com.example.RCCC03.provider.model.AllProvidersResponse;
import com.example.RCCC03.provider.model.Provider;
import com.example.RCCC03.provider.ProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProviderService {
    private final ProviderRepository providerRepo;

    public AllProvidersResponse getAll(){
        var providers = providerRepo.findAll();
       return AllProvidersResponse
               .builder()
               .providers(providers)
               .count(providers.size())
               .build();
    }

    public Optional<Provider> update(Provider body, long id){
        return providerRepo.findById(id).map(provider ->{
            provider.setName(body.getName());
            return provider;
        });
    }

    public Provider createProvider(Provider provider) throws Exception {
        // verify that the user has permission to create accounts
        var authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if (authorities.stream().noneMatch(authrity->authrity.getAuthority().matches("accounts_creator"))){
            System.out.println("User does not have permission to create users");
            throw new Exception("User does not have permission to create users");
        }
        return providerRepo.save(provider);

    }
}
