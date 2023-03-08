package com.example.RCCC03.service;

import com.example.RCCC03.audit.AuditLogService;
import com.example.RCCC03.config.BasicResponse;
import com.example.RCCC03.service.model.Service;
import com.example.RCCC03.service.repository.ServiceRepository;
import com.example.RCCC03.service.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/service")
@RequiredArgsConstructor
public class ServiceController {
    private final ServiceService serviceService;
    @GetMapping("/all")
    public ResponseEntity<BasicResponse<List<Service>>> all(){
        return ResponseEntity.ok(serviceService.all());
    }

    @PostMapping("/create")
    public ResponseEntity<BasicResponse<Service>> create(@RequestBody Service body){
        return ResponseEntity.ok(serviceService.create(body));
    }

    @PostMapping("/{id}/update")
    public ResponseEntity<BasicResponse<Service>> update(@RequestBody Service body, @PathVariable long id){
        return ResponseEntity.ok(serviceService.update(id,body));
    }
}
