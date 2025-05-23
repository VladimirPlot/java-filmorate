package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
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