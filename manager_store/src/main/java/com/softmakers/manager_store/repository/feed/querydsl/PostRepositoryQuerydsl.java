package com.softmakers.manager_store.repository.feed.querydsl;

import com.softmakers.manager_domain.entity.feed.PostDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PostRepositoryQuerydsl {
    public Page<PostDto> findPostDtoPageOfFollowingMembersOrHashtagsByMemberId(
            Long userId, Pageable pageable );
    Optional<PostDto> findPostDtoByPostIdAndMemberId(Long postId, Long memberId);
}
