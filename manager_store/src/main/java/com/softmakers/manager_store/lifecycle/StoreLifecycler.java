package com.softmakers.manager_store.lifecycle;

import com.softmakers.manager_domain.lifecycle.StoreLifecycle;
import com.softmakers.manager_domain.store.RefreshTokenStore;
import com.softmakers.manager_domain.store.RegisterCodeRedisStore;
import com.softmakers.manager_domain.store.UserStore;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StoreLifecycler implements StoreLifecycle {

    private final UserStore userStore;
    private final RefreshTokenStore refreshTokenStore;
    private final RegisterCodeRedisStore registerCodeRedisStore;

    @Override
    public UserStore requestUserStore() {
        return this.userStore;
    }

    @Override
    public RefreshTokenStore requestRefreshTokenStore() {
        return this.refreshTokenStore;
    }

    @Override
    public RegisterCodeRedisStore requestRegisterCodeRedisStore() {
        return this.registerCodeRedisStore;
    }
}
