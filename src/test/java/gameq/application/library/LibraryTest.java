package gameq.application.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import gameq.application.Result;
import gameq.application.runtime.Backend;
import gameq.application.runtime.BackendError;
import gameq.database.DatabaseException;
import gameq.database.games.GameStoreException;
import gameq.domain.Game;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

final class LibraryTest {

    @TempDir
    Path directory;

    @Test
    void retainsAddedGameAfterReopening() {
        var database = directory.resolve("library.db");
        Game added;

        try (var backend = success(Backend.open(database))) {
            var library = backend.library();
            added = success(library.addGame("Disco Elysium"));
            assertTrue(added.id() > 0);
            assertEquals(List.of(added), success(library.games()));
        }

        try (var backend = success(Backend.open(database))) {
            assertEquals(List.of(added), success(backend.library().games()));
        }
    }

    @Test
    void listsGamesInAddedOrder() {
        var database = directory.resolve("library.db");
        var firstAddedAt = Instant.parse("2026-09-06T12:00:00Z");
        var secondAddedAt = Instant.parse("2026-09-06T12:00:00.100Z");
        Game first;
        Game second;

        try (var backend = success(Backend.open(database, Clock.fixed(firstAddedAt, ZoneOffset.UTC)))) {
            first = success(backend.library().addGame("Pentiment"));
        }

        try (var backend = success(Backend.open(database, Clock.fixed(secondAddedAt, ZoneOffset.UTC)))) {
            second = success(backend.library().addGame("Citizen Sleeper"));
        }

        try (var backend = success(Backend.open(database))) {
            assertEquals(List.of(first, second), success(backend.library().games()));
        }
    }

    @Test
    void rejectsBlankTitle() {
        var database = directory.resolve("library.db");

        try (var backend = success(Backend.open(database))) {
            var library = backend.library();
            var result = library.addGame("   ");
            var error = assertInstanceOf(LibraryError.InvalidTitle.class, failure(result));

            assertEquals("Game title must not be blank", error.message());
            assertEquals(List.of(), success(library.games()));
        }
    }

    @Test
    void reportsWhenDatabaseCannotBeOpened() {
        var error = assertInstanceOf(BackendError.OpenFailed.class, failure(Backend.open(directory)));

        assertEquals(directory.toAbsolutePath().normalize(), error.database());
        assertEquals(
                "Could not open gameq database at \"%s\""
                        .formatted(directory.toAbsolutePath().normalize()),
                error.message());
        var cause = assertInstanceOf(DatabaseException.class, error.cause());
        assertNotNull(cause.getCause());
    }

    @Test
    void reportsWhichGameCouldNotBeSaved() {
        var backend = success(Backend.open(directory.resolve("library.db")));
        var library = backend.library();
        backend.close();

        var error = assertInstanceOf(LibraryError.SaveFailed.class, failure(library.addGame("  Disco Elysium  ")));

        assertEquals("Disco Elysium", error.title());
        assertEquals("Could not save game \"Disco Elysium\"", error.message());
        var cause = assertInstanceOf(GameStoreException.class, error.cause());
        assertNotNull(cause.getCause());
    }

    private static <T, E> T success(Result<T, E> result) {
        return switch (result) {
            case Result.Success(var value) -> value;
            case Result.Failure(var error) -> fail("Expected success, got " + error);
        };
    }

    private static <T, E> E failure(Result<T, E> result) {
        return switch (result) {
            case Result.Success(var value) -> fail("Expected failure, got " + value);
            case Result.Failure(var error) -> error;
        };
    }
}
