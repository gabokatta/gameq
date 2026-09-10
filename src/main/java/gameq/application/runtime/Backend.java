package gameq.application.runtime;

import gameq.application.Result;
import gameq.application.library.Library;
import gameq.database.Database;
import gameq.database.DatabaseException;
import gameq.database.games.GameStore;
import java.nio.file.Path;
import java.time.Clock;

public final class Backend implements AutoCloseable {

    private final Database database;
    private final Library library;

    private Backend(Database database, Library library) {
        this.database = database;
        this.library = library;
    }

    public static Result<Backend, BackendError> open(Path database) {
        return open(database, Clock.systemUTC());
    }

    public static Result<Backend, BackendError> open(Path database, Clock clock) {
        var normalizedDatabase = database.toAbsolutePath().normalize();

        try {
            var openedDatabase = Database.open(normalizedDatabase);
            var library = new Library(new GameStore(openedDatabase), clock);
            return Result.success(new Backend(openedDatabase, library));
        } catch (DatabaseException cause) {
            return Result.failure(new BackendError.OpenFailed(normalizedDatabase, cause));
        }
    }

    public Library library() {
        return library;
    }

    @Override
    public void close() {
        try {
            database.close();
        } catch (DatabaseException cause) {
            throw new BackendException(new BackendError.CloseFailed(cause));
        }
    }
}
