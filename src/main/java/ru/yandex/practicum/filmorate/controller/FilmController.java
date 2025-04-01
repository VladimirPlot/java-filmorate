package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int nextId = 1;

    @PostMapping
    public Film createFilm(@RequestBody @Valid Film film) {
        film.setId(nextId++);
        films.put(film.getId(), film);
        log.info("Создание фильма: {}", film);
        return film;
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@RequestBody @Valid Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Фильм с ID {} не найден", film.getId());
            throw new NoSuchElementException("Фильм с ID " + film.getId() + " не найден.");
        }

        films.put(film.getId(), film);
        log.info("Фильм с ID {} обновлён: {}", film.getId(), film);
        return ResponseEntity.ok(film);
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Получение списка всех фильмов");
        return List.copyOf(films.values());
    }
}