package com.softmakers.manager_store.repository.dm.querydsl;

import com.softmakers.manager_store.jpo.dm.JoinRoom;
import com.softmakers.manager_store.jpo.dm.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageRepositoryQuerydsl {

    Page<Message> findAllByJoinRoom(JoinRoom joinRoom, Pageable pageable);
}
