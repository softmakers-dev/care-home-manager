package com.softmakers.manager_store;

import com.softmakers.manager_domain.entity.dto.redis.RegisterCode;
import com.softmakers.manager_store.repository.RegisterCodeRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RegisterCodeRedisStore implements com.softmakers.manager_domain.store.RegisterCodeRedisStore {

    private final RegisterCodeRedisRepository registerCodeRedisRepository;

    @Override
    public void registerCodeSave(RegisterCode registerCode) {
        this.registerCodeRedisRepository.save( registerCode );
    }
}
