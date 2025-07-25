package com.softmakers.manager_domain.entity.dto;

import com.softmakers.manager_domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class MemberProfile {

    private BigDecimal memberId;
    private String memberUsername;
    private String memberImageUrl;
    private String memberName;

    public MemberProfile( User user ) {
        this.memberId = user.getUser_id();
        this.memberUsername = user.getUserName();
        this.memberName = user.getUserName();
    }
}
