package com.example.RCCC03.service.service;

import com.example.RCCC03.audit.AuditLogService;
import com.example.RCCC03.config.BasicResponse;
import com.example.RCCC03.service.model.Service;
import com.example.RCCC03.service.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceService {

    private final AuditLogService auditLogService;
    private final ServiceRepository serviceRepo;
    public BasicResponse<Service> create(com.example.RCCC03.service.model.Service body) {
        Service service = serviceRepo.save(body);
        auditLogService.audit("create service",service);
        return BasicResponse.<Service>builder().data(service).build();
    }

    public BasicResponse<List<Service>> all() {
        return BasicResponse.<List<Service>>builder().data(serviceRepo.findAll()).build();
    }

    public BasicResponse<Service> update(long id, Service body) {
        Service service = serviceRepo.findById(id).map(found->{
            found.setService(body.getService());
            return serviceRepo.save(found);
        }).orElseThrow();
        auditLogService.audit("update service",service);
        return BasicResponse.<Service>builder().data(service).build();
    }
}
