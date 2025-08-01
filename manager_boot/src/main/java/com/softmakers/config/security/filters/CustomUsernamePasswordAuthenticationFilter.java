package com.softmakers.config.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

import com.softmakers.manager_domain.entity.dto.LoginRequest;

public class CustomUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final AntPathRequestMatcher ANT_PATH_REQUEST_MATCHER =
            new AntPathRequestMatcher("/login", "POST");
    final ObjectMapper objectMapper = new ObjectMapper();

    public CustomUsernamePasswordAuthenticationFilter() {
        super(ANT_PATH_REQUEST_MATCHER);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
            AuthenticationException, IOException, ServletException {

        if ( !request.getMethod().equals("POST") ) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {

            try {
                final String requestBody = IOUtils.toString(request.getReader());
                final LoginRequest loginRequest = objectMapper.readValue(requestBody, LoginRequest.class);
                final UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(), loginRequest.getPassword());

                return this.getAuthenticationManager().authenticate(authRequest);
            } catch ( IOException e ) {
                throw new AuthenticationServiceException("Authentication failed while converting request body.");
            }
        }
    }
}
