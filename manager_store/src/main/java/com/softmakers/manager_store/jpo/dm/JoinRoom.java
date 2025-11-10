package com.softmakers.manager_store.jpo.dm;

import com.softmakers.manager_store.jpo.UserJpo;

import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "join_rooms")
public class JoinRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserJpo userJpo;

    @CreatedDate
    @Column(name = "createdAt")
    private LocalDateTime createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private Message message;

    @Builder
    public JoinRoom(Room room, UserJpo userJpo, Message message) {
        this.room = room;
        this.userJpo = userJpo;
        this.message = message;
    }

    public void updateMessage(Message message) {
        this.message = message;
    }
}
