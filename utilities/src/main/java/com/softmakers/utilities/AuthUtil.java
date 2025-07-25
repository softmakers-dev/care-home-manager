package com.softmakers.utilities;

import com.softmakers.error.exception.BusinessException;
import com.softmakers.error.exception.JwtExpiredException;
import com.softmakers.error.exception.JwtInvalidException;
import com.softmakers.manager_domain.entity.dto.JWTDto;
import com.softmakers.manager_domain.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.security.Key;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AuthUtil {

    private String SECRET_KEY = "careHome";

    private final static String CLAIM_AUTHORITIES_KEY = "authorities";
    private final static String CLAIM_JWT_TYPE_KEY = "type";
    private final static String CLAIM_MEMBER_ID_KEY = "memberId";
    private final static String BEARER_TYPE_PREFIX = "Bearer ";
    private final static String BEARER_TYPE = "Bearer";
    private final static String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private final static String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final int JWT_PREFIX_LENGTH = 7;
    private final Key JWT_KEY;
    @Value("${access-token-expires}")
    private long ACCESS_TOKEN_EXPIRES;
    @Value("${refresh-token-expires}")
    private long REFRESH_TOKEN_EXPIRES;

    public AuthUtil(@Value("${jwt.key}") byte[] key) {
        this.JWT_KEY = Keys.hmacShaKeyFor(key);
    }

    public String createToken(User user, long expireTime) {

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime validity = now.plusSeconds(expireTime);

        String secret = "your-32-characters-random-string-here!";
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        String token = Jwts.builder()
                .claim("userId", user.getUser_id())
                .claim("email", user.getEmail())
                .setSubject(user.getEmail())
                .setIssuedAt(Date.from(now.toInstant()))
//                .setExpiration(Date.from(validity.toInstant())) // commented for token without time limit
                .signWith(key)
                .compact();

        return token;
    }

    public String extractJwt(String authenticationHeader) {
        if (authenticationHeader == null) {
//            throw new JwtInvalidException();
            return null;
        } else if (!authenticationHeader.startsWith(BEARER_TYPE_PREFIX)) {
//            throw new JwtInvalidException();
            return null;
        }

        return authenticationHeader.substring(JWT_PREFIX_LENGTH);
    }

    public JWTDto generateJwtDto(Authentication authentication) {
        final String authoritiesString = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        final long currentTime = (new Date()).getTime();

        final Date accessTokenExpiresIn = new Date(currentTime + ACCESS_TOKEN_EXPIRES);
        final Date refreshTokenExpiresIn = new Date(currentTime + REFRESH_TOKEN_EXPIRES);

        final String accessToken = Jwts.builder()
                .setSubject(ACCESS_TOKEN_SUBJECT)
                .claim(CLAIM_MEMBER_ID_KEY, authentication.getName())
                .claim(CLAIM_AUTHORITIES_KEY, authoritiesString)
                .claim(CLAIM_JWT_TYPE_KEY, BEARER_TYPE)
                .setExpiration(accessTokenExpiresIn)
                .signWith(JWT_KEY, SignatureAlgorithm.HS512)
                .compact();

        final String refreshToken = Jwts.builder()
                .setSubject(REFRESH_TOKEN_SUBJECT)
                .claim(CLAIM_MEMBER_ID_KEY, authentication.getName())
                .claim(CLAIM_AUTHORITIES_KEY, authoritiesString)
                .setExpiration(refreshTokenExpiresIn)
                .signWith(JWT_KEY, SignatureAlgorithm.HS512)
                .compact();

        return new JWTDto(BEARER_TYPE, accessToken, refreshToken);
    }

    public JWTDto generateJWT(User user ) {

        final String authoritiesString = user.getUserName();
        long currentTime = (new Date()).getTime();

        final Date accessTokenExpiresIn = new Date(currentTime + ACCESS_TOKEN_EXPIRES);
        final Date refreshTokenExpiresIn = new Date(currentTime + REFRESH_TOKEN_EXPIRES);

        final String accessToken = Jwts.builder()
                .setSubject(ACCESS_TOKEN_SUBJECT)
                .claim(CLAIM_MEMBER_ID_KEY, user.getUser_id().toString())
                .claim(CLAIM_AUTHORITIES_KEY, authoritiesString)
                .claim(CLAIM_JWT_TYPE_KEY, BEARER_TYPE)
                .setExpiration(accessTokenExpiresIn)
                .signWith(JWT_KEY, SignatureAlgorithm.HS512)
                .compact();

        final String refreshToken = Jwts.builder()
                .setSubject(REFRESH_TOKEN_SUBJECT)
                .claim(CLAIM_MEMBER_ID_KEY, user.getUser_id().toString())
                .claim(CLAIM_AUTHORITIES_KEY, authoritiesString)
                .setExpiration(refreshTokenExpiresIn)
                .signWith(JWT_KEY, SignatureAlgorithm.HS512)
                .compact();

        return new JWTDto(BEARER_TYPE, accessToken, refreshToken);
    }

    public AuthenticationUserInfo getAuthenticationUser(String token) throws BusinessException {

        if ( token == null ) return null;
        Claims claims = parseClaims(token);
        final List<SimpleGrantedAuthority> authorities = Arrays.stream(
                        claims.get(CLAIM_AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        final org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User((String)claims.get(CLAIM_MEMBER_ID_KEY), "", authorities);

        return new AuthenticationUserInfo(principal, authorities);
    }

    private Claims parseClaims(String token) throws BusinessException {
        try {
            return Jwts.parserBuilder().setSigningKey(JWT_KEY).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw new JwtExpiredException();
        } catch (Exception e) {
            throw new JwtInvalidException();
        }
    }

    public class AuthenticationUserInfo {
        private final org.springframework.security.core.userdetails.User principal;
        private final List<SimpleGrantedAuthority> authorities;

        public AuthenticationUserInfo(org.springframework.security.core.userdetails.User principal, List<SimpleGrantedAuthority> authorities) {
            this.principal = principal;
            this.authorities = authorities;
        }

        public org.springframework.security.core.userdetails.User getPrincipal() {
            return principal;
        }

        public List<SimpleGrantedAuthority> getAuthorities() {
            return authorities;
        }
    }

    public Long getLoginUserId() {
        try {
            log.info("SecurityContextHolder.getContext().getAuthentication().getName()"+SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
            Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
            return userId;
        } catch ( Exception e ) {
            throw new RuntimeException();
        }
    }
}
