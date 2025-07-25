package com.softmakers.manager_store.repository;

import com.softmakers.manager_domain.entity.dto.redis.RegisterCode;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RegisterCodeRedisRepository extends CrudRepository<RegisterCode, String> {

    Optional<RegisterCode> findByUsername(String username);
}
