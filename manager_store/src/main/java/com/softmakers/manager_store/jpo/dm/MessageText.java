package com.softmakers.manager_store.jpo.dm;

import com.softmakers.manager_store.jpo.UserJpo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("TEXT")
@Table(name = "message_texts")
public class MessageText extends Message {

    @Lob
    @Column(name = "message_text_content")
    private String content;

    public MessageText(String content, UserJpo userJpo, Room room) {
        super(userJpo, room);
        this.content = content;
    }
}
