package com.softmakers.manager_domain.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResetPasswordCode {

    private String username;
    private String code;
}
