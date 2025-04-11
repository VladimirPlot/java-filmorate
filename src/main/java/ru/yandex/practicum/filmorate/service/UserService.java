package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EmailAlreadyTakenException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    private boolean isEmailAlreadyTaken(String email) {
        return userStorage.getAllUsers().stream().anyMatch(user -> user.getEmail().equals(email));
    }

    private boolean isIdAlreadyTaken(int id) {
        return userStorage.getAllUsers().stream().anyMatch(user -> user.getId() == id);
    }

    private void checkUserExistence(int userId, String role) {
        Optional<User> user = userStorage.getUser(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException(role + " с id " + userId + " не найден");
        }
    }

    public User addUser(User user) {
        if (isEmailAlreadyTaken(user.getEmail())) {
            throw new EmailAlreadyTakenException("Email уже занят");
        }

        if (isIdAlreadyTaken(user.getId())) {
            throw new UserAlreadyExistsException("ID уже занят");
        }

        return userStorage.addUser(user);
    }

    public Set<User> addFriend(int userId, int friendId) {
        checkUserExistence(userId, "Пользователь");
        checkUserExistence(friendId, "Друг");

        User user = userStorage.getUser(userId).get();
        User friend = userStorage.getUser(friendId).get();

        user.getFriends().add(friend);
        friend.getFriends().add(user);

        userStorage.updateUser(user);
        userStorage.updateUser(friend);

        return user.getFriends();
    }

    public Set<User> removeFriend(int userId, int friendId) {
        checkUserExistence(userId, "Пользователь");
        checkUserExistence(friendId, "Друг");

        User user = userStorage.getUser(userId).get();
        User friend = userStorage.getUser(friendId).get();

        user.getFriends().remove(friend);
        friend.getFriends().remove(user);

        userStorage.updateUser(user);
        userStorage.updateUser(friend);

        return user.getFriends();
    }

    public User updateUser(User user) {
        checkUserExistence(user.getId(), "Пользователь");

        return userStorage.updateUser(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public Set<User> getFriends(int id) {
        checkUserExistence(id, "Пользователь");
        return userStorage.getUser(id).get().getFriends();
    }

    public Set<User> getCommonFriends(int id, int otherId) {
        checkUserExistence(id, "Пользователь");
        checkUserExistence(otherId, "Пользователь");

        Set<User> userFriends = userStorage.getUser(id).get().getFriends();
        Set<User> otherUserFriends = userStorage.getUser(otherId).get().getFriends();

        userFriends.retainAll(otherUserFriends);
        return userFriends;
    }
}