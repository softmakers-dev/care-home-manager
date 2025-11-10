package com.softmakers.manager_store.repository.dm.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.softmakers.manager_domain.entity.dto.dm.JoinRoomDto;
import com.softmakers.manager_domain.entity.dto.dm.QJoinRoomDto;
import com.softmakers.manager_store.jpo.dm.JoinRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.softmakers.manager_store.jpo.QUserJpo.userJpo;
import static com.softmakers.manager_store.jpo.dm.QJoinRoom.joinRoom;
import static com.softmakers.manager_store.jpo.dm.QMessage.message;
import static com.softmakers.manager_store.jpo.dm.QRoom.room;
import static com.softmakers.manager_store.jpo.dm.QRoomUnreadMember.roomUnreadMember;

@RequiredArgsConstructor
public class JoinRoomRepositoryQuerydslImpl implements JoinRoomRepositoryQuerydsl{
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<JoinRoom> findWithRoomAndUserJpoByUserIdAndRoomId(Long memberId, Long roomId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(joinRoom)
                .where(joinRoom.userJpo.user_id.eq(BigDecimal.valueOf(memberId)).and(joinRoom.room.id.eq(roomId)))
                .innerJoin(joinRoom.room, room).fetchJoin()
                .innerJoin(joinRoom.userJpo, userJpo).fetchJoin()
//                .fetchOne());
                .fetchFirst()); // <- Changes to fetch the first result and ignore the rest
    }

    @Override
    public List<JoinRoom> findAllWithMessageByUserIdAndRoomIdIn(Long memberId, List<Long> roomIds) {
        return queryFactory
                .selectFrom(joinRoom)
                .innerJoin(joinRoom.message, message)
                .where(joinRoom.userJpo.user_id.eq(BigDecimal.valueOf(memberId)).and(
                        joinRoom.room.id.in(roomIds)
                ))
                .fetch();
    }

    @Override
    public Page<JoinRoomDto> findJoinRoomDtoPageByUserId(Long memberId, Pageable pageable) {
        final List<JoinRoomDto> joinRoomDtos = queryFactory
                .select(new QJoinRoomDto(
                        joinRoom.room.id,
                        isUnread(memberId),
                        joinRoom.room.userJpo.user_id,
                        joinRoom.room.userJpo.userName
//                        joinRoom.room.userJpo.image.imageUrl
                ))
                .from(joinRoom)
                .innerJoin(joinRoom.room, room)
                .innerJoin(joinRoom.message, message)
                .innerJoin(joinRoom.room.userJpo, userJpo)
                .where(joinRoom.userJpo.user_id.eq(BigDecimal.valueOf(memberId)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(joinRoom.message.createdDate.desc())
                .fetch();

        final long total = queryFactory
                .selectFrom(joinRoom)
                .where(joinRoom.userJpo.user_id.eq(BigDecimal.valueOf(memberId)))
                .fetchCount();

        return new PageImpl<>(joinRoomDtos, pageable, total);
    }

    private BooleanExpression isUnread(Long memberId) {
        return JPAExpressions
                .selectFrom(roomUnreadMember)
                .where(
                        roomUnreadMember.userJpo.user_id.eq(BigDecimal.valueOf(memberId)).and(
                                roomUnreadMember.room.eq(joinRoom.room)
                        )
                )
                .exists();
    }
}
