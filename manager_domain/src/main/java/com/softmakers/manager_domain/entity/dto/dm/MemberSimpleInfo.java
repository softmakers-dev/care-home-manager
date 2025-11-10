package com.softmakers.manager_domain.entity.dto.dm;

import com.softmakers.manager_domain.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberSimpleInfo {

    private String username;
    private String imageUrl;

    public MemberSimpleInfo(User user) {
        this.username = user.getUserName();
        this.imageUrl = user.getUserImageUrl();
    }
}
