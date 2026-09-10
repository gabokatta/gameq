package gameq.desktop;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

final class AppDataTest {

    @Test
    @ResourceLock(Resources.SYSTEM_PROPERTIES)
    void defaultsToApplicationSupportInTheUserHome() {
        var originalUserHome = System.getProperty("user.home");
        var originalDatabase = System.getProperty("gameq.database");

        try {
            System.setProperty("user.home", "/Users/tester");
            System.clearProperty("gameq.database");

            assertEquals(Path.of("/Users/tester/Library/Application Support/gameq/library.db"), AppData.database());
        } finally {
            restoreProperty("user.home", originalUserHome);
            restoreProperty("gameq.database", originalDatabase);
        }
    }

    @Test
    @ResourceLock(Resources.SYSTEM_PROPERTIES)
    void usesConfiguredDatabasePath() {
        var originalDatabase = System.getProperty("gameq.database");

        try {
            System.setProperty("gameq.database", "/tmp/gameq/library.db");

            assertEquals(Path.of("/tmp/gameq/library.db"), AppData.database());
        } finally {
            restoreProperty("gameq.database", originalDatabase);
        }
    }

    private static void restoreProperty(String name, String value) {
        if (value == null) {
            System.clearProperty(name);
        } else {
            System.setProperty(name, value);
        }
    }
}
