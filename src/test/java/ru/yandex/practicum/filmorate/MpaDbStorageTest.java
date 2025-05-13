package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(MpaDbStorage.class)
public class MpaDbStorageTest {

    @Autowired
    private MpaDbStorage mpaStorage;

    @Test
    public void testGetMpaById() {
        Optional<MpaRating> mpa = mpaStorage.getMpaById(1);

        assertThat(mpa)
                .isPresent()
                .hasValueSatisfying(rating ->
                        assertThat(rating.getName()).isEqualTo("G")
                );
    }

    @Test
    public void testGetAllMpa() {
        List<MpaRating> all = mpaStorage.getAllMpa();

        assertThat(all)
                .isNotEmpty()
                .anyMatch(rating -> rating.getName().equals("PG"));
    }
}