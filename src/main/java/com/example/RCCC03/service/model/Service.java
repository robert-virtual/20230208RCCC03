package com.example.RCCC03.service.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Service {
    public Service(){
        status = true;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String service;
    private boolean status;

}
