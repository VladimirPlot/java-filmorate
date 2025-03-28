package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmValidationTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void testValidFilm() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("This is a valid film description.");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Film should be valid");
    }

    @Test
    public void testInvalidReleaseDate() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1890, 11, 11));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Release date should be after 28 Dec 1895");
    }

    @Test
    public void testEmptyName() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Name should not be empty");
    }

    @Test
    public void testDescriptionTooLong() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("a".repeat(201));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Description should not exceed 200 characters");
    }

    @Test
    public void testNegativeDuration() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-120);  // Negative duration

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Duration should be positive");
    }
}