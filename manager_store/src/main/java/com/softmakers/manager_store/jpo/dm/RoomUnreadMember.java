package com.softmakers.manager_store.jpo.dm;

import com.softmakers.manager_store.jpo.UserJpo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "room_unread_members")
public class RoomUnreadMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private Message message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserJpo userJpo;

    @Builder
    public RoomUnreadMember(Room room, Message message, UserJpo userJpo) {
        this.room = room;
        this.message = message;
        this.userJpo = userJpo;
    }
}
