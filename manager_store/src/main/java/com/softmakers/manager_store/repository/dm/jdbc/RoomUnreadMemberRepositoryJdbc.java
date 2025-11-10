package com.softmakers.manager_store.repository.dm.jdbc;

import com.softmakers.manager_store.jpo.dm.Message;
import com.softmakers.manager_store.jpo.dm.RoomUnreadMember;

import java.util.List;

public interface RoomUnreadMemberRepositoryJdbc {

    void saveAllBatch(List<RoomUnreadMember> roomUnreadMembers, Message message);
}
