package com.softmakers.manager_domain.spec;

import com.softmakers.manager_domain.entity.dto.redis.RegisterCode;

public interface RegisterCodeRedisService {

    public void registerCodeRedisSave( RegisterCode registerCode );
}
