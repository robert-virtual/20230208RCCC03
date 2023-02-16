package com.example.RCCC03.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/provider")
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderRepository providerRepo;
    @GetMapping("/all")
    public Iterable<Provider> all(){
        return providerRepo.findAll();
    }
    @PostMapping ("/create")
    public Provider create(@RequestBody Provider body){
        return providerRepo.save(body);
    }
    @PutMapping ("/update/{id}")
    public Optional<Provider> create(@RequestBody Provider body, @PathVariable long id){
        return providerRepo.findById(id).map(provider ->{
            provider.setName(body.getName());
            return provider;
        });
    }

}
