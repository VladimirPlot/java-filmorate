package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(GenreDbStorage.class)
public class GenreDbStorageTest {

    @Autowired
    private GenreDbStorage genreStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void insertTestGenres() {
        jdbcTemplate.update("""
                    INSERT INTO genres (id, name) VALUES
                    (1, 'Комедия'),
                    (2, 'Драма'),
                    (3, 'Мультфильм'),
                    (4, 'Триллер'),
                    (5, 'Документальный'),
                    (6, 'Боевик')
                """);
    }

    @Test
    public void testGetGenreById() {
        Optional<Genre> genre = genreStorage.getGenreById(6);

        assertThat(genre)
                .isPresent()
                .hasValueSatisfying(g ->
                        assertThat(g.getName()).isEqualTo("Боевик")
                );
    }

    @Test
    public void testGetAllGenres() {
        List<Genre> genres = genreStorage.getAllGenres();

        assertThat(genres)
                .isNotEmpty()
                .anyMatch(g -> g.getName().equals("Комедия"));
    }
}