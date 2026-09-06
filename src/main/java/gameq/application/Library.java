package gameq.application;

import gameq.domain.Game;
import gameq.persistence.GameStore;
import gameq.persistence.GameStoreException;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;

public final class Library implements AutoCloseable {

    private final GameStore games;
    private final Clock clock;

    private Library(GameStore games, Clock clock) {
        this.games = games;
        this.clock = clock;
    }

    public static Library open(Path database) {
        return open(database, Clock.systemUTC());
    }

    public static Library open(Path database, Clock clock) {
        return callStore(
                LibraryError.OPEN_FAILED, () -> new Library(GameStore.open(database), clock));
    }

    public Game addGame(String title) {
        if (title == null || title.isBlank()) {
            throw new LibraryException(LibraryError.INVALID_TITLE);
        }

        return callStore(
                LibraryError.SAVE_FAILED, () -> games.add(title.strip(), Instant.now(clock)));
    }

    public List<Game> games() {
        return callStore(LibraryError.LOAD_FAILED, games::all);
    }

    @Override
    public void close() {
        runStore(LibraryError.CLOSE_FAILED, games::close);
    }

    private static <T> T callStore(LibraryError error, Supplier<T> call) {
        try {
            return call.get();
        } catch (GameStoreException exception) {
            throw new LibraryException(error, exception);
        }
    }

    private static void runStore(LibraryError error, Runnable call) {
        try {
            call.run();
        } catch (GameStoreException exception) {
            throw new LibraryException(error, exception);
        }
    }
}
