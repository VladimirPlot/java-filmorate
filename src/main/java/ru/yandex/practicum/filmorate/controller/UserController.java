package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    private boolean isEmailAlreadyTaken(String email) {
        return users.values().stream().anyMatch(user -> user.getEmail().equals(email));
    }

    private boolean isIdAlreadyTaken(int id) {
        return users.containsKey(id);
    }

    @PostMapping
    public User createUser(@RequestBody @Valid User user) {
        if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            log.error("Email {} уже занят", user.getEmail());
            throw new DuplicatedDataException("Email уже занят");
        }

        user.setId(nextId++);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        users.put(user.getId(), user);
        log.info("Создан пользователь: {}", user);
        return user;
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@RequestBody @Valid User user) {
        if (!users.containsKey(user.getId())) {
            log.error("Пользователь с ID {} не найден", user.getId());
            throw new NoSuchElementException("Пользователь с ID " + user.getId() + " не найден.");
        }

        users.put(user.getId(), user);
        log.info("Пользователь с ID {} обновлён: {}", user.getId(), user);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Получение списка всех пользователей");
        return List.copyOf(users.values());
    }
}