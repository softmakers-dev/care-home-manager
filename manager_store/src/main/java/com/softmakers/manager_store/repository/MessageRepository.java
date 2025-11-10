package com.softmakers.manager_store.repository;

import com.softmakers.manager_store.jpo.dm.Message;
import com.softmakers.manager_store.jpo.dm.Room;
import com.softmakers.manager_store.repository.dm.querydsl.MessageRepositoryQuerydsl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long>, MessageRepositoryQuerydsl {

    @Query("select m from Message m join fetch m.room where m.id = :id")
    Optional<Message> findWithRoomById(@Param("id") Long id);

    Long countByCreatedDateBetweenAndRoom(LocalDateTime start, LocalDateTime end, Room room);

    List<Message> findTop2ByCreatedDateBetweenAndRoomOrderByIdDesc(LocalDateTime start,
                                                                   LocalDateTime end, Room room);

}
