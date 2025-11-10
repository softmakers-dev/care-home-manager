package com.softmakers.manager_domain.entity.feed;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class PostLikeCountDto {

    private Long postId;
    private Long postLikesCount;

    @QueryProjection
    public PostLikeCountDto(Long postId, Long postLikesCount) {
        this.postId = postId;
        this.postLikesCount = postLikesCount;
    }
}
