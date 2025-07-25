package com.softmakers.config.security.filters;

import com.softmakers.config.security.token.JwtAuthenticationToken;
import com.softmakers.utilities.AuthUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
//public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
//
//    public static final String AUTHORIZATION_HEADER = "Authorization";
//    private final AuthUtil authUtil;
//
//    public JwtAuthenticationFilter(RequestMatcher requestMatcher, AuthUtil authUtil) {
//        super(requestMatcher);
//        this.authUtil = authUtil;
//    }
//
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
//            AuthenticationException {
//        final String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
//        final String jwt = authUtil.extractJwt(authorizationHeader);
//        final JwtAuthenticationToken authentication = JwtAuthenticationToken.of(jwt);
//
//        return super.getAuthenticationManager().authenticate(authentication);
//    }
//
//    @Override
//    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
//                                            Authentication authResult) throws IOException, ServletException {
//        SecurityContextHolder.getContext().setAuthentication(authResult);
//        chain.doFilter(request, response);
//    }
//}

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

//    public static final String AUTHORIZATION_HEADER = "Bearer "; // Authorization
    public static final String AUTHORIZATION_HEADER = "Authorization"; // Authorization
    private final AuthUtil authUtil;
    private final AuthenticationManager authenticationManager;
    private final List<String> skipUrls;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if ( request.getMethod().equals("OPTIONS") ) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
            final String jwt = authUtil.extractJwt(authorizationHeader);

            if( jwt == null ) {
                throw new UsernameNotFoundException("jwt Authentication exception occurs!");
            }

            final JwtAuthenticationToken authentication = JwtAuthenticationToken.of(jwt);
            Authentication authResult = authenticationManager.authenticate(authentication);
            SecurityContextHolder.getContext().setAuthentication(authResult);
        } catch (UsernameNotFoundException exception) {
            log.info("JwtAuthentication Authentication Exception Occurs! - {}");
        } catch (BadCredentialsException err) {
            log.info("Bad Credential error Occurs!");
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return skipUrls.stream().anyMatch(path::startsWith);
    }
}
