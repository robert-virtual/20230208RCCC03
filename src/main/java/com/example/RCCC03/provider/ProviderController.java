package com.example.RCCC03.provider;

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
    @GetMapping("/all")
    public ResponseEntity<Iterable<Provider>> all(){
        return ResponseEntity.ok(providerService.getAll());
    }
    @PostMapping ("/create")
    public ResponseEntity<Provider> create(@RequestBody Provider body) throws Exception {
        return ResponseEntity.ok(providerService.createProvider(body));
    }
    @PutMapping ("/update/{id}")
    public ResponseEntity<Optional<Provider>> create(@RequestBody Provider body, @PathVariable long id){
        return ResponseEntity.ok(providerService.update(body,id));
    }

}
