package com.softmakers.config.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;
import java.util.stream.Collectors;

public class CustomRequestMatcher implements RequestMatcher {

    private final OrRequestMatcher matcher;

    public CustomRequestMatcher(List<String> skipPaths) {
        final List<RequestMatcher> requestMatchers = skipPaths.stream()
                .map(AntPathRequestMatcher::new)
                .collect(Collectors.toList());
        this.matcher = new OrRequestMatcher(requestMatchers);
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        return !matcher.matches(request);
    }
}
