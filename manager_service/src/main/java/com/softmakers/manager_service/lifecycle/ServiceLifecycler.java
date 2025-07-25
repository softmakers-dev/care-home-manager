package com.softmakers.manager_service.lifecycle;

import com.softmakers.manager_domain.lifecycle.ServiceLifecycle;
import com.softmakers.manager_domain.spec.RefreshTokenService;
import com.softmakers.manager_domain.spec.RegisterCodeRedisService;
import com.softmakers.manager_domain.spec.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServiceLifecycler implements ServiceLifecycle {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final RegisterCodeRedisService registerCodeRedisService;

    @Override
    public UserService requestUserService() {
        return this.userService;
    }

    @Override
    public RefreshTokenService requestRefreshTokenService() {
        return this.refreshTokenService;
    }

    @Override
    public RegisterCodeRedisService requestRegisterCodeRedisService() {
        return this.registerCodeRedisService;
    }
}
