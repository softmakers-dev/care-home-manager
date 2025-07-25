package com.softmakers.manager_store.repository;

import com.softmakers.manager_domain.entity.dto.redis.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRedisRepository extends CrudRepository<RefreshToken, String> {

    List<RefreshToken> findAllByUserId(Long userId);

    Optional<RefreshToken> findByUserIdAndValue(Long userId, String value);

    Optional<RefreshToken> findByUserIdAndId(Long userId, String id);

}
