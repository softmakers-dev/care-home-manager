package com.softmakers.manager_service.resource;

import com.softmakers.manager_domain.entity.dto.ResetPasswordCode;
import com.softmakers.manager_domain.entity.User;

import com.softmakers.manager_domain.lifecycle.ServiceLifecycle;
import com.softmakers.manager_domain.spec.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class ResetPasswordCodeUserDetailsService implements UserDetailsService {

    private UserService userService;
    private final ServiceLifecycle serviceLifecycle;

    private static final String errorMessage = "일치하는 계정이 없습니다";

    public ResetPasswordCodeUserDetailsService(ServiceLifecycle serviceLifecycle) {
        this.serviceLifecycle = serviceLifecycle;
        this.userService = this.serviceLifecycle.requestUserService();
    }

    @Override
    public UserDetails loadUserByUsername( String username ) throws UsernameNotFoundException {

        User user = this.userService.findUserByEmail(username);
        if ( user == null ) {
            throw new UsernameNotFoundException(errorMessage);
        }

        return createUserDetails(new ResetPasswordCode(username, null));
    }

    private UserDetails createUserDetails( ResetPasswordCode resetPasswordCodeuser ) throws UsernameNotFoundException {
        User user = this.userService.findUserByEmail(resetPasswordCodeuser.getUsername());

        if (user == null) {
            throw new UsernameNotFoundException(errorMessage);
        }
        final GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("USER");
        return new org.springframework.security.core.userdetails.User(
                String.valueOf(user.getUser_id()),
                user.getPassword(),
                Collections.singleton(grantedAuthority)
        );
    }
}
