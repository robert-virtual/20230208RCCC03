package com.example.RCCC03.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/provider")
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderRepository providerRepo;
    @GetMapping("/all")
    public Iterable<Provider> all(){
        return providerRepo.findAll();
    }
}
