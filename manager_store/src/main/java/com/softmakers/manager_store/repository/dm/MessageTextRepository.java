package com.softmakers.manager_store.repository.dm;

import com.softmakers.manager_store.jpo.dm.MessageText;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageTextRepository extends JpaRepository<MessageText, Long> {
    @Query("select mt from MessageText mt where mt.id in :messageIds")
    List<MessageText> findAllByIdIn(@Param("messageIds") List<Long> messageIds);

}
