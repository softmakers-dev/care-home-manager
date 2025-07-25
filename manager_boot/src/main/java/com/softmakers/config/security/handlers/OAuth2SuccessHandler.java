package com.softmakers.config.security.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softmakers.manager_domain.entity.dto.JWTDto;
import com.softmakers.manager_domain.entity.dto.JWTResponse;
import com.softmakers.manager_domain.entity.dto.Location;
import com.softmakers.manager_domain.spec.RefreshTokenService;
import com.softmakers.result.ResultCode;
import com.softmakers.result.ResultResponse;
import com.softmakers.utilities.AuthUtil;
import com.softmakers.utilities.RequestExtractor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import static com.softmakers.result.ResultCode.LOGIN_SUCCESS;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final AuthUtil authUtil;
    private final RefreshTokenService refreshTokenService;
    private final int REFRESH_TOKEN_EXPIRES = 60 * 60 * 24 * 7; // 7일
    private final ResultCode DEFAULT_RESULT_CODE = LOGIN_SUCCESS;
    private Map<String, ResultCode> resultCodeMap;
    @Value("${server-domain}")
    private String SERVER_DOMAIN;
    private final String URI = "/care-home/oauth/success";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                        Authentication authentication) throws IOException, ServletException {
        this.onAuthenticationSuccess(request, response, authentication);
    }

    @Override
    public void onAuthenticationSuccess( HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication ) throws IOException, ServletException {
        final JWTDto jwtDto = authUtil.generateJwtDto(authentication);
        final Location location = new Location("Seoul", "127.04", "37.57");

        String redirectUrl = UriComponentsBuilder.fromUriString(URI)
                .queryParam("accessToken", jwtDto.getAccessToken())
                .build().toUriString();

        response.sendRedirect( redirectUrl );
    }

    protected ResultCode getResultCode(HttpServletRequest request) {
        if (resultCodeMap != null && resultCodeMap.containsKey(request.getRequestURI())) {
            return resultCodeMap.get(request.getRequestURI());
        } else {
            return DEFAULT_RESULT_CODE;
        }
    }

    protected void addCookie(HttpServletResponse response, String refreshTokenString) {

        final Cookie cookie = new Cookie("refreshToken", refreshTokenString);

        cookie.setMaxAge(REFRESH_TOKEN_EXPIRES);

        // cookie.setSecure(true); https 미지원
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setDomain(SERVER_DOMAIN);

        response.addCookie(cookie);
    }
}
