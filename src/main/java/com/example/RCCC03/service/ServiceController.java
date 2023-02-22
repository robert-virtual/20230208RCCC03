package com.example.RCCC03.service;

import com.example.RCCC03.service.model.Service;
import com.example.RCCC03.service.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/service")
@RequiredArgsConstructor
public class ServiceController {
   private final ServiceRepository serviceRepo;
    @GetMapping("/all")
    public Iterable<Service> all(){
        return serviceRepo.findAll();
    }

    @PostMapping("/create")
    public Service create(@RequestBody Service service){
        System.out.println(service.getService());
        return serviceRepo.save(service);
    }

    @PostMapping("/{id}/update")
    public Service update(@RequestBody Service service,@PathVariable long id){
        return serviceRepo.findById(id).map(found->{
           found.setService(service.getService());
            return serviceRepo.save(found);
        }).orElseThrow();
    }
}
