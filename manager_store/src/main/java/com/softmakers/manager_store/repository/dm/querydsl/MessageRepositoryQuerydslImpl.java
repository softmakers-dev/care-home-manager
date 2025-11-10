package com.softmakers.manager_store.repository.dm.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.softmakers.manager_store.jpo.dm.JoinRoom;
import com.softmakers.manager_store.jpo.dm.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static com.softmakers.manager_store.jpo.dm.QMessage.message;

@RequiredArgsConstructor
public class MessageRepositoryQuerydslImpl implements MessageRepositoryQuerydsl{
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Message> findAllByJoinRoom(JoinRoom joinRoom, Pageable pageable) {
        final List<Message> messages = queryFactory
                .selectFrom(message)
                .where(isMessageByRoomIdAndAfter(joinRoom.getRoom().getId(), joinRoom.getCreatedDate()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(message.id.desc())
                .fetch();

        final long total = queryFactory
                .selectFrom(message)
                .where(isMessageByRoomIdAndAfter(joinRoom.getRoom().getId(), joinRoom.getCreatedDate()))
                .fetchCount();

        return new PageImpl<>(messages, pageable, total);
    }

    private BooleanExpression isMessageByRoomIdAndAfter(Long roomId, LocalDateTime joinedDate) {
        return message.room.id.eq(roomId).and(message.createdDate.goe(joinedDate));
    }
}
