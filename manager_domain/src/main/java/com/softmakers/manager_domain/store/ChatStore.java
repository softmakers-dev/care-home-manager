package com.softmakers.manager_domain.store;

import com.softmakers.manager_domain.entity.User;
import com.softmakers.manager_domain.entity.dto.dm.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ChatStore {

    public ChatRoomCreateResponse createRoom(List<String> usernames, User user);
    public Page<JoinRoomDto> getJoinRooms(int page);
    public Page<MessageDto> getChatMessages(Long roomId, Integer page);
    public ChatRoomInquireResponse inquireRoom(Long roomId);
    public void sendMessage(MessageRequest request);
}
