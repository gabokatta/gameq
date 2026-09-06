package gameq;

import gameq.desktop.App;
import javafx.application.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Launcher {

    private static final Logger LOG = LoggerFactory.getLogger(Launcher.class);
    private static final String BANNER =
            """
               ____ _____ _____ ___  ___  ____ _
              / __ `/ __ `/ __ `__ \\/ _ \\/ __ `/
             / /_/ / /_/ / / / / / /  __/ /_/ /
             \\__, /\\__,_/_/ /_/ /_/\\___/\\__, /
            /____/                        /_/
            """;

    private Launcher() {}

    static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(Launcher::logUnhandledFailure);
        LOG.info("booting up... \n{}", BANNER.stripTrailing());
        Application.launch(App.class, args);
    }

    private static void logUnhandledFailure(Thread thread, Throwable error) {
        LOG.error("Unhandled failure on thread {}", thread.getName(), error);
    }
}
