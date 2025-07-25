package com.softmakers.config.security;

import com.softmakers.config.security.filters.CustomExceptionHandleFilter;
import com.softmakers.config.security.filters.CustomUsernamePasswordAuthenticationFilter;
import com.softmakers.config.security.filters.JwtAuthenticationFilter;
import com.softmakers.config.security.handlers.CustomAuthenticationFailureHandler;
import com.softmakers.config.security.handlers.CustomAuthenticationSuccessHandler;
import com.softmakers.config.security.handlers.OAuth2SuccessHandler;
import com.softmakers.config.security.providers.JwtAuthenticationProvider;
import com.softmakers.manager_service.service.CustomOAuth2UserService;
import com.softmakers.manager_service.service.CustomUserDetailsService;

import com.softmakers.result.ResultCode;
import com.softmakers.utilities.AuthUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] AUTH_WHITELIST_SWAGGER = {"/v2/api-docs", "/v3/api-docs/**",
            "/configuration/ui", "/swagger-resources",
            "/configuration/security", "/swagger-ui.html", "/webjars/**", "/swagger-ui/**"};
    private static final String[] AUTH_WHITELIST_STATIC = {"/static/css/**", "/static/js/**", "/error", "/favicon.ico"};
    private static final String[] AUTH_WHITELIST = {
            "/login", "/signup", "/login/oauth2/code/*",
            "/accounts/check", "/accounts/password/reset",
            "/accounts/email", "/accounts/email",
            "/swagger-ui.html", "/swagger/**", "/swagger-resources/**",
            "/swagger-ui/**" };

    private final AuthUtil authUtil;
    private final CustomUserDetailsService jwtUserDetailsService;
//    private final ResetPasswordCodeUserDetailsService resetPasswordCodeUserDetailsService;
    private final CustomOAuth2UserService oAuth2UserService;
    // Handler
//    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    // Provider
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
//    private final DaoAuthenticationProvider daoAuthenticationProvider;
    // Filter
    private final CustomExceptionHandleFilter customExceptionHandleFilter;

    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            CustomUserDetailsService jwtUserDetailsService,
            PasswordEncoder passwordEncoder ) {

        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(jwtUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authenticationProvider, jwtAuthenticationProvider);
    }

//    @Bean
//    public AuthenticationEntryPointFailureHandler authenticationEntryPointFailureHandler() {
//        return new AuthenticationEntryPointFailureHandler(customAuthenticationEntryPoint);
//    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(
            AuthenticationManager authenticationManager) throws Exception {

        final List<String> skipPaths = new ArrayList<>();
        skipPaths.addAll(Arrays.stream(AUTH_WHITELIST_SWAGGER).collect(Collectors.toList()));
        skipPaths.addAll(Arrays.stream(AUTH_WHITELIST_STATIC).collect(Collectors.toList()));
        skipPaths.addAll(Arrays.stream(AUTH_WHITELIST).collect(Collectors.toList()));
//        final RequestMatcher matcher = new CustomRequestMatcher(skipPaths);
//        final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(matcher, authUtil);
//        filter.setAuthenticationManager(authenticationManager);
//        filter.setAuthenticationFailureHandler(authenticationEntryPointFailureHandler());
        final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(authUtil, authenticationManager, skipPaths);

        return filter;
    }

    @Bean
    public CustomUsernamePasswordAuthenticationFilter customUsernamePasswordAuthenticationFilter(
            AuthenticationManager authenticationManager) throws Exception {

        final CustomUsernamePasswordAuthenticationFilter filter =
                new CustomUsernamePasswordAuthenticationFilter();

        filter.setAuthenticationManager(authenticationManager);
        filter.setAuthenticationSuccessHandler(customAuthenticationSuccessHandler);
        filter.setAuthenticationFailureHandler(customAuthenticationFailureHandler);

        return filter;
    }

    @Bean
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList("http://localhost.com:5176"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Allow specific methods
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "access", "refresh")); // Allow specific headers
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Authorization-refresh", "access", "refresh"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(AUTH_WHITELIST_STATIC)
                .requestMatchers(AUTH_WHITELIST_SWAGGER);
    }

    private void configureCustomBeans() {
        final Map<String, ResultCode> map = new HashMap<>();
        map.put("/login", ResultCode.LOGIN_SUCCESS);
        map.put("/reissue", ResultCode.REISSUE_SUCCESS);
        map.put("/login/recovery", ResultCode.LOGIN_WITH_CODE_SUCCESS);
        customAuthenticationSuccessHandler.setResultCodeMap(map);
    }


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CustomUsernamePasswordAuthenticationFilter customUsernamePasswordAuthenticationFilter,
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) throws Exception {

        http
            .cors(cors -> cors.configurationSource(configurationSource())) // Enable CORS with the global configuration
            .csrf(customizer -> customizer.disable())
            .logout(logout -> logout.disable())
            .formLogin(customizer -> customizer.disable())
            .httpBasic(customizer -> customizer.disable())
            .authorizeHttpRequests(
                auth -> auth
                    .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                    .requestMatchers(AUTH_WHITELIST).permitAll()
                    .requestMatchers(AUTH_WHITELIST_SWAGGER).permitAll()
                    .anyRequest().authenticated()
            )
            .oauth2Login( oauth ->
                    oauth.userInfoEndpoint( c -> c.userService(oAuth2UserService) )
                            .successHandler(oAuth2SuccessHandler)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(customExceptionHandleFilter, JwtAuthenticationFilter.class)
            .addFilterBefore(customUsernamePasswordAuthenticationFilter, JwtAuthenticationFilter.class);

        return http.build();
    }

//    @Bean
    public UserDetailsService userDetailsService(){

        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER")
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password("{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW")
                .roles("USER", "ADMIN")
                .build();

        return new InMemoryUserDetailsManager( user, admin );
    }
}
