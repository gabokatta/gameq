package gameq.application.library;

import gameq.application.ApplicationError;
import java.util.Objects;

public sealed interface LibraryError extends ApplicationError {

    sealed interface Caused extends LibraryError, ApplicationError.Caused {}

    record InvalidTitle() implements LibraryError {

        @Override
        public String message() {
            return "Game title must not be blank";
        }
    }

    record SaveFailed(String title, Throwable cause) implements Caused {

        public SaveFailed {
            Objects.requireNonNull(title, "title");
            Objects.requireNonNull(cause, "cause");
        }

        @Override
        public String message() {
            return "Could not save game \"%s\"".formatted(title);
        }
    }

    record LoadFailed(Throwable cause) implements Caused {

        public LoadFailed {
            Objects.requireNonNull(cause, "cause");
        }

        @Override
        public String message() {
            return "Could not load the game library";
        }
    }
}
