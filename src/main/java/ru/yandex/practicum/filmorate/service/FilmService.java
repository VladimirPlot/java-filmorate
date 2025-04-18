    package ru.yandex.practicum.filmorate.service;

    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;
    import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
    import ru.yandex.practicum.filmorate.exception.IllegalArgumentException;
    import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
    import ru.yandex.practicum.filmorate.model.Film;
    import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
    import ru.yandex.practicum.filmorate.storage.user.UserStorage;

    import java.util.List;
    import java.util.stream.Collectors;

    @Service
    @RequiredArgsConstructor
    public class FilmService {
        private final FilmStorage filmStorage;
        private final UserStorage userStorage;

        private void checkFilmExistence(int filmId) {
            filmStorage.getFilm(filmId)
                    .orElseThrow(() -> new FilmNotFoundException("Фильм с id " + filmId + " не найден"));
        }

        private void checkUserExistence(int userId) {
            userStorage.getUser(userId)
                    .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + userId + " не найден"));
        }

        public Film addFilm(Film film) {
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
    }