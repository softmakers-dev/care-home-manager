package com.softmakers.manager_store.repository.dm.jdbc;

import com.softmakers.manager_store.jpo.UserJpo;
import com.softmakers.manager_store.jpo.dm.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
public class RoomMemberRepositoryJdbcImpl implements RoomMemberRepositoryJdbc {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveAllBatch(Room room, List<UserJpo> userJpos) {
        final String sql = "INSERT INTO room_members (`user_id`, `room_id`) VALUES(?, ?)";

        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, userJpos.get(i).getUser_id().toString());
                        ps.setString(2, room.getId().toString());
                    }

                    @Override
                    public int getBatchSize() {
                        return userJpos.size();
                    }
                });
    }
}
