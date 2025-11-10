package com.softmakers.manager_store.repository.feed.querydsl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.softmakers.manager_domain.entity.User;
import com.softmakers.manager_domain.entity.feed.MemberPostDto;
import com.softmakers.manager_domain.entity.feed.QMemberPostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static com.softmakers.manager_store.jpo.QUserJpo.userJpo;
import static com.softmakers.manager_store.jpo.feed.QBookmark.bookmark;
import static com.softmakers.manager_store.jpo.feed.QPost.post;
import static com.softmakers.manager_store.jpo.feed.QPostImage.postImage;
import static com.softmakers.manager_store.jpo.feed.QPostLike.postLike;
import static com.softmakers.manager_store.jpo.feed.QPostTag.postTag;

@RequiredArgsConstructor
public class MemberPostRepositoryQuerydslImpl implements MemberPostRepositoryQuerydsl{
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<MemberPostDto> findMemberPostDtoPageByLoginMemberIdAndTargetUsername(
            Long loginMemberId, String username, Pageable pageable) {

        final List<MemberPostDto> posts = queryFactory
                .select(new QMemberPostDto(
                        post.postId,
                        post.userJpo.user_id,
                        post.userJpo.userName,
                        post.postImages.size().gt(1),
                        post.likeFlag,
                        existPostLikeWherePostEqAndMemberIdEq(loginMemberId),
                        post.comments.size(),
                        post.postLikes.size()))
                .from(post)
                .innerJoin(post.userJpo, userJpo)
                .where( post.userJpo.userName.eq(username) )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.postId.desc())
                .distinct()
                .fetch();

        final long total = queryFactory
                .selectFrom(post)
                .where(post.userJpo.userName.eq(username))
                .fetch().size();
        return new PageImpl<>(posts, pageable, total);
    }

    @Override
    public Page<MemberPostDto> findMemberSavedPostDtoPageByLoginMemberId(
            Long loginMemberId, Pageable pageable) {

        final List<MemberPostDto> posts = queryFactory
                .select(new QMemberPostDto(
                        bookmark.post.postId,
                        bookmark.post.userJpo.user_id,
                        bookmark.post.userJpo.userName,
                        bookmark.post.postImages.size().gt(1),
                        post.likeFlag,
                        existPostLikeWherePostEqAndMemberIdEq(loginMemberId),
                        bookmark.post.comments.size(),
                        bookmark.post.postLikes.size()))
                .from(bookmark)
                .innerJoin(bookmark.post, post)
                .innerJoin(bookmark.post.userJpo, userJpo)
                .where(bookmark.user.user_id.eq(BigDecimal.valueOf(loginMemberId)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(bookmark.post.postId.desc())
                .distinct()
                .fetch();

        final long total = queryFactory
                .selectFrom(bookmark)
                .where(bookmark.user.user_id.eq(BigDecimal.valueOf(loginMemberId)))
                .fetch().size();
        return new PageImpl<>(posts, pageable, total);
    }

    @Override
    public Page<MemberPostDto> findMemberTaggedPostDtoPageByLoginMemberIdAndTargetUsername(
            Long loginMemberId, String username, Pageable pageable) {

        final List<MemberPostDto> posts = queryFactory
                .select(new QMemberPostDto(
                        postTag.postImage.post.postId,
                        postTag.postImage.post.userJpo.user_id,
                        postTag.postImage.post.userJpo.userName,
                        postTag.postImage.post.postImages.size().gt(1),
                        postTag.postImage.post.likeFlag,
                        existPostLikeWherePostEqAndMemberIdEq(loginMemberId),
                        postTag.postImage.post.comments.size(),
                        postTag.postImage.post.postLikes.size()))
                .from(postTag)
                .innerJoin(postTag.postImage, postImage)
                .innerJoin(postTag.postImage.post, post)
                .innerJoin(postTag.postImage.post.userJpo, userJpo)
                .where(postTag.tag.username.eq(username))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(postTag.postImage.post.postId.desc())
                .distinct()
                .fetch();

        final long total = queryFactory
                .selectFrom(postTag)
                .where(postTag.tag.username.eq(username))
                .fetch().size();
        return new PageImpl<>(posts, pageable, total);
    }

    private BooleanExpression existPostLikeWherePostEqAndMemberIdEq(Long id) {
//        return JPAExpressions
//                .selectFrom(postLike)
//                .where(postLike.post.eq(post).and(postLike.user.user_id.eq(BigDecimal.valueOf(id))))
//                .exists();
        return Expressions.FALSE;
    }
}
