package com.example.RCCC03.audit;

import com.example.RCCC03.auth.model.User;
import com.example.RCCC03.auth.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogService {
    private final AuditLogRepository auditLogRepo;
    private final UserRepository userRepo;

    public void audit(
            String action,
            Object data,
            User user
    ) {

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            auditLogRepo.save(
                    AuditLog
                            .builder()
                            .user_id(user.getId())
                            .action(action)
                            .data(objectMapper.writeValueAsString(data))
                            .date(LocalDateTime.now())
                            .build()
            );
        } catch (JsonProcessingException e) {
            auditLogRepo.save(
                    AuditLog
                            .builder()
                            .user_id(user.getId())
                            .action(action)
                            .data(data.toString())
                            .date(LocalDateTime.now())
                            .build()
            );
        }
    }

    public void audit(
            String action,
            Object data
    ) {

        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepo.findByEmail(email).orElseThrow();
            audit(action, data, user);
        } catch (Exception e) {
            User user = userRepo.findByEmail("admin@admin.com").orElseThrow();
            audit(action, data, user);

        }
    }
}
