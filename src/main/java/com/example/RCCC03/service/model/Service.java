package com.example.RCCC03.service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String service;
    private boolean status = true;

}
