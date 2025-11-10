package com.softmakers.manager_domain.logic;

import com.softmakers.manager_domain.entity.User;
import com.softmakers.manager_domain.entity.dto.dm.*;
import com.softmakers.manager_domain.lifecycle.StoreLifecycle;
import com.softmakers.manager_domain.spec.ChatService;
import com.softmakers.manager_domain.store.ChatStore;
import org.springframework.data.domain.Page;

import java.util.List;

public class ChatLogic implements ChatService {

    private final StoreLifecycle storeLifecycle;
    private ChatStore chatStore;

    public ChatLogic(StoreLifecycle storeLifecycle) {
        this.storeLifecycle = storeLifecycle;
        this.chatStore = this.storeLifecycle.requestChatStore();
    }

    @Override
    public ChatRoomCreateResponse createRoom(List<String> usernames, User user) {
        return this.chatStore.createRoom( usernames, user );
    }

    @Override
    public Page<JoinRoomDto> getJoinRooms(int page) {
        return this.chatStore.getJoinRooms(page);
    }

    @Override
    public ChatRoomInquireResponse inquireRoom(Long roomId) {
        return this.chatStore.inquireRoom( roomId );
    }

    @Override
    public Page<MessageDto> getChatMessages(Long roomId, Integer page) {
        return this.chatStore.getChatMessages( roomId, page );
    }

    @Override
    public void sendMessage(MessageRequest request) {
        this.chatStore.sendMessage( request );
    }
}
