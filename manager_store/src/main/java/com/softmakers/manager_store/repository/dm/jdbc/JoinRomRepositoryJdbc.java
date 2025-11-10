package com.softmakers.manager_store.repository.dm.jdbc;

import com.softmakers.manager_store.jpo.dm.JoinRoom;
import com.softmakers.manager_store.jpo.dm.Message;

import java.util.List;

public interface JoinRomRepositoryJdbc {

    void saveAllBatch(List<JoinRoom> joinRooms, Message message);

    void updateAllBatch(List<JoinRoom> updateJoinRooms, Message message);

}
