package com.softmakers.manager_store.repository.dm;

import com.softmakers.manager_store.jpo.UserJpo;
import com.softmakers.manager_store.jpo.dm.JoinRoom;
import com.softmakers.manager_store.jpo.dm.Room;
import com.softmakers.manager_store.repository.dm.jdbc.JoinRomRepositoryJdbc;
import com.softmakers.manager_store.repository.dm.querydsl.JoinRoomRepositoryQuerydsl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JoinRoomRepository extends JpaRepository<JoinRoom, Long>
        , JoinRoomRepositoryQuerydsl, JoinRomRepositoryJdbc {

    @Query(value = "select j from JoinRoom j join fetch j.userJpo where j.room.id = :id")
    List<JoinRoom> findAllWithUserJpoByRoomId(@Param("id") Long id);

    Optional<JoinRoom> findByUserJpoAndRoom(UserJpo userJpo, Room room);

    List<JoinRoom> findDistinctByRoomAndUserJpoIn(Room room, List<UserJpo> userJpos);
    // In your JoinRoomRepository:
    @Query("SELECT DISTINCT jr FROM JoinRoom jr WHERE jr.room = :room AND jr.userJpo IN :userJpos")
    List<JoinRoom> findByRoomAndUserJpoIn(@Param("room") Room room, @Param("userJpos") List<UserJpo> userJpos);

    void deleteByUserJpoAndRoom(UserJpo userJpo, Room room);

    @Query(value = "select j from JoinRoom j join fetch j.message where j.room.id = :id")
    List<JoinRoom> findAllWithMessageByRoomId(@Param("id") Long id);
}
