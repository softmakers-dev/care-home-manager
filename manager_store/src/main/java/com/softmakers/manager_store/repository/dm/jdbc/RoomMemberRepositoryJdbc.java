package com.softmakers.manager_store.repository.dm.jdbc;

import com.softmakers.manager_store.jpo.UserJpo;
import com.softmakers.manager_store.jpo.dm.Room;

import java.util.List;

public interface RoomMemberRepositoryJdbc {

    void saveAllBatch(Room room, List<UserJpo> userJpos);
}
