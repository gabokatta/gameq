package gameq.desktop;

import java.nio.file.Path;

final class AppData {

    private AppData() {}

    static Path database() {
        var userHome = System.getProperty("user.home");
        var applicationSupport = Path.of(userHome, "Library", "Application Support");
        var defaultPath = applicationSupport.resolve("gameq").resolve("library.db");
        var configuredPath = System.getProperty("gameq.database", defaultPath.toString());
        return Path.of(configuredPath);
    }
}
