package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NoWhitespaceValidator implements ConstraintValidator<NoWhitespace, String> {

    @Override
    public void initialize(NoWhitespace constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && !value.contains(" "); // Логин не должен содержать пробелы
    }
}