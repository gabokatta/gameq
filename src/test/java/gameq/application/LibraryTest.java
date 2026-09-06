package gameq.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        try (var library = Library.open(database)) {
            added = library.addGame("Disco Elysium");
            assertTrue(added.id() > 0);
            assertEquals(List.of(added), library.games());
        }

        try (var reopened = Library.open(database)) {
            assertEquals(List.of(added), reopened.games());
        }
    }

    @Test
    void listsGamesInAddedOrder() {
        var database = directory.resolve("library.db");
        var firstAddedAt = Instant.parse("2026-09-06T12:00:00Z");
        var secondAddedAt = Instant.parse("2026-09-06T12:00:00.100Z");
        Game first;
        Game second;

        try (var library = Library.open(database, Clock.fixed(firstAddedAt, ZoneOffset.UTC))) {
            first = library.addGame("Pentiment");
        }

        try (var library = Library.open(database, Clock.fixed(secondAddedAt, ZoneOffset.UTC))) {
            second = library.addGame("Citizen Sleeper");
        }

        try (var library = Library.open(database)) {
            assertEquals(List.of(first, second), library.games());
        }
    }

    @Test
    void rejectsBlankTitle() {
        var database = directory.resolve("library.db");

        try (var library = Library.open(database)) {
            var exception = assertThrows(LibraryException.class, () -> library.addGame("   "));

            assertEquals(LibraryError.INVALID_TITLE, exception.error());
            assertEquals(List.of(), library.games());
        }
    }

    @Test
    void reportsWhenLibraryCannotBeOpened() {
        var exception = assertThrows(LibraryException.class, () -> Library.open(directory));

        assertEquals(LibraryError.OPEN_FAILED, exception.error());
        assertTrue(exception.getCause() instanceof GameStoreException);
        assertNotNull(exception.getCause().getCause());
    }
}
