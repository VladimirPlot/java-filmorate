package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film addFilm(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_rating_id) VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setObject(5, film.getMpa() != null ? film.getMpa().getId() : null);
            return stmt;
        }, keyHolder);

        int filmId = keyHolder.getKey().intValue();
        film.setId(filmId);

        if (film.getGenres() != null) {
            String genreSql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            film.getGenres().forEach(genre ->
                    jdbcTemplate.update(genreSql, filmId, genre.getId())
            );
        }

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId()
        );

        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());

        if (film.getGenres() != null) {
            String insertGenreSql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            film.getGenres().forEach(genre ->
                    jdbcTemplate.update(insertGenreSql, film.getId(), genre.getId()));
        }

        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToFilm(rs));
    }

    @Override
    public Optional<Film> getFilm(int id) {
        String sql = "SELECT * FROM films WHERE id = ?";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToFilm(rs), id);
        return films.stream().findFirst();
    }

    @Override
    public void addLike(int filmId, int userId) {
        String sql = """
                    MERGE INTO film_likes (film_id, user_id)
                    KEY (film_id, user_id)
                    VALUES (?, ?)
                """;
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = """
                SELECT f.*, COUNT(fl.user_id) AS like_count
                FROM films f
                LEFT JOIN film_likes fl ON f.id = fl.film_id
                GROUP BY f.id
                ORDER BY like_count DESC, f.id ASC
                LIMIT ?
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToFilm(rs), count);
    }

    private Film mapRowToFilm(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        String likeSql = "SELECT user_id FROM film_likes WHERE film_id = ?";
        Set<Integer> likes = new HashSet<>(jdbcTemplate.query(
                likeSql, (likeRs, rowNum) -> likeRs.getInt("user_id"), film.getId()));
        film.setLikes(likes);

        int mpaId = rs.getInt("mpa_rating_id");
        if (mpaId != 0) {
            film.setMpa(new MpaRating(mpaId, null));
        }

        String genreSql = "SELECT g.id, g.name FROM film_genres fg JOIN genres g ON fg.genre_id = g.id WHERE fg.film_id = ?";
        List<Genre> genres = jdbcTemplate.query(genreSql,
                (genreRs, rowNum) -> new Genre(genreRs.getInt("id"), genreRs.getString("name")),
                film.getId());
        film.setGenres(new TreeSet<>(genres));

        return film;
    }
}