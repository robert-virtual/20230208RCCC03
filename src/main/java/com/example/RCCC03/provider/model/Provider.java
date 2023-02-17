package com.example.RCCC03.provider.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private long customer_id;

    @ManyToMany
    @JoinTable(
            name = "service_provider",
            joinColumns = @JoinColumn(name = "provider_id",referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "service_id",referencedColumnName = "id")
    )
    private List<Service> services;
}
