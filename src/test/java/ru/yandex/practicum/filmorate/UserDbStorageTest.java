package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(UserDbStorage.class)
public class UserDbStorageTest {

    @Autowired
    private UserDbStorage userDbStorage;

    @Test
    public void testAddAndGetUser() {
        User user = new User(0, "test@example.com", "testuser", "Test User", LocalDate.of(1990, 1, 1));
        User saved = userDbStorage.addUser(user);

        Optional<User> optionalUser = userDbStorage.getUser(saved.getId());

        assertThat(optionalUser)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u)
                                .hasFieldOrPropertyWithValue("id", saved.getId())
                                .hasFieldOrPropertyWithValue("email", "test@example.com")
                                .hasFieldOrPropertyWithValue("login", "testuser")
                );
    }
}