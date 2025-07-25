package com.softmakers.manager_domain.lifecycle;

import com.softmakers.manager_domain.store.RefreshTokenStore;
import com.softmakers.manager_domain.store.RegisterCodeRedisStore;
import com.softmakers.manager_domain.store.UserStore;

public interface StoreLifecycle {

    UserStore requestUserStore();
    RefreshTokenStore requestRefreshTokenStore();
    RegisterCodeRedisStore requestRegisterCodeRedisStore();
}
