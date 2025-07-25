package com.softmakers.manager_store;

import com.softmakers.error.exception.JwtInvalidException;
import com.softmakers.manager_domain.entity.dto.Location;
import com.softmakers.manager_domain.entity.dto.redis.RefreshToken;
import com.softmakers.manager_store.repository.RefreshTokenRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RefreshTokenStore implements com.softmakers.manager_domain.store.RefreshTokenStore {

    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    @Value("${max-login-device}")
    private long MAX_LOGIN_DEVICE;

    @Transactional
    public void addRefreshToken(Long userId, String tokenValue, String device, Location location) {

        deleteExceedRefreshTokens(userId);

        final RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .value(tokenValue)
                .device(device)
                .location(location)
                .build();
        refreshTokenRedisRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findRefreshToken( Long memberId, String value ) {
        return refreshTokenRedisRepository.findByUserIdAndValue( memberId, value );
    }

    @Override
    public void deleteRefreshToken(RefreshToken refreshToken) {
        refreshTokenRedisRepository.delete(refreshToken);
    }

    @Override
    public void deleteRefreshTokenByValue(Long memberId, String value) {
        final RefreshToken refreshToken = refreshTokenRedisRepository.findByUserIdAndValue( memberId, value )
                .orElseThrow(JwtInvalidException::new);
        refreshTokenRedisRepository.delete(refreshToken);
    }

    public void deleteExceedRefreshTokens( Long userId ){
        final List<RefreshToken> refreshTokens = refreshTokenRedisRepository.findAllByUserId( userId )
                .stream()
                .sorted(Comparator.comparing(RefreshToken::getLastUpdateDate))
                .collect(Collectors.toList());

        for (int i = 0; i <= refreshTokens.size() - MAX_LOGIN_DEVICE; ++i) {
            refreshTokenRedisRepository.deleteById(refreshTokens.get(i).getId());
            refreshTokens.remove(i);
        }
    }
}
