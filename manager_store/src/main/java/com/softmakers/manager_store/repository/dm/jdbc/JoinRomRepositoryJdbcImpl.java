package com.softmakers.manager_store.repository.dm.jdbc;

import com.softmakers.manager_store.jpo.dm.JoinRoom;
import com.softmakers.manager_store.jpo.dm.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
public class JoinRomRepositoryJdbcImpl implements JoinRomRepositoryJdbc {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveAllBatch(List<JoinRoom> joinRooms, Message message) {
        final String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        final String sql =
                "INSERT INTO join_rooms (`createdAt`, `user_id`, `message_id`, `room_id`) " +
                        "VALUES(?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, now);
                        ps.setString(2, joinRooms.get(i).getUserJpo().getUser_id().toString());
                        ps.setString(3, message.getId().toString());
                        ps.setString(4, joinRooms.get(i).getRoom().getId().toString());
                    }

                    @Override
                    public int getBatchSize() {
                        return joinRooms.size();
                    }
                });
    }

    @Override
    public void updateAllBatch(List<JoinRoom> updateJoinRooms, Message message) {
        final String sql = "UPDATE join_rooms SET message_id = ? where id = ?";

        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, message.getId().toString());
                        ps.setString(2, updateJoinRooms.get(i).getId().toString());
                    }

                    @Override
                    public int getBatchSize() {
                        return updateJoinRooms.size();
                    }
                });
    }
}

