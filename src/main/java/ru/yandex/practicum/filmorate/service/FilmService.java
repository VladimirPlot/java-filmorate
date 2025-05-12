package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.exception.IllegalArgumentException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("mpaDbStorage") MpaStorage mpaStorage,
                       @Qualifier("genreDbStorage") GenreStorage genreStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    private void checkFilmExistence(int filmId) {
        filmStorage.getFilm(filmId)
                .orElseThrow(() -> new FilmNotFoundException("Фильм с id " + filmId + " не найден"));
    }

    private void checkUserExistence(int userId) {
        userStorage.getUser(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + userId + " не найден"));
    }

    private void validateMpa(Film film) {
        if (film.getMpa() == null || film.getMpa().getId() == 0
                || mpaStorage.getMpaById(film.getMpa().getId()).isEmpty()) {
            throw new MpaNotFoundException("Некорректный рейтинг MPA");
        }
    }

    private void validateGenres(Film film) {
        if (film.getGenres() == null) return;

        for (Genre genre : film.getGenres()) {
            if (genreStorage.getGenreById(genre.getId()).isEmpty()) {
                throw new GenreNotFoundException("Жанр с id " + genre.getId() + " не найден");
            }
        }
    }

    public Film addFilm(Film film) {
        validateMpa(film);
        validateGenres(film); // ← ДО сохранения
        Film saved = filmStorage.addFilm(film);
        enrichWithMpaName(saved);
        enrichWithGenres(saved);
        return saved;
    }

    public void likeFilm(int filmId, int userId) {
        checkFilmExistence(filmId);
        checkUserExistence(userId);

        filmStorage.addLike(filmId, userId);
    }

    public void unlikeFilm(int filmId, int userId) {
        checkFilmExistence(filmId);
        checkUserExistence(userId);

        filmStorage.removeLike(filmId, userId);
    }

    public Film updateFilm(Film film) {
        checkFilmExistence(film.getId());
        validateMpa(film);

        Film updated = filmStorage.updateFilm(film);

        enrichWithMpaName(updated);
        enrichWithGenres(updated);

        return updated;
    }

    public List<Film> getAllFilms() {
        List<Film> films = filmStorage.getAllFilms();
        films.forEach(film -> {
            enrichWithMpaName(film);
        });
        return films;
    }

    public Film getFilm(int id) {
        Film film = filmStorage.getFilm(id)
                .orElseThrow(() -> new FilmNotFoundException("Фильм с id " + id + " не найден"));

        enrichWithMpaName(film);
        enrichWithGenres(film);

        return film;
    }

    public List<Film> getPopularFilms(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Количество фильмов должно быть положительным числом");
        }

        List<Film> films = filmStorage.getPopularFilms(count);
        films.forEach(this::enrichWithMpaName);
        films.forEach(this::enrichWithGenres);
        return films;
    }

    private void enrichWithMpaName(Film film) {
        int mpaId = film.getMpa().getId();
        MpaRating fullMpa = mpaStorage.getMpaById(mpaId)
                .orElseThrow(() -> new MpaNotFoundException("Некорректный рейтинг MPA с id = " + mpaId));
        film.setMpa(fullMpa);
    }

    private void enrichWithGenres(Film film) {
        if (film.getGenres() == null) return;

        Set<Genre> enrichedGenres = film.getGenres().stream()
                .map(g -> genreStorage.getGenreById(g.getId())
                        .orElseThrow(() -> new GenreNotFoundException("Жанр с id " + g.getId() + " не найден")))
                .collect(Collectors.toCollection(TreeSet::new));

        film.setGenres(enrichedGenres);
    }
}