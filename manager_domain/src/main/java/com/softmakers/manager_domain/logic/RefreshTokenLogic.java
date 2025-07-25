package com.softmakers.manager_domain.logic;

import com.softmakers.manager_domain.entity.dto.Location;
import com.softmakers.manager_domain.entity.dto.redis.RefreshToken;
import com.softmakers.manager_domain.lifecycle.StoreLifecycle;
import com.softmakers.manager_domain.spec.RefreshTokenService;
import com.softmakers.manager_domain.store.RefreshTokenStore;

import java.util.Optional;

public class RefreshTokenLogic implements RefreshTokenService {

    private RefreshTokenStore refreshTokenStore;
    private final StoreLifecycle storeLifecycle;

    public RefreshTokenLogic(StoreLifecycle storeLifecycle) {
        this.storeLifecycle = storeLifecycle;
        this.refreshTokenStore = this.storeLifecycle.requestRefreshTokenStore();
    }

    @Override
    public void addRefreshToken( Long memberId, String tokenValue, String device, Location location ) {
        this.refreshTokenStore.addRefreshToken( memberId, tokenValue, device, location );
    }

    @Override
    public Optional<RefreshToken> findRefreshToken( Long memberId, String value ) {
        return this.refreshTokenStore.findRefreshToken( memberId, value );
    }

    @Override
    public void deleteRefreshToken( RefreshToken refreshToken ) {
        this.refreshTokenStore.deleteRefreshToken( refreshToken );
    }

    @Override
    public void deleteRefreshTokenByValue( Long memberId, String value ) {
        this.refreshTokenStore.deleteRefreshTokenByValue( memberId, value );
    }
}
