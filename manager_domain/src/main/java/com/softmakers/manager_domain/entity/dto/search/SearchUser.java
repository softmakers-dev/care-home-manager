package com.softmakers.manager_domain.entity.dto.search;

import com.querydsl.core.annotations.QueryProjection;
import com.softmakers.manager_domain.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Collections;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchUser extends SearchDto {

    private User user;

    @QueryProjection
    public SearchUser(String dtype, BigDecimal userId, String userName) {
        super(dtype);
        this.user = new User(userId, userName);
    }
}
