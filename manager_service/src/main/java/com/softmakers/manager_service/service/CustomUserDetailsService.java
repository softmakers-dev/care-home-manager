package com.softmakers.manager_service.service;

import com.softmakers.manager_domain.entity.User;

import com.softmakers.manager_domain.lifecycle.ServiceLifecycle;
import com.softmakers.manager_domain.spec.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UserService userService;
    private final ServiceLifecycle serviceLifecycle;

    private static final String errorMessage = "일치하는 계정이 없습니다";

    public CustomUserDetailsService(ServiceLifecycle serviceLifecycle) {
        this.serviceLifecycle = serviceLifecycle;
        this.userService = this.serviceLifecycle.requestUserService();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = this.userService.findUserByEmail(username);
        if ( user == null ) {
            throw new UsernameNotFoundException(errorMessage);
        }

        return createUserDetails(user);
    }

    private UserDetails createUserDetails( User user ) {

        final GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("USER");
        org.springframework.security.core.userdetails.User securityUser = new org.springframework.security.core.userdetails.User(
                String.valueOf(user.getUser_id()),
                user.getPassword(),
                Collections.singleton(grantedAuthority)
        );
        System.out.println("securityUser: " + securityUser.toString());
        return securityUser;
    }
}
