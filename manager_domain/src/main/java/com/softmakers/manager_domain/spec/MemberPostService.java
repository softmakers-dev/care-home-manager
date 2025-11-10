package com.softmakers.manager_domain.spec;

import com.softmakers.manager_domain.entity.feed.MemberPostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberPostService {

    public Page<MemberPostDto> getMemberPostDtoPage(
            Long memberId, String username, Pageable pageable);

    public Page<MemberPostDto> getMemberPostDtoPage(
            String username, int size, int page);

    public Page<MemberPostDto> getMemberPostDtoPageWithoutLogin(
            String username, int size, int page);
}
