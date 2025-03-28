package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final List<Film> films = new ArrayList<>();

    @PostMapping
    public Film createFilm(@RequestBody @Valid Film film, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.append(error.getDefaultMessage()).append("; ");
            }
            log.error("Ошибка валидации: {}", errors);
            throw new ValidationException(errors.toString());
        }
        log.info("Создание фильма: {}", film);
        films.add(film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.append(error.getDefaultMessage()).append("; ");
            }
            log.error("Ошибка валидации: {}", errors);
            throw new ValidationException(errors.toString());
        }
        log.info("Обновление фильма: {}", film);
        films.add(film);
        return film;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Получение списка всех фильмов");
        return films;
    }
}