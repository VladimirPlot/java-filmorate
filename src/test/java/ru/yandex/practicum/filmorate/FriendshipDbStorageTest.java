package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({FriendshipDbStorage.class, UserDbStorage.class})
public class FriendshipDbStorageTest {

    @Autowired
    private FriendshipDbStorage friendshipStorage;

    @Autowired
    private UserDbStorage userStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private User user1;
    private User user2;

    @BeforeEach
    public void setup() {
        jdbcTemplate.update("DELETE FROM friendships");
        jdbcTemplate.update("DELETE FROM users");

        user1 = userStorage.addUser(new User(0, "user1@example.com", "user1", "User One", LocalDate.of(1990, 1, 1)));
        user2 = userStorage.addUser(new User(0, "user2@example.com", "user2", "User Two", LocalDate.of(1992, 2, 2)));
    }

    @Test
    public void testAddAndConfirmFriendship() {
        friendshipStorage.addFriend(user1.getId(), user2.getId());

        assertThat(friendshipStorage.getFriends(user1.getId()))
                .containsExactly(user2.getId());

        assertThat(friendshipStorage.getFriends(user2.getId()))
                .isEmpty();
    }

    @Test
    public void testRemoveConfirmedFriend() {
        friendshipStorage.addFriend(user1.getId(), user2.getId());

        friendshipStorage.removeFriend(user1.getId(), user2.getId());

        assertThat(friendshipStorage.getFriends(user1.getId())).isEmpty();
        assertThat(friendshipStorage.getFriends(user2.getId())).isEmpty();
    }
}