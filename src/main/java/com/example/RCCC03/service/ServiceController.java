package com.example.RCCC03.service;

import com.example.RCCC03.service.model.Service;
import com.example.RCCC03.service.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service")
@RequiredArgsConstructor
public class ServiceController {
   private final ServiceRepository serviceRepo;
    @GetMapping("/all")
    public Iterable<Service> all(){
        return serviceRepo.findAll();
    }
}
