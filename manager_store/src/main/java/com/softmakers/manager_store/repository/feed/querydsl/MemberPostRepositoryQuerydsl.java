package com.softmakers.manager_store.repository.feed.querydsl;

import com.softmakers.manager_domain.entity.feed.MemberPostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberPostRepositoryQuerydsl {

    Page<MemberPostDto> findMemberPostDtoPageByLoginMemberIdAndTargetUsername(Long loginMemberId,
                                                                              String username,
                                                                              Pageable pageable);

    Page<MemberPostDto> findMemberSavedPostDtoPageByLoginMemberId(Long loginMemberId,
                                                                  Pageable pageable);

    Page<MemberPostDto> findMemberTaggedPostDtoPageByLoginMemberIdAndTargetUsername(
            Long loginMemberId, String username, Pageable pageable);

}
