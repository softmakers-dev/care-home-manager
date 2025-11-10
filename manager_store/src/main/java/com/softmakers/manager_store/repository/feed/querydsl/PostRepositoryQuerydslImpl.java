package com.softmakers.manager_store.repository.feed.querydsl;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import static com.softmakers.manager_store.jpo.feed.QPost.post;
import static com.softmakers.manager_store.jpo.QUserJpo.userJpo;
import static com.softmakers.manager_store.vo.QImage.image;

import com.softmakers.manager_domain.entity.feed.PostDto;
import com.softmakers.manager_domain.entity.feed.QPostDto;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;



@RequiredArgsConstructor
public class PostRepositoryQuerydslImpl implements PostRepositoryQuerydsl{
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PostDto> findPostDtoPageOfFollowingMembersOrHashtagsByMemberId(
            Long userId, Pageable pageable ) {

        final List<PostDto> postDtos = queryFactory
                .select(new QPostDto(
                        post.postId,
                        post.content,
                        post.createdAt,
                        post.userJpo.userName,
                        post.userJpo.user_id,
                        post.userJpo.image.imageUrl,
                        post.comments.size(),
                        post.postLikes.size(),
                        post.likeFlag,
                        post.commentFlag
                ))
                .from( post )
                .innerJoin(post.userJpo, userJpo) // join user
//                .leftJoin(userJpo.image, image).fetchJoin() // join and fetch image
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.postId.desc())
                .distinct()
                .fetch();

        final long total = queryFactory
                .selectFrom( post )
                .fetchCount();

        return new PageImpl<>( postDtos, pageable, total );
    }

    @Override
    public Optional<PostDto> findPostDtoByPostIdAndMemberId(Long postId, Long userId) {
        return Optional.ofNullable(queryFactory
                .select(new QPostDto(
                        post.postId,
                        post.content,
                        post.createdAt,
                        post.userJpo.userName,
                        post.userJpo.user_id,
                        post.userJpo.image.imageUrl,
                        post.comments.size(),
                        post.postLikes.size(),
                        post.likeFlag,
                        post.commentFlag
                ))
                .from(post)
                .innerJoin(post.userJpo, userJpo) // join user
                .where(post.postId.eq(postId))
                .fetchOne());
    }
}
