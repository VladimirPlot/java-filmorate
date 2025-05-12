package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository("genreDbStorage")
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Genre> getGenreById(int id) {
        String sql = "SELECT * FROM genres WHERE id = ?";
        List<Genre> genres = jdbcTemplate.query(sql,
                (rs, rowNum) -> new Genre(rs.getInt("id"), rs.getString("name")), id);
        return genres.stream().findFirst();
    }

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM genres";
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new Genre(rs.getInt("id"), rs.getString("name")));
    }
}