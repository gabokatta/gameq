package gameq.application;

import gameq.domain.Game;
import gameq.persistence.GameStore;
import gameq.persistence.GameStoreException;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.util.List;

public final class Library implements AutoCloseable {

    private final GameStore games;
    private final Clock clock;

    private Library(GameStore games, Clock clock) {
        this.games = games;
        this.clock = clock;
    }

    public static Result<Library, LibraryError> open(Path database) {
        return open(database, Clock.systemUTC());
    }

    public static Result<Library, LibraryError> open(Path database, Clock clock) {
        var normalizedDatabase = database.toAbsolutePath().normalize();

        try {
            return Result.success(new Library(GameStore.open(normalizedDatabase), clock));
        } catch (GameStoreException cause) {
            return Result.failure(new LibraryError.OpenFailed(normalizedDatabase, cause));
        }
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

    @Override
    public void close() {
        try {
            games.close();
        } catch (GameStoreException cause) {
            throw new LibraryException(new LibraryError.CloseFailed(cause), cause);
        }
    }
}
