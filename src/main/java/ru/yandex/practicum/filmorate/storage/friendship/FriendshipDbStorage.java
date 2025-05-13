package ru.yandex.practicum.filmorate.storage.friendship;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class FriendshipDbStorage implements FriendshipStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(int userId, int friendId) {
        String checkSql = "SELECT COUNT(*) FROM friendships WHERE user_id = ? AND friend_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, userId, friendId);

        if (count != null && count == 0) {
            jdbcTemplate.update("INSERT INTO friendships (user_id, friend_id, status) VALUES (?, ?, 'CONFIRMED')",
                    userId, friendId);
        }
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        String getStatusSql = "SELECT status FROM friendships WHERE user_id = ? AND friend_id = ?";
        List<String> statuses = jdbcTemplate.query(getStatusSql,
                (rs, rowNum) -> rs.getString("status"), userId, friendId);

        if (statuses.isEmpty()) {
            return;
        }

        String status = statuses.get(0);

        jdbcTemplate.update("DELETE FROM friendships WHERE user_id = ? AND friend_id = ?", userId, friendId);

        if (status.equals("CONFIRMED")) {
            String updateBack = "UPDATE friendships SET status = 'PENDING' WHERE user_id = ? AND friend_id = ?";
            jdbcTemplate.update(updateBack, friendId, userId);
        }
    }

    @Override
    public Set<Integer> getFriends(int userId) {
        String sql = "SELECT friend_id FROM friendships WHERE user_id = ? AND status = 'CONFIRMED'";
        List<Integer> ids = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("friend_id"), userId);
        return new HashSet<>(ids);
    }

    @Override
    public Set<Integer> getCommonFriends(int userId, int otherId) {
        String sql = """
                    SELECT f1.friend_id
                    FROM friendships f1
                    JOIN friendships f2 ON f1.friend_id = f2.friend_id
                    WHERE f1.user_id = ? AND f2.user_id = ? AND f1.status = 'CONFIRMED' AND f2.status = 'CONFIRMED'
                """;
        List<Integer> ids = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("friend_id"), userId, otherId);
        return new HashSet<>(ids);
    }

    @Override
    public Set<Integer> getPendingRequestsFor(int userId) {
        String sql = """
                    SELECT user_id
                    FROM friendships
                    WHERE friend_id = ? AND status = 'PENDING'
                """;
        List<Integer> ids = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("user_id"), userId);
        return new HashSet<>(ids);
    }
}