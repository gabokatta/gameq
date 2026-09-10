package gameq.application.library;

import gameq.application.Result;
import gameq.database.games.GameStore;
import gameq.database.games.GameStoreException;
import gameq.domain.Game;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public final class Library {

    private final GameStore games;
    private final Clock clock;

    public Library(GameStore games, Clock clock) {
        this.games = Objects.requireNonNull(games, "games");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public Result<Game, LibraryError> addGame(String title) {
        if (title == null || title.isBlank()) {
            return Result.failure(new LibraryError.InvalidTitle());
        }

        var normalizedTitle = title.strip();

        try {
            return Result.success(games.add(normalizedTitle, Instant.now(clock)));
        } catch (GameStoreException cause) {
            return Result.failure(new LibraryError.SaveFailed(normalizedTitle, cause));
        }
    }

    public Result<List<Game>, LibraryError> games() {
        try {
            return Result.success(games.all());
        } catch (GameStoreException cause) {
            return Result.failure(new LibraryError.LoadFailed(cause));
        }
    }
}
