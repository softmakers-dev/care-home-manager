package com.softmakers.manager_domain.entity.dto;

import lombok.Data;

@Data
public class LoginRequest {

    private String email;
    private String password;
    private String username;
    private String name;
}
