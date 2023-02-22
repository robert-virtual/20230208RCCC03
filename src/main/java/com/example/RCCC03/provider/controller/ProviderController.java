package com.example.RCCC03.provider.controller;

import com.example.RCCC03.account.controller.DataCountResponse;
import com.example.RCCC03.provider.model.Provider;
import com.example.RCCC03.service.model.Service;
import com.example.RCCC03.provider.model.ServiceProvider;
import com.example.RCCC03.provider.service.ProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/provider")
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderService providerService;
    // returns services of a specific provider
    @GetMapping("/{id}/services")
    public ResponseEntity<DataCountResponse<Service>> services(@PathVariable long id){
        return ResponseEntity.ok(providerService.getAllServices(id));
    }
    // returns all providers
    @GetMapping("/user")
    public ResponseEntity<DataCountResponse<Provider>> providersByUser(){
        return ResponseEntity.ok(providerService.providersByUser());
    }
    @GetMapping("/all")
    public ResponseEntity<DataCountResponse<Provider>> all(){
        return ResponseEntity.ok(providerService.getAll());
    }

    // creates a provider
    @PostMapping ("/create")
    public ResponseEntity<Provider> create(@RequestBody Provider body) throws Exception {
        return ResponseEntity.ok(providerService.createProvider(body));
    }
    // add a service to a provider
    // it verifies that the provider belongs to the user requesting the action
    @PostMapping ("/add/service")
    public ResponseEntity<String> createService(@RequestBody ServiceProvider body) throws Exception {
        return ResponseEntity.ok(providerService.addServiceToProvider(body));
    }
    @PutMapping ("/{id}/update")
    public ResponseEntity<Optional<Provider>> create(@RequestBody Provider body, @PathVariable long id){
        return ResponseEntity.ok(providerService.update(body,id));
    }

}
