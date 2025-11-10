package com.softmakers.manager_domain.entity.dto.search;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class RecommendUser {

    BigDecimal userId;

    Long postCounts;

    @QueryProjection
    public RecommendUser(BigDecimal userId, Long postCounts) {
        this.userId = userId;
        this.postCounts = postCounts;
    }
}
