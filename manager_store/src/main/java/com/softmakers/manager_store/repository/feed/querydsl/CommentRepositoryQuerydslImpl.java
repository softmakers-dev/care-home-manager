package com.softmakers.manager_store.repository.feed.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.softmakers.manager_domain.entity.feed.CommentDto;
import com.softmakers.manager_domain.entity.feed.QCommentDto;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static com.softmakers.manager_store.jpo.QUserJpo.userJpo;
import static com.softmakers.manager_store.jpo.feed.QComment.comment;
import static com.softmakers.manager_store.jpo.feed.QCommentLike.commentLike;
import static com.softmakers.manager_store.jpo.feed.QRecentComment.recentComment;

@RequiredArgsConstructor
public class CommentRepositoryQuerydslImpl implements CommentRepositoryQuerydsl {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<CommentDto> findAllRecentCommentDtoByMemberIdAndPostIdIn(Long memberId, List<Long> postIds) {
        return queryFactory
                .select(new QCommentDto(
                        comment.post.postId,
                        comment.comment.id,
                        comment.user.user_id,
                        comment.user.userName,
                        comment.user.image.imageUrl,
                        comment.comment.content,
                        comment.comment.createdAt,
                        comment.comment.commentLikes.size(),
                        isExistCommentLikeWhereCommentEqAndMemberEq(memberId),
                        comment.comment.children.size()
                ))
                .from( recentComment )
                .innerJoin(recentComment.comment, comment)
                .innerJoin(recentComment.userJpo, userJpo)
                .where( recentComment.post.postId.in( postIds ) )
                .fetch();
    }

    public Page<CommentDto> findCommentDtoPageByMemberIdAndPostId( Long userId, Long postId, Pageable pageable ) {
        final List<CommentDto> commentDtos = queryFactory
                .select(new QCommentDto(
                        comment.post.postId,
                        comment.id,
                        comment.user.user_id,
                        comment.user.userName,
                        comment.user.image.imageUrl,
                        comment.content,
                        comment.createdAt,
                        comment.commentLikes.size(),
                        isExistCommentLikeWhereCommentEqAndMemberEq(userId),
                        comment.children.size()
                ))
                .from(comment)
                .where(comment.post.postId.eq( postId )
                        .and(comment.parent.id.isNull()))
                .innerJoin(comment.user, userJpo)
                .orderBy(comment.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        final long total = queryFactory
                .selectFrom(comment)
                .where(comment.post.postId.eq(postId).
                        and(comment.parent.id.isNull()))
                .fetchCount();

        return new PageImpl<>(commentDtos, pageable, total);
    }

    @Override
    public Page<CommentDto> findReplyDtoPageByMemberIdAndCommentId(Long memberId, Long commentId, Pageable pageable) {
        final List<CommentDto> commentDtos = queryFactory
                .select(new QCommentDto(
                        comment.post.postId,
                        comment.id,
                        comment.user.user_id,
                        comment.user.userName,
                        comment.user.image.imageUrl,
                        comment.content,
                        comment.createdAt,
                        comment.commentLikes.size(),
                        isExistCommentLikeWhereCommentEqAndMemberEq(memberId),
                        comment.children.size()
                ))
                .from(comment)
                .where(comment.parent.id.eq(commentId))
                .innerJoin(comment.user, userJpo)
                .orderBy(comment.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        final long total = queryFactory
                .selectFrom(comment)
                .where(comment.parent.id.eq(commentId))
                .fetchCount();

        return new PageImpl<>(commentDtos, pageable, total);
    }

    @Override
    public Page<CommentDto> findCommentDtoPageWithoutLoginByPostId(Long postId, Pageable pageable) {
        final List<CommentDto> commentDtos = queryFactory
                .select(new QCommentDto(
                        comment.post.postId,
                        comment.id,
                        comment.user.user_id,
                        comment.user.userName,
                        comment.user.image.imageUrl,
                        comment.content,
                        comment.createdAt,
                        comment.commentLikes.size(),
                        comment.children.size()
                ))
                .from(comment)
                .where(comment.post.postId.eq(postId).and(comment.parent.id.isNull()))
                .innerJoin(comment.user, userJpo)
                .orderBy(comment.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        final long total = queryFactory
                .selectFrom(comment)
                .where(comment.post.postId.eq(postId).and(comment.parent.id.isNull()))
                .fetchCount();

        return new PageImpl<>(commentDtos, pageable, total);
    }

    private BooleanExpression isExistCommentLikeWhereCommentEqAndMemberEq(Long userId) {
        return JPAExpressions
                .selectFrom(commentLike)
                .where(commentLike.comment.eq(comment).and(commentLike.user.user_id.eq( BigDecimal.valueOf(userId)) ))
                .exists();
    }
}
