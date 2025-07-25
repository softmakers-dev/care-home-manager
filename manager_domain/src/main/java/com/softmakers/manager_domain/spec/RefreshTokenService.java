package com.softmakers.manager_domain.spec;

import com.softmakers.manager_domain.entity.dto.Location;
import com.softmakers.manager_domain.entity.dto.redis.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {

    public void addRefreshToken(Long memberId, String tokenValue, String device, Location location);
    public Optional<RefreshToken> findRefreshToken(Long memberId, String value);
    public void deleteRefreshToken(RefreshToken refreshToken);
    public void deleteRefreshTokenByValue(Long memberId, String value);
}
