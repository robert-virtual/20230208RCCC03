package com.example.RCCC03.provider.controller;

import com.example.RCCC03.config.BasicResponse;
import com.example.RCCC03.provider.model.Provider;
import com.example.RCCC03.service.model.Service;
import com.example.RCCC03.provider.model.ServiceProvider;
import com.example.RCCC03.provider.service.ProviderService;
import jakarta.persistence.Basic;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/provider")
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderService providerService;
    // returns services of a specific provider
    @GetMapping("/{provider_id}/services")
    public ResponseEntity<BasicResponse<List<Service>>> services(
            @PathVariable long provider_id
    ){
        return ResponseEntity.ok(
                providerService.getAllServices(provider_id)
        );
    }
    // returns all providers
    @GetMapping("/user")
    public ResponseEntity<BasicResponse<List<Provider>>> providersByUser(){
        return ResponseEntity.ok(
                providerService.providersByUser()
        );
    }
    @GetMapping("/all")
    public ResponseEntity<BasicResponse<List<Provider>>> all(){
        return ResponseEntity.ok(
                providerService.getAll()
        );
    }

    // creates a provider
    @PostMapping ("/create")
    public ResponseEntity<BasicResponse<Provider>> create(
            @RequestBody Provider body
    ) throws Exception {
        return ResponseEntity.ok(
                providerService.createProvider(body)
        );
    }
    // add a service to a provider
    // it verifies that the provider belongs to the user requesting the action
    @PostMapping ("/add/service")
    public ResponseEntity<BasicResponse> createService(
            @RequestBody ServiceProvider body
    ) throws Exception {
        return ResponseEntity.ok(
                providerService.addServiceToProvider(body)
        );
    }
    @PutMapping ("/{provider_id}/update")
    public ResponseEntity<BasicResponse<Provider>> create(
            @RequestBody Provider body,
            @PathVariable long provider_id
    ) throws Exception {
        return ResponseEntity.ok(
                providerService.update(body, provider_id)
        );
    }

}
