package com.softmakers.manager_domain.entity.dto.dm;

import com.softmakers.manager_domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {

    private Long roomId;
    private Long messageId;
    private User sender;
//    private Image senderImage;
    private Object content;
    private String messageType;
    private LocalDateTime messageDate;
    private List<User> likeMembers = new ArrayList<>();

    public MessageDto(Long roomId, Long messageId, User sender,
                      LocalDateTime createdDate, String messageType, Object content
    ) {
//        this.roomId = message.getRoom().getId();
//        this.messageId = message.getId();
//        this.sender = new MemberDto(message.getMember());
////        this.senderImage = message.getMember().getImage();
//        this.messageDate = message.getCreatedDate();
//        this.messageType = message.getDtype();
//        this.content = message.getContent();
        this.roomId = roomId;
        this.messageId = messageId;
        this.sender = sender;
        this.messageDate = createdDate;
        this.messageType = messageType;
        this.content = content;
    }
}
