package com.softmakers.manager_store.repository.feed.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;

import com.softmakers.manager_domain.entity.feed.PostLikeCountDto;
import com.softmakers.manager_domain.entity.feed.PostLikeDto;

import com.softmakers.manager_domain.entity.feed.QPostLikeCountDto;
import com.softmakers.manager_domain.entity.feed.QPostLikeDto;

import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.softmakers.manager_store.jpo.QUserJpo.userJpo;
import static com.softmakers.manager_store.jpo.feed.QPostLike.postLike;

@RequiredArgsConstructor
public class PostLikeRepositoryQuerydslImpl implements PostLikeRepositoryQuerydsl {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<PostLikeCountDto> findAllPostLikeCountDtoOfFollowingsLikedPostByMemberAndPostIdIn(
            Long userId, List<Long> postIds) {

        return queryFactory
                .select(new QPostLikeCountDto(
                        postLike.post.postId,
                        postLike.count()
                ))
                .from(postLike)
                .where(postLike.post.postId.in(postIds))
                .groupBy(postLike.post.postId)
                .fetch();
    }

    @Override
    public List<PostLikeDto> findAllPostLikeDtoOfFollowingsByMemberIdAndPostIdIn(
            Long memberId, List<Long> postIds ) {
        return queryFactory
                .select(new QPostLikeDto(
                        postLike.post.postId,
                        postLike.user.userName
                ))
                .from(postLike)
                .innerJoin(postLike.user, userJpo)
                .fetch();
    }
}
