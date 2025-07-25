package com.softmakers.manager_domain.entity.dto.redis;

import com.softmakers.manager_domain.entity.dto.Location;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

@RedisHash("refresh_tokens")
@Getter
@NoArgsConstructor( access = AccessLevel.PROTECTED )
public class RefreshToken implements Serializable {

    @Id
    @Indexed
    private String id;

    @Indexed
    private String value;

    @Indexed
    private Long userId;

    @TimeToLive( unit = TimeUnit.DAYS)
    private Long timeout = 7L;

    private LocalDateTime lastUpdateDate;

    private String city;

    private String longitude;

    private String latitude;

    private String device;

    @Builder
    public RefreshToken(Long userId, String value, Location location, String device) {
        this.lastUpdateDate = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        this.userId = userId;
        this.value = value;
        this.city = location.getCity();
        this.longitude = location.getLongitude();
        this.latitude = location.getLatitude();
        this.device = device;
    }

    public void updateToken(String newValue) {
        this.lastUpdateDate = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        this.value = newValue;
    }

    public Location getLocation() {
        return new Location(city, longitude, latitude);
    }
}
