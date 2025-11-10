package com.softmakers.manager_store;

import com.softmakers.error.exception.ChatRoomNotFoundException;
import com.softmakers.error.exception.JoinRoomNotFoundException;
import com.softmakers.error.exception.UserJpoDoesNotExistException;
import com.softmakers.manager_domain.entity.User;
import com.softmakers.manager_domain.entity.dto.dm.*;
import com.softmakers.manager_domain.store.ChatStore;
import com.softmakers.manager_store.jpo.UserJpo;
import com.softmakers.manager_store.jpo.dm.*;
import com.softmakers.manager_store.repository.MessageRepository;
import com.softmakers.manager_store.repository.UserRepository;
import com.softmakers.manager_store.repository.dm.*;
import com.softmakers.utilities.AuthUtil;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hibernate.grammars.ordering.OrderingLexer.DESC;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ChatJpaStore implements ChatStore {
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final JoinRoomRepository joinRoomRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final RoomUnreadMemberRepository roomUnreadMemberRepository;
    private final AuthUtil authUtil;
    private final MessageRepository messageRepository;
    private final MessageTextRepository messageTextRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public ChatRoomCreateResponse createRoom(List<String> usernames, User user) {
        UserJpo inviter = new UserJpo( user );
        usernames.add(inviter.getUserName());
        final List<UserJpo> userJpos = userRepository.findAllByUserNameIn( usernames );
        final List<User> users = userJpos.stream()
                .map(UserJpo::toDomain)
                .collect(Collectors.toList());

        final Room room;
        final boolean status;
        final Optional<Room> roomOptional = getRoomByMembers( userJpos );
        if (roomOptional.isEmpty()) {
            status = true;
            room = roomRepository.save(new Room(inviter));
            roomMemberRepository.saveAllBatch(room, userJpos);
        } else {
            status = false;
            room = roomOptional.get();
        }

        final List<MemberSimpleInfo> memberSimpleInfos = users.stream()
                .map( MemberSimpleInfo::new )
                .collect(Collectors.toList());

        return new ChatRoomCreateResponse(status, room.getId(),
                new MemberSimpleInfo( user ),
                memberSimpleInfos);
    }

    @Override
    public Page<JoinRoomDto> getJoinRooms(int page) {
        final Long loginMember = authUtil.getLoginUserId();
        Optional<UserJpo> userJpoOptional = this.userRepository.findById( BigDecimal.valueOf(loginMember) );

        Page<JoinRoomDto> joinRoomDtoPage = null;
        if( userJpoOptional.isPresent() ) {
            UserJpo userJpo = userJpoOptional.get();

            page = (page == 0 ? 0 : page - 1);
            final Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
            joinRoomDtoPage = joinRoomRepository.findJoinRoomDtoPageByUserId(userJpo.getUser_id().longValue(),
                    pageable);

            final List<JoinRoomDto> joinRoomDtos = joinRoomDtoPage.getContent();
            final List<Long> roomIds = joinRoomDtos.stream()
                    .map(JoinRoomDto::getRoomId)
                    .collect(Collectors.toList());
            final List<Long> messageIds = joinRoomRepository.findAllWithMessageByUserIdAndRoomIdIn(userJpo.getUser_id().longValue(),
                            roomIds).stream()
                    .map(JoinRoom::getMessage)
                    .map(Message::getId)
                    .collect(Collectors.toList());

            setMemberAndLastMessageToJoinRoomDto(joinRoomDtos, roomIds, messageIds);
        }
        return joinRoomDtoPage;
    }

    @Override
    public Page<MessageDto> getChatMessages(Long roomId, Integer page) {
        final Long loginUserId = authUtil.getLoginUserId();
        page = (page == 0 ? 0 : page - 1);
        final Pageable pageable = PageRequest.of(page, 10);

        final JoinRoom joinRoom = joinRoomRepository.findWithRoomAndUserJpoByUserIdAndRoomId(loginUserId, roomId)
                .orElseThrow(JoinRoomNotFoundException::new);
        final Page<Message> messagePage = messageRepository.findAllByJoinRoom(joinRoom, pageable);

        final List<Message> messages = messagePage.getContent();
        final List<Long> messageIds = messages.stream()
                .map(Message::getId)
                .collect(Collectors.toList());

        final List<MessageDto> messageDtos = convertToDto(messages, messageIds);
//        setMessageLikesToMessageDto(messageIds, messageDtos);

        return new PageImpl<>(messageDtos, pageable, messagePage.getTotalElements());
    }

    @Override
    public ChatRoomInquireResponse inquireRoom(Long roomId) {
        final LocalDateTime now = LocalDateTime.now();
        final Long loginMember = authUtil.getLoginUserId();
        Optional<UserJpo> userJpoOptional = this.userRepository.findById( BigDecimal.valueOf(loginMember) );

        if( userJpoOptional.isPresent() ) {
            UserJpo loginUserJpo = userJpoOptional.get();
            final Room room = roomRepository.findById(roomId).orElseThrow(ChatRoomNotFoundException::new);
            final Map<Long, List<RoomUnreadMember>> roomUnreadMemberMap = roomUnreadMemberRepository.findAllByRoom(room)
                    .stream()
                    .collect(Collectors.groupingBy(r -> r.getUserJpo().getUser_id().longValue()));

            long unseenCount = 0;
            for (final Long id : roomUnreadMemberMap.keySet()) {
                if (!roomUnreadMemberMap.get(id).isEmpty()) {
                    unseenCount++;
                }
            }

            if (roomUnreadMemberRepository.findAllByRoomAndUserJpo(room, loginUserJpo).isEmpty()) {
                return new ChatRoomInquireResponse(false, unseenCount);
            }

            final List<JoinRoom> joinRooms = joinRoomRepository.findAllWithUserJpoByRoomId(roomId);
            final MessageSeenDto messageSeenDTO = new MessageSeenDto(room.getId(), loginUserJpo.getUser_id().longValue(), now);
            final MessageResponse response = new MessageResponse(MessageAction.MESSAGE_SEEN, messageSeenDTO);
            joinRooms.forEach(joinRoom -> {
                if (!joinRoom.getUserJpo().getUser_id().equals(loginUserJpo.getUser_id())) {
                    messagingTemplate.convertAndSend("/sub/" + joinRoom.getUserJpo().getUserName(), response);
                }
            });

            final List<RoomUnreadMember> roomUnreadMembers = roomUnreadMemberRepository.findAllByRoomAndUserJpo(room,
                    loginUserJpo);
            roomUnreadMemberRepository.deleteAllInBatch(roomUnreadMembers);

            return new ChatRoomInquireResponse(true, unseenCount - 1);
        }

        return null;
    }

    @Override
    public void sendMessage(MessageRequest request) {
        final UserJpo sender = userRepository.findById(BigDecimal.valueOf(request.getSenderId()))
                .orElseThrow(UserJpoDoesNotExistException::new);
        final Room room = roomRepository.findById(request.getRoomId()).orElseThrow(ChatRoomNotFoundException::new);
        final List<RoomMember> roomMembers = roomMemberRepository.findAllWithUserJpoByRoomId(room.getId());
        if (roomMembers.stream().noneMatch(r -> r.getUserJpo().getUser_id().equals(sender.getUser_id())))
            throw new JoinRoomNotFoundException();

        final MessageText message = messageTextRepository.save(new MessageText(
                request.getContent(), sender, room));
        message.setDtype();
        updateRoom(request.getSenderId(), room, roomMembers, message);

        final MessageResponse response = new MessageResponse(MessageAction.MESSAGE_GET, new MessageDto(
                message.getRoom().getId(), message.getId(), message.getUserJpo().toDomain(),
                message.getCreatedDate(), message.getDtype(), message.getContent()));
//        log.info("r.getUserJpo: {}", room.getUserJpo().getUserName());
        roomMembers.forEach(r -> messagingTemplate.convertAndSend("/sub/" + r.getUserJpo().getUserName(), response));
    }

    private void updateRoom(Long senderId, Room room, List<RoomMember> roomMembers, Message message) {
        final List<UserJpo> members = roomMembers.stream()
                .map(RoomMember::getUserJpo)
                .collect(Collectors.toList());
        final Map<Long, JoinRoom> joinRoomMap = joinRoomRepository.findByRoomAndUserJpoIn(room, members).stream()
                .collect(Collectors.toMap(
                        j -> j.getUserJpo().getUser_id().longValue(),
                        j -> j,
                        (existing, replacement) -> existing
                ));

        final List<JoinRoom> newJoinRooms = new ArrayList<>();
        final List<JoinRoom> updateJoinRooms = new ArrayList<>();
        final List<RoomUnreadMember> newRoomUnreadMembers = new ArrayList<>();

        for (final RoomMember roomMember : roomMembers) {
            final UserJpo member = roomMember.getUserJpo();
            if (!member.getUser_id().equals(senderId)) {
                newRoomUnreadMembers.add(new RoomUnreadMember(room, message, member));
            }
            if (joinRoomMap.containsKey(member.getUser_id().longValue())) {
                updateJoinRooms.add(joinRoomMap.get(member.getUser_id().longValue()));
            } else {
                newJoinRooms.add(new JoinRoom(room, member, message));
            }
        }

//        roomUnreadMemberRepository.saveAllBatch(newRoomUnreadMembers, message);
        joinRoomRepository.saveAllBatch(newJoinRooms, message);
        joinRoomRepository.updateAllBatch(updateJoinRooms, message);
    }

    private Optional<Room> getRoomByMembers(List<UserJpo> userJpos) {
        final Map<Long, List<RoomMember>> roomMembersMap = roomMemberRepository.findAllByUserJpoIn(userJpos)
                .stream()
                .collect(Collectors.groupingBy(r -> r.getRoom().getId()));

        final List<Long> roomIds = new ArrayList<>();
        roomMembersMap.forEach((rid, rms) -> {
            if (rms.size() == userJpos.size()) {
                roomIds.add(rid);
            }
        });
        final Map<Long, List<RoomMember>> roomMemberMapGroupByRoomId = roomMemberRepository.findAllByRoomIdIn(roomIds)
                .stream()
                .collect(Collectors.groupingBy(r -> r.getRoom().getId()));

        for (final Long roomId : roomMemberMapGroupByRoomId.keySet()) {
            if (roomMemberMapGroupByRoomId.get(roomId).size() == userJpos.size()) {
                return Optional.of(roomMemberMapGroupByRoomId.get(roomId).get(0).getRoom());
            }
        }

        return Optional.empty();
    }

    private void setMemberAndLastMessageToJoinRoomDto(List<JoinRoomDto> joinRoomDtos, List<Long> roomIds,
                                                      List<Long> messageIds) {
//        final Map<Long, MessagePost> messagePostMap = messagePostRepository.findAllWithPostByIdIn(messageIds)
//                .stream()
//                .collect(Collectors.toMap(mp -> mp.getRoom().getId(), mp -> mp));
//        final Map<Long, MessageImage> messageImageMap = messageImageRepository.findAllByIdIn(messageIds)
//                .stream()
//                .collect(Collectors.toMap(mi -> mi.getRoom().getId(), mi -> mi));
        final Map<Long, MessageText> messageTextMap = messageTextRepository.findAllByIdIn(messageIds)
                .stream()
                .collect(Collectors.toMap(
                        mt -> mt.getRoom().getId(),
                        mt -> mt,
                        (existing, replacement) -> existing // Merge Function: Always keep the 'existing' value
                        ));

        final Map<Long, List<RoomMember>> roomMembersMap = roomMemberRepository.findAllWithUserJpoByRoomIdIn(roomIds)
                .stream()
                .collect(Collectors.groupingBy(rm -> rm.getRoom().getId()));
        joinRoomDtos.forEach( joinRoomDto -> {
            joinRoomDto.setMembers(
                    roomMembersMap.get(joinRoomDto.getRoomId()).stream()
                            .map(r -> new MemberSimpleInfo( r.getUserJpo().toDomain() ))
                            .collect(Collectors.toList())
            );

//            if (messagePostMap.containsKey(joinRoomDto.getRoomId())) {
//                joinRoomDto.setLastMessage(new MessageDto(messagePostMap.get(joinRoomDto.getRoomId())));
//            } else if (messageImageMap.containsKey(joinRoomDto.getRoomId())) {
//                joinRoomDto.setLastMessage(new MessageDto(messageImageMap.get(joinRoomDto.getRoomId())));
//            } else
            if (messageTextMap.containsKey(joinRoomDto.getRoomId())) {
                MessageText mt = messageTextMap.get(joinRoomDto.getRoomId());
                joinRoomDto.setLastMessage(new MessageDto( mt.getRoom().getId(),
                        mt.getId(), mt.getUserJpo().toDomain(), mt.getCreatedDate(),
                        mt.getDtype(), mt.getContent()));
            }
        } );
    }

    private List<MessageDto> convertToDto(List<Message> messages, List<Long> messageIds) {
//        final Map<Long, MessageStory> messageStoryMap = messageStoryRepository.findAllWithStoryByIdIn(messageIds)
//                .stream()
//                .collect(Collectors.toMap(Message::getId, ms -> ms));
//        final Map<Long, MessagePost> messagePostMap = messagePostRepository.findAllWithPostByIdIn(messageIds)
//                .stream()
//                .collect(Collectors.toMap(Message::getId, mp -> mp));
//        final Map<Long, MessageImage> messageImageMap = messageImageRepository.findAllByIdIn(messageIds)
//                .stream()
//                .collect(Collectors.toMap(Message::getId, mi -> mi));
        final Map<Long, MessageText> messageTextMap = messageTextRepository.findAllByIdIn(messageIds)
                .stream()
                .collect(Collectors.toMap(Message::getId, mt -> mt));

        return messages.stream()
                .map(m -> {
                    switch (m.getDtype()) {
//                        case "POST":
//                            return new MessageDto(messagePostMap.get(m.getId()));
//                        case "STORY":
//                            return new MessageDto(messageStoryMap.get(m.getId()));
//                        case "IMAGE":
//                            return new MessageDto(messageImageMap.get(m.getId()));
                        case "TEXT":
                            MessageText mt = messageTextMap.get(m.getId());
                            return new MessageDto( mt.getRoom().getId(), mt.getId(),
                                    mt.getUserJpo().toDomain(),
                                    mt.getCreatedDate(),
                                    mt.getDtype(),
                                    mt.getContent()
                                    );
                    }
                    return null;
                })
                .collect(Collectors.toList());
    }
}
