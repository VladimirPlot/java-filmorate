package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {

    private final GenreStorage genreStorage;

    public GenreController(@Qualifier("genreDbStorage") GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    @GetMapping
    public List<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable int id) {
        return genreStorage.getGenreById(id)
                .orElseThrow(() -> new GenreNotFoundException("Жанр с id " + id + " не найден"));
    }
}