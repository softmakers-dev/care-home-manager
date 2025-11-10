package com.softmakers.manager_store.repository.feed.querydsl;

import com.softmakers.manager_domain.entity.feed.PostLikeCountDto;
import com.softmakers.manager_domain.entity.feed.PostLikeDto;

import java.util.List;

public interface PostLikeRepositoryQuerydsl {

    List<PostLikeCountDto> findAllPostLikeCountDtoOfFollowingsLikedPostByMemberAndPostIdIn(Long userId,
                                                                                           List<Long> postIds);
    List<PostLikeDto> findAllPostLikeDtoOfFollowingsByMemberIdAndPostIdIn(
            Long memberId, List<Long> postIds );
}
