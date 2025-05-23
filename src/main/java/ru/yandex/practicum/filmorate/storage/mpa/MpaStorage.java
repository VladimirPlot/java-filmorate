package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;
import java.util.Optional;

public interface MpaStorage {
    Optional<MpaRating> getMpaById(int id);

    List<MpaRating> getAllMpa();
}