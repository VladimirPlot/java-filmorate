package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final List<User> users = new ArrayList<>();

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
        log.info("Создание пользователя: {}", user);
        users.add(user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.append(error.getDefaultMessage()).append("; ");
            }
            log.error("Ошибка валидации: {}", errors);
            throw new ValidationException(errors.toString());
        }
        log.info("Обновление пользователя: {}", user);
        users.add(user);
        return user;
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Получение списка всех пользователей");
        return users;
    }
}