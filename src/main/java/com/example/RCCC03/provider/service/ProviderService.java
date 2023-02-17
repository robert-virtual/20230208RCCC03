package com.example.RCCC03.provider.service;


import com.example.RCCC03.account.controller.DataCountResponse;
import com.example.RCCC03.auth.model.User;
import com.example.RCCC03.auth.repository.UserRepository;
import com.example.RCCC03.customer.model.Customer;
import com.example.RCCC03.customer.repository.CustomerRepository;
import com.example.RCCC03.provider.model.Provider;
import com.example.RCCC03.provider.model.ServiceProvider;
import com.example.RCCC03.provider.repository.ProviderRepository;
import com.example.RCCC03.provider.model.Service;
import com.example.RCCC03.provider.repository.ServiceProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ProviderService {
    private final ProviderRepository providerRepo;
    private final ServiceProviderRepository serviceProviderRepository;
    private final UserRepository userRepo;
    private final CustomerRepository customerRepo;

    public DataCountResponse<Service> getAllServices(long id){
        Provider provider = providerRepo.findById(id).orElseThrow();
        var services = provider.getServices();
        return new DataCountResponse<>(services.size(),services);
    }
    public DataCountResponse<Provider> getAll(){
        var providers = providerRepo.findAll();
       return new DataCountResponse<>(providers.size(),providers);
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
        if (authorities.stream().noneMatch(authority->authority.getAuthority().matches("accounts_creator"))){
            System.out.println("User does not have permission to create users");
            throw new Exception("User does not have permission to create users");
        }
        return providerRepo.save(provider);

    }
    /*

    public Service addService(Service service) throws Exception {
        // verify that the user has permission to create accounts
        var authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if (authorities.stream().noneMatch(authrity->authrity.getAuthority().matches("accounts_creator"))){
            System.out.println("User does not have permission to create users");
            throw new Exception("User does not have permission to create users");
        }

        Service service = serviceRepo.save(service);

        return service;

    }
     */

    public String addService(ServiceProvider serviceProvider) throws Exception {
        User user = userRepo.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow();
        List<Provider> providers = customerRepo.findProviders(user.getCustomer_id());
        if (providers.stream().noneMatch(p->p.getId() == serviceProvider.getProvider_id())){
            throw new Exception("the given provider id does not belong to the user");
        }
        serviceProviderRepository.save(
                ServiceProvider
                        .builder()
                        .service_id(serviceProvider.getService_id())
                        .provider_id(serviceProvider.getProvider_id())
                        .build()
        );
        return "Service added successfully";

    }
}
