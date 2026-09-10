package gameq.desktop;

import java.nio.file.Path;

final class AppData {

    private AppData() {}

    static Path database() {
        var defaultPath =
                Path.of(System.getProperty("user.home"), "Library", "Application Support", "gameq", "library.db");
        return Path.of(System.getProperty("gameq.database", defaultPath.toString()));
    }
}
