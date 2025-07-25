package com.softmakers.manager_domain.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JWTDto {

    private String type;
    private String accessToken;
    private String refreshToken;
}
