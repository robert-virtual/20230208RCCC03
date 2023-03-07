package com.example.RCCC03.audit;

import com.example.RCCC03.audit.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog,Long> {
}
