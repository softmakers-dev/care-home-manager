package com.softmakers.manager_domain.entity.dto.dm;

import com.querydsl.core.annotations.QueryProjection;
import com.softmakers.manager_domain.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class JoinRoomDto {

    private Long roomId;
    private MessageDto lastMessage;
    private boolean unreadFlag;
    private MemberSimpleInfo inviter;
    private List<MemberSimpleInfo> members = new ArrayList<>();

    @QueryProjection
    public JoinRoomDto(Long roomId, boolean unreadFlag, BigDecimal userId, String userName
//            ,String userImgUrl
    ) {
        this.roomId = roomId;
        this.unreadFlag = unreadFlag;

        User user = new User( userId, userName );
//        user.setUserImageUrl( userImgUrl );
        this.inviter = new MemberSimpleInfo(user);
    }
}
