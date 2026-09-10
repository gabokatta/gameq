package gameq.application.runtime;

import gameq.application.ApplicationError;
import java.nio.file.Path;
import java.util.Objects;

public sealed interface BackendError extends ApplicationError.Caused {

    record OpenFailed(Path database, Throwable cause) implements BackendError {

        public OpenFailed {
            Objects.requireNonNull(database, "database");
            Objects.requireNonNull(cause, "cause");
        }

        @Override
        public String message() {
            return "Could not open gameq database at \"%s\"".formatted(database);
        }
    }

    record CloseFailed(Throwable cause) implements BackendError {

        public CloseFailed {
            Objects.requireNonNull(cause, "cause");
        }

        @Override
        public String message() {
            return "Could not close gameq database";
        }
    }
}
