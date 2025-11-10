package com.softmakers.manager_store.repository.dm.querydsl;

import com.softmakers.manager_domain.entity.dto.dm.JoinRoomDto;
import com.softmakers.manager_store.jpo.dm.JoinRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface JoinRoomRepositoryQuerydsl {

    Optional<JoinRoom> findWithRoomAndUserJpoByUserIdAndRoomId(Long memberId, Long roomId);

    List<JoinRoom> findAllWithMessageByUserIdAndRoomIdIn(Long memberId, List<Long> roomIds);

    Page<JoinRoomDto> findJoinRoomDtoPageByUserId(Long memberId, Pageable pageable);
}
