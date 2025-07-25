package com.softmakers.config.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softmakers.error.ErrorResponse;
import com.softmakers.error.exception.BusinessException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.OutputStream;

@Component
public class CustomExceptionHandleFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (BusinessException e) {
            final ErrorResponse errorCode = ErrorResponse.of(e.getErrorCode(), e.getErrors());
            response.setStatus(errorCode.getStatus());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            try (OutputStream os = response.getOutputStream()) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.writeValue(os, errorCode);
                os.flush();
            }
        }
    }
}
