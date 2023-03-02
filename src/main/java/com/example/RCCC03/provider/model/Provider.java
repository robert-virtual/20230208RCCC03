package com.example.RCCC03.provider.model;

import com.example.RCCC03.service.model.Service;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;

    @Column(name = "customer_id")
    private long customerId;


    @ManyToMany
    @JoinTable(
            name = "service_provider",
            joinColumns = @JoinColumn(name = "provider_id",referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "service_id",referencedColumnName = "id")
    )
    private List<Service> services = new ArrayList<>();
    public void addService(Service service){
       services.add(service);
    }
}
