package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidationTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void testValidUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("userLogin");
        user.setName("userName");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "User should be valid");
    }

    @Test
    public void testInvalidEmail() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("userLogin");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Email should be invalid");
    }

    @Test
    public void testLoginWithSpaces() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("user login with spaces");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Login should not contain spaces");
    }

    @Test
    public void testFutureBirthday() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("userLogin");
        user.setBirthday(LocalDate.now().plusDays(1));  // Future date

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Birthday should not be in the future");
    }

    @Test
    public void testNameIsNullUsesLogin() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("userLogin");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        // Name is null
        user.setName(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "User should be valid even if name is null");
        assertEquals("userLogin", user.getName(), "Name should be set to login if it's null");
    }
}