package com.softmakers.manager_store.jpo.dm;

import com.softmakers.manager_store.jpo.UserJpo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserJpo userJpo;

    @OneToMany(mappedBy = "room")
    private List<RoomUnreadMember> roomUnreadMembers = new ArrayList<>();

    @OneToMany(mappedBy = "room")
    private List<RoomMember> roomMembers = new ArrayList<>();

    public Room(UserJpo userJpo) {
        this.userJpo = userJpo;
    }
}
