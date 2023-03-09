package com.example.RCCC03.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String email;
    private List<Role> roles = new ArrayList<>();
    private long customer_id;
    public void addRole(Role role) {
        roles.add(role);
    }
    public void addRoles(List<Role> roles) {
        roles.addAll(roles);
    }

}
