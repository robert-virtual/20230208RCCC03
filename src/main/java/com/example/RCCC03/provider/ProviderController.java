package com.example.RCCC03.provider;

import com.example.RCCC03.account.controller.DataCountResponse;
import com.example.RCCC03.provider.model.Provider;
import com.example.RCCC03.provider.model.Service;
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
    @GetMapping("/{id}/services")
    public ResponseEntity<DataCountResponse<Service>> services(@PathVariable long id){
        return ResponseEntity.ok(providerService.getAllServices(id));
    }
    @GetMapping("/all")
    public ResponseEntity<DataCountResponse<Provider>> all(){
        return ResponseEntity.ok(providerService.getAll());
    }
    @PostMapping ("/create")
    public ResponseEntity<Provider> create(@RequestBody Provider body) throws Exception {
        return ResponseEntity.ok(providerService.createProvider(body));
    }
    @PutMapping ("/{id}/update")
    public ResponseEntity<Optional<Provider>> create(@RequestBody Provider body, @PathVariable long id){
        return ResponseEntity.ok(providerService.update(body,id));
    }

}
