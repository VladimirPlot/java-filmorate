package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("friendshipDbStorage") FriendshipStorage friendshipStorage) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
    }

    private void checkUserExistence(int userId, String role) {
        userStorage.getUser(userId)
                .orElseThrow(() -> new UserNotFoundException(role + " с id " + userId + " не найден"));
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        checkUserExistence(user.getId(), "Пользователь");
        return userStorage.updateUser(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public Set<User> addFriend(int userId, int friendId) {
        if (userId == friendId) {
            throw new ValidationException("Нельзя добавить самого себя в друзья.");
        }

        checkUserExistence(userId, "Пользователь");
        checkUserExistence(friendId, "Друг");

        friendshipStorage.addFriend(userId, friendId);

        return getFriends(userId);
    }

    public Set<User> removeFriend(int userId, int friendId) {
        checkUserExistence(userId, "Пользователь");
        checkUserExistence(friendId, "Друг");

        friendshipStorage.removeFriend(userId, friendId);

        return getFriends(userId);
    }

    public Set<User> getFriends(int userId) {
        checkUserExistence(userId, "Пользователь");
        return friendshipStorage.getFriends(userId).stream()
                .map(id -> userStorage.getUser(id)
                        .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + id + " не найден")))
                .collect(Collectors.toSet());
    }

    public Set<User> getCommonFriends(int id, int otherId) {
        checkUserExistence(id, "Пользователь");
        checkUserExistence(otherId, "Пользователь");

        Set<Integer> commonFriendIds = friendshipStorage.getCommonFriends(id, otherId);

        return commonFriendIds.stream()
                .map(friendId -> userStorage.getUser(friendId)
                        .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + friendId + " не найден")))
                .collect(Collectors.toSet());
    }

    public Set<User> getPendingFriendRequests(int userId) {
        checkUserExistence(userId, "Пользователь");

        Set<Integer> pendingIds = friendshipStorage.getPendingRequestsFor(userId);

        return pendingIds.stream()
                .map(id -> userStorage.getUser(id)
                        .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + id + " не найден")))
                .collect(Collectors.toSet());
    }

    public User getUserById(int id) {
        return userStorage.getUser(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + id + " не найден"));
    }
}