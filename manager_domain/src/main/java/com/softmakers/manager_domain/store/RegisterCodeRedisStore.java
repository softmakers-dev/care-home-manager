package com.softmakers.manager_domain.store;

import com.softmakers.manager_domain.entity.dto.redis.RegisterCode;

public interface RegisterCodeRedisStore {

    public void registerCodeSave( RegisterCode registerCode );
}
