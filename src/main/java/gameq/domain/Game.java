package gameq.domain;

import java.time.Instant;
import java.util.Objects;

public record Game(long id, String title, Instant addedAt) {

    public Game {
        if (id <= 0) {
            throw new IllegalArgumentException("id must be positive");
        }

        title = Objects.requireNonNull(title, "title").strip();
        Objects.requireNonNull(addedAt, "addedAt");

        if (title.isEmpty()) {
            throw new IllegalArgumentException("title must not be blank");
        }
    }
}
