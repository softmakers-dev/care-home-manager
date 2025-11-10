package com.softmakers.manager_store.repository.dm;

import com.softmakers.manager_store.jpo.UserJpo;
import com.softmakers.manager_store.jpo.dm.Message;
import com.softmakers.manager_store.jpo.dm.Room;
import com.softmakers.manager_store.jpo.dm.RoomUnreadMember;
import com.softmakers.manager_store.repository.dm.jdbc.RoomUnreadMemberRepositoryJdbc;
import com.softmakers.manager_store.repository.dm.jdbc.RoomUnreadMemberRepositoryJdbcImpl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomUnreadMemberRepository
        extends JpaRepository<RoomUnreadMember, Long>, RoomUnreadMemberRepositoryJdbc {

    List<RoomUnreadMember> findAllByRoomAndUserJpo(Room room, UserJpo userJpo);

    List<RoomUnreadMember> findAllByMessage(Message message);

    List<RoomUnreadMember> findAllByRoom(Room room);

    List<RoomUnreadMember> findAllByUserJpo(UserJpo userJpo);
}
