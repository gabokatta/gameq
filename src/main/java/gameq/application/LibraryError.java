package gameq.application;

import java.nio.file.Path;
import java.util.Objects;

public sealed interface LibraryError {

    String message();

    record InvalidTitle() implements LibraryError {

        @Override
        public String message() {
            return "Game title must not be blank";
        }
    }

    record OpenFailed(Path database, Throwable cause) implements LibraryError {

        public OpenFailed {
            Objects.requireNonNull(database, "database");
            Objects.requireNonNull(cause, "cause");
        }

        @Override
        public String message() {
            return "Could not open game library at \"%s\"".formatted(database);
        }
    }

    record SaveFailed(String title, Throwable cause) implements LibraryError {

        public SaveFailed {
            Objects.requireNonNull(title, "title");
            Objects.requireNonNull(cause, "cause");
        }

        @Override
        public String message() {
            return "Could not save game \"%s\"".formatted(title);
        }
    }

    record LoadFailed(Throwable cause) implements LibraryError {

        public LoadFailed {
            Objects.requireNonNull(cause, "cause");
        }

        @Override
        public String message() {
            return "Could not load the game library";
        }
    }

    record CloseFailed(Throwable cause) implements LibraryError {

        public CloseFailed {
            Objects.requireNonNull(cause, "cause");
        }

        @Override
        public String message() {
            return "Could not close the game library";
        }
    }
}
