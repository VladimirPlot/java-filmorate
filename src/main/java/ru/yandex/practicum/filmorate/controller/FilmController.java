package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable int id) {
        return filmService.getFilm(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Получение топ-{} популярных фильмов", count);
        return filmService.getPopularFilms(count);
    }

    @PostMapping
    public Film createFilm(@RequestBody @Valid Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@RequestBody @Valid Film film) {
        return ResponseEntity.ok(filmService.updateFilm(film));
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeFilm(@PathVariable int id, @PathVariable int userId) {
        filmService.likeFilm(id, userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void unlikeFilm(@PathVariable int id, @PathVariable int userId) {
        filmService.unlikeFilm(id, userId);
        log.info("Пользователь {} убрал лайк с фильма {}", userId, id);
    }
}