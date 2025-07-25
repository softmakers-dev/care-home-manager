package com.softmakers.manager_domain.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class JWTResponse {

    private String type;
    private String accessToken;
}
