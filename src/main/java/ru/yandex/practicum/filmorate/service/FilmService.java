    package ru.yandex.practicum.filmorate.service;

    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;
    import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
    import ru.yandex.practicum.filmorate.exception.IllegalArgumentException;
    import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
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
    @RequiredArgsConstructor
    public class FilmService {
        private final FilmStorage filmStorage;
        private final UserStorage userStorage;
        private final MpaStorage mpaStorage;
        private final GenreStorage genreStorage;

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
                throw new IllegalArgumentException("Некорректный рейтинг MPA");
            }
        }

        public Film addFilm(Film film) {
            validateMpa(film);
            enrichWithMpaName(film);
            return filmStorage.addFilm(film);
        }

        public void likeFilm(int filmId, int userId) {
            checkFilmExistence(filmId);
            checkUserExistence(userId);

            Film film = filmStorage.getFilm(filmId).get();
            film.getLikes().add(userId);
            filmStorage.updateFilm(film);
        }

        public void unlikeFilm(int filmId, int userId) {
            checkFilmExistence(filmId);
            checkUserExistence(userId);

            Film film = filmStorage.getFilm(filmId).get();
            film.getLikes().remove(userId);
            filmStorage.updateFilm(film);
        }

        public Film updateFilm(Film film) {
            checkFilmExistence(film.getId());
            validateMpa(film);
            enrichWithMpaName(film);
            return filmStorage.updateFilm(film);
        }

        public List<Film> getAllFilms() {
            return filmStorage.getAllFilms();
        }

        public List<Film> getPopularFilms(int count) {
            if (count < 1) {
                throw new IllegalArgumentException("Количество фильмов должно быть положительным числом");
            }
            return filmStorage.getAllFilms().stream()
                    .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                    .limit(count)
                    .collect(Collectors.toList());
        }

        private void enrichWithMpaName(Film film) {
            int mpaId = film.getMpa().getId();
            MpaRating fullMpa = mpaStorage.getMpaById(mpaId)
                    .orElseThrow(() -> new IllegalArgumentException("Некорректный рейтинг MPA с id = " + mpaId));
            film.setMpa(fullMpa);
        }

        private void enrichWithGenres(Film film) {
            if (film.getGenres() == null) return;

            Set<Genre> enrichedGenres = film.getGenres().stream()
                    .map(g -> genreStorage.getGenreById(g.getId())
                            .orElseThrow(() -> new IllegalArgumentException("Жанр с id " + g.getId() + " не найден")))
                    .collect(Collectors.toCollection(TreeSet::new));

            film.setGenres(enrichedGenres);
        }
    }