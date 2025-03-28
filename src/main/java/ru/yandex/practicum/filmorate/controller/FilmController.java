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
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final List<Film> films = new ArrayList<>();
    private int nextId = 1;

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
        film.setId(nextId++);
        log.info("Создание фильма: {}", film);
        films.add(film);
        return film;
    }

    @PutMapping
    public ResponseEntity<?> updateFilm(@RequestBody @Valid Film film, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.append(error.getDefaultMessage()).append("; ");
            }
            log.error("Ошибка валидации: {}", errors);
            throw new ValidationException(errors.toString());
        }

        Optional<Film> existingFilmOpt = films.stream()
                .filter(f -> f.getId() == film.getId())
                .findFirst();

        if (existingFilmOpt.isEmpty()) {
            log.error("Фильм с ID {} не найден", film.getId());
            String errorMessage = "Фильм с ID " + film.getId() + " не найден.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(errorMessage));
        }

        Film existingFilm = existingFilmOpt.get();

        existingFilm.setName(film.getName());
        existingFilm.setDescription(film.getDescription());
        existingFilm.setReleaseDate(film.getReleaseDate());
        existingFilm.setDuration(film.getDuration());

        log.info("Фильм с ID {} обновлен: {}", film.getId(), existingFilm);
        return ResponseEntity.status(HttpStatus.OK).body(existingFilm);
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Получение списка всех фильмов");
        return films;
    }
}