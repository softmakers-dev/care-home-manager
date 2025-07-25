package com.softmakers.manager_domain.lifecycle;

import com.softmakers.manager_domain.spec.RefreshTokenService;
import com.softmakers.manager_domain.spec.RegisterCodeRedisService;
import com.softmakers.manager_domain.spec.UserService;

public interface ServiceLifecycle {

    UserService requestUserService();
    RefreshTokenService requestRefreshTokenService();
    RegisterCodeRedisService requestRegisterCodeRedisService();
}
