package com.example.RCCC03.config;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "audit_log")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String action;
    @Column(columnDefinition = "text")
    private String data;
    private LocalDateTime date;
    private long user_id;
}
