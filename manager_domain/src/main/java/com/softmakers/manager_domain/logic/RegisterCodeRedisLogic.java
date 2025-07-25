package com.softmakers.manager_domain.logic;

import com.softmakers.manager_domain.entity.dto.redis.RegisterCode;
import com.softmakers.manager_domain.lifecycle.StoreLifecycle;
import com.softmakers.manager_domain.spec.RegisterCodeRedisService;
import com.softmakers.manager_domain.store.RegisterCodeRedisStore;

public class RegisterCodeRedisLogic implements RegisterCodeRedisService {

    private RegisterCodeRedisStore registerCodeRedisStore;
    private final StoreLifecycle storeLifecycle;

    public RegisterCodeRedisLogic(StoreLifecycle storeLifecycle) {
        this.storeLifecycle = storeLifecycle;
        this.registerCodeRedisStore = this.storeLifecycle.requestRegisterCodeRedisStore();
    }

    public void registerCodeRedisSave( RegisterCode registerCode ) {
        this.registerCodeRedisStore.registerCodeSave( registerCode );
    }
}
