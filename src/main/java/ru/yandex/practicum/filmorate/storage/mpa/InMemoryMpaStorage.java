package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.*;

@Component
public class InMemoryMpaStorage implements MpaStorage {
    private final Map<Integer, MpaRating> mpaRatings = new HashMap<>();

    public InMemoryMpaStorage() {
        mpaRatings.put(1, new MpaRating(1, "G"));
        mpaRatings.put(2, new MpaRating(2, "PG"));
        mpaRatings.put(3, new MpaRating(3, "PG-13"));
        mpaRatings.put(4, new MpaRating(4, "R"));
        mpaRatings.put(5, new MpaRating(5, "NC-17"));
    }

    @Override
    public Optional<MpaRating> getMpaById(int id) {
        return Optional.ofNullable(mpaRatings.get(id));
    }

    @Override
    public List<MpaRating> getAllMpa() {
        return new ArrayList<>(mpaRatings.values());
    }
}