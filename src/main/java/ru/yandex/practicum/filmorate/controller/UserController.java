package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.errors.ErrorResponse;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final List<User> users = new ArrayList<>();

    private boolean isEmailAlreadyTaken(String email) {
        return users.stream().anyMatch(user -> user.getEmail().equals(email));
    }

    private boolean isIdAlreadyTaken(int id) {
        return users.stream().anyMatch(user -> user.getId() == id);
    }

    @PostMapping
    public User createUser(@RequestBody @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.append(error.getDefaultMessage()).append("; ");
            }
            log.error("Ошибка валидации: {}", errors);
            throw new ValidationException(errors.toString());
        }

        if (isEmailAlreadyTaken(user.getEmail())) {
            log.error("Email {} уже занят", user.getEmail());
            throw new ValidationException("Email уже занят");
        }

        int userId = users.size() + 1;
        user.setId(userId);

        log.info("Создание пользователя: {}", user);
        users.add(user);
        return user;
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.append(error.getDefaultMessage()).append("; ");
            }
            log.error("Ошибка валидации: {}", errors);
            throw new ValidationException(errors.toString());
        }

        Optional<User> existingUserOpt = users.stream()
                .filter(u -> u.getId() == user.getId())
                .findFirst();

        if (existingUserOpt.isEmpty()) {
            log.error("Пользователь с ID {} не найден", user.getId());
            String errorMessage = "Пользователь с ID " + user.getId() + " не найден.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(errorMessage));
        }

        User existingUser = existingUserOpt.get();

        existingUser.setLogin(user.getLogin());
        existingUser.setEmail(user.getEmail());
        existingUser.setBirthday(user.getBirthday());
        existingUser.setName(user.getName());

        log.info("Пользователь с ID {} обновлен: {}", user.getId(), existingUser);
        return ResponseEntity.status(HttpStatus.OK).body(existingUser);
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Получение списка всех пользователей");
        return users;
    }
}