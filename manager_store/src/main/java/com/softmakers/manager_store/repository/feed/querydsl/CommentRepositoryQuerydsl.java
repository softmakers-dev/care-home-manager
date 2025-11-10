package com.softmakers.manager_store.repository.feed.querydsl;

import com.softmakers.manager_domain.entity.feed.CommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentRepositoryQuerydsl {
    public List<CommentDto> findAllRecentCommentDtoByMemberIdAndPostIdIn(
            Long memberId, List<Long> postIds);
    Page<CommentDto> findCommentDtoPageByMemberIdAndPostId(
            Long memberId, Long postId, Pageable pageable);
    Page<CommentDto> findReplyDtoPageByMemberIdAndCommentId(
            Long memberId, Long commentId, Pageable pageable);
    public Page<CommentDto> findCommentDtoPageWithoutLoginByPostId(
            Long postId, Pageable pageable);
    }
