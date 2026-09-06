package gameq.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import gameq.domain.Game;
import gameq.persistence.GameStoreException;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

final class LibraryTest {

    @TempDir Path directory;

    @Test
    void retainsAddedGameAfterReopening() {
        var database = directory.resolve("library.db");
        Game added;

        try (var library = success(Library.open(database))) {
            added = success(library.addGame("Disco Elysium"));
            assertTrue(added.id() > 0);
            assertEquals(List.of(added), success(library.games()));
        }

        try (var reopened = success(Library.open(database))) {
            assertEquals(List.of(added), success(reopened.games()));
        }
    }

    @Test
    void listsGamesInAddedOrder() {
        var database = directory.resolve("library.db");
        var firstAddedAt = Instant.parse("2026-09-06T12:00:00Z");
        var secondAddedAt = Instant.parse("2026-09-06T12:00:00.100Z");
        Game first;
        Game second;

        try (var library =
                success(Library.open(database, Clock.fixed(firstAddedAt, ZoneOffset.UTC)))) {
            first = success(library.addGame("Pentiment"));
        }

        try (var library =
                success(Library.open(database, Clock.fixed(secondAddedAt, ZoneOffset.UTC)))) {
            second = success(library.addGame("Citizen Sleeper"));
        }

        try (var library = success(Library.open(database))) {
            assertEquals(List.of(first, second), success(library.games()));
        }
    }

    @Test
    void rejectsBlankTitle() {
        var database = directory.resolve("library.db");

        try (var library = success(Library.open(database))) {
            var result = library.addGame("   ");
            var error = assertInstanceOf(LibraryError.InvalidTitle.class, failure(result));

            assertEquals("Game title must not be blank", error.message());
            assertEquals(List.of(), success(library.games()));
        }
    }

    @Test
    void reportsWhenLibraryCannotBeOpened() {
        var error =
                assertInstanceOf(LibraryError.OpenFailed.class, failure(Library.open(directory)));

        assertEquals(directory.toAbsolutePath().normalize(), error.database());
        assertEquals(
                "Could not open game library at \"%s\""
                        .formatted(directory.toAbsolutePath().normalize()),
                error.message());
        assertTrue(error.cause() instanceof GameStoreException);
        assertNotNull(error.cause().getCause());
    }

    @Test
    void reportsWhichGameCouldNotBeSaved() {
        var library = success(Library.open(directory.resolve("library.db")));
        library.close();

        var error =
                assertInstanceOf(
                        LibraryError.SaveFailed.class,
                        failure(library.addGame("  Disco Elysium  ")));

        assertEquals("Disco Elysium", error.title());
        assertEquals("Could not save game \"Disco Elysium\"", error.message());
        assertTrue(error.cause() instanceof GameStoreException);
        assertNotNull(error.cause().getCause());
    }

    private static <T, E> T success(Result<T, E> result) {
        return switch (result) {
            case Result.Success<T, E>(var value) -> value;
            case Result.Failure<T, E>(var error) -> fail("Expected success, got " + error);
        };
    }

    private static <T, E> E failure(Result<T, E> result) {
        return switch (result) {
            case Result.Success<T, E>(var value) -> fail("Expected failure, got " + value);
            case Result.Failure<T, E>(var error) -> error;
        };
    }
}
