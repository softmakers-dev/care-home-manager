package com.softmakers.config.security.providers;

import com.softmakers.config.security.token.JwtAuthenticationToken;
import com.softmakers.utilities.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final AuthUtil authUtil;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String jwt = (String)authentication.getPrincipal();
        AuthUtil.AuthenticationUserInfo authenticationUserInfo = authUtil.getAuthenticationUser(jwt);
        return JwtAuthenticationToken.of(
                authenticationUserInfo.getPrincipal(),
                jwt, authenticationUserInfo.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return JwtAuthenticationToken.class.isAssignableFrom(aClass);
    }
}
