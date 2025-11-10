package com.softmakers.manager_domain.store;

import com.softmakers.manager_domain.entity.feed.MemberPostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberPostStore {

    public Page<MemberPostDto> getMemberPostDtoPage(
            Long memberId, String username, Pageable pageable);

    public Page<MemberPostDto> getMemberPostDtoPage(
            String username, int size, int page);

    public Page<MemberPostDto> getMemberPostDtoPageWithoutLogin(
            String username, int size, int page);
}
