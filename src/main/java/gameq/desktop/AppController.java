package gameq.desktop;

import gameq.application.ApplicationError;
import gameq.application.Result;
import gameq.application.runtime.Backend;
import gameq.application.runtime.BackendException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class AppController implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(AppController.class);

    private final App view;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(
            Thread.ofVirtual().name("gameq-database-", 0).factory());

    private Backend backend;

    AppController(App view) {
        this.view = view;
    }

    void start() {
        executor.execute(this::openBackend);
    }

    private void openBackend() {
        switch (Backend.open(AppData.database())) {
            case Result.Success(var opened) -> {
                backend = opened;
                loadGames();
            }
            case Result.Failure(var error) -> showError(error);
        }
    }

    void addGame(String title) {
        view.showSaving();
        executor.execute(() -> saveGame(title));
    }

    private void saveGame(String title) {
        switch (backend.library().addGame(title)) {
            case Result.Success(var game) -> Platform.runLater(() -> view.showAddedGame(game));
            case Result.Failure(var error) -> {
                log(error);
                Platform.runLater(() -> view.showSaveError(error.message()));
            }
        }
    }

    private void loadGames() {
        switch (backend.library().games()) {
            case Result.Success(var games) -> Platform.runLater(() -> view.showGames(games));
            case Result.Failure(var error) -> showError(error);
        }
    }

    private void showError(ApplicationError error) {
        log(error);
        Platform.runLater(() -> view.showError(error.message()));
    }

    private static void log(ApplicationError error) {
        if (error instanceof ApplicationError.Caused caused) {
            LOG.error(error.message(), caused.cause());
        }
    }

    @Override
    public void close() {
        executor.execute(this::closeBackend);
        executor.close();
    }

    private void closeBackend() {
        if (backend == null) {
            return;
        }

        try {
            backend.close();
        } catch (BackendException exception) {
            log(exception.error());
        }
    }
}
