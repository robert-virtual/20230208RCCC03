package com.example.RCCC03.service;

import com.example.RCCC03.audit.AuditLogService;
import com.example.RCCC03.service.model.Service;
import com.example.RCCC03.service.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/service")
@RequiredArgsConstructor
public class ServiceController {
   private final ServiceRepository serviceRepo;
    private final AuditLogService auditLogService;
    @GetMapping("/all")
    public Iterable<Service> all(){
        return serviceRepo.findAll();
    }

    @PostMapping("/create")
    public Service create(@RequestBody Service body){
        Service service = serviceRepo.save(body);
        auditLogService.audit("create service",service);
        return service;
    }

    @PostMapping("/{id}/update")
    public Service update(@RequestBody Service body,@PathVariable long id){
        Service service = serviceRepo.findById(id).map(found->{
            found.setService(body.getService());
            return serviceRepo.save(found);
        }).orElseThrow();
        auditLogService.audit("update service",service);
        return service;
    }
}
