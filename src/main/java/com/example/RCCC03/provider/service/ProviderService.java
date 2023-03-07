package com.example.RCCC03.provider.service;


import com.example.RCCC03.auth.model.User;
import com.example.RCCC03.auth.repository.UserRepository;
import com.example.RCCC03.audit.AuditLogService;
import com.example.RCCC03.config.BasicResponse;
import com.example.RCCC03.provider.model.Provider;
import com.example.RCCC03.provider.model.ServiceProvider;
import com.example.RCCC03.provider.repository.ProviderRepository;
import com.example.RCCC03.service.model.Service;
import com.example.RCCC03.service.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ProviderService {
    private final ProviderRepository providerRepo;
    private final AuditLogService auditLogService;
    private final UserRepository userRepo;
    private final ServiceRepository serviceRepo;

    public BasicResponse<List<Service>> getAllServices(long provider_id) {
        Provider provider = providerRepo.findById(provider_id).orElseThrow();
        var services = provider.getServices();
        return BasicResponse.<List<Service>>builder()
                .data(services)
                .data_count(services.size())
                .build();
    }

    public BasicResponse<List<Provider>> getAll() {
        var providers = providerRepo.findAll();
        return BasicResponse.<List<Provider>>builder()
                .data_count(providers.size())
                .data(providers)
                .build();
    }

    public BasicResponse<Provider> update(Provider body, long provider_id) throws Exception {
        // verify that the provider belongs to the user requesting the action
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByEmail(userEmail).orElseThrow();
        Provider provider = providerRepo.findById(provider_id).orElseThrow();
        if (provider.getCustomerId() != user.getCustomerId()) {
            throw new Exception("the provider does not belong to the user requesting the action");
        }
        provider.setName(body.getName());
        providerRepo.save(provider);
        auditLogService.audit("update provider", provider, user);
        return BasicResponse.<Provider>builder().data(provider).build();
    }

    public BasicResponse<Provider> createProvider(Provider provider) throws Exception {
        // verify that the user has permission to create providers
        var authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if (authorities.stream().noneMatch(authority -> authority.getAuthority().matches("accounts_creator"))) {
            System.out.println("User does not have permission to create providers");
            throw new Exception("User does not have permission to create providers");
        }
        User user = userRepo.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow();
        provider.setCustomerId(user.getCustomerId());
        auditLogService.audit("create provider", provider, user);
        providerRepo.save(provider);
        return BasicResponse.<Provider>builder().data(provider).build();

    }


    public BasicResponse<String> addServiceToProvider(ServiceProvider serviceProvider) throws Exception {
        User user = userRepo.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow();
        Provider provider = providerRepo.findById(serviceProvider.getProvider_id()).orElseThrow();
        if (provider.getCustomerId() != user.getCustomerId()) {
            throw new Exception("the given provider id does not belong to the user");
        }
        Service service = serviceRepo.findById(serviceProvider.getService_id()).orElseThrow();
        provider.addService(service);
        providerRepo.save(provider);
        auditLogService.audit("add service to provider", provider, user);
        return BasicResponse
                .<String>builder()
                .message("Service added successfully")
                .build();

    }

    public BasicResponse<List<Provider>> providersByUser() {
        User user = userRepo.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow();
        List<Provider> providers = providerRepo.findAllByCustomerId(user.getCustomerId());
        return BasicResponse.<List<Provider>>builder()
                .data_count(providers.size())
                .data(providers)
                .build();
    }
}
