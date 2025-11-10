package com.softmakers.manager_store.repository.dm;

import com.softmakers.manager_store.jpo.UserJpo;
import com.softmakers.manager_store.jpo.dm.Room;
import com.softmakers.manager_store.jpo.dm.RoomMember;
import com.softmakers.manager_store.repository.dm.jdbc.RoomMemberRepositoryJdbc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomMemberRepository extends JpaRepository<RoomMember, Long>,
        RoomMemberRepositoryJdbc {

    List<RoomMember> findAllByUserJpoIn(List<UserJpo> userJpoList);

    List<RoomMember> findAllByRoomIdIn(List<Long> roomIds);

    @Query("select rm from RoomMember rm join fetch rm.userJpo where rm.room.id = :roomId")
    List<RoomMember> findAllWithUserJpoByRoomId(@Param("roomId") Long roomId);

    @Query("select rm from RoomMember rm join fetch rm.userJpo where rm.room.id in :roomIds")
    List<RoomMember> findAllWithUserJpoByRoomIdIn(@Param("roomIds") List<Long> roomIds);

    List<RoomMember> findAllByRoom(Room room);
}
