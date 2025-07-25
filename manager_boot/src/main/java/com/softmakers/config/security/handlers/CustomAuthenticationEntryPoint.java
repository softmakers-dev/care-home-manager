package com.softmakers.config.security.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softmakers.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

import static com.softmakers.error.ErrorCode.AUTHENTICATION_FAIL;
import static com.softmakers.error.ErrorCode.JWT_INVALID;

//@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(JWT_INVALID.getStatus());
//        response.setStatus(AUTHENTICATION_FAIL.getStatus()); // 401 status
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        try (OutputStream os = response.getOutputStream()) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(os, ErrorResponse.of(JWT_INVALID));
//            objectMapper.writeValue(os, ErrorResponse.of(AUTHENTICATION_FAIL));
            os.flush();
        }
    }
}
