package gameq.application.runtime;

public final class BackendException extends RuntimeException {

    private final BackendError error;

    BackendException(BackendError error) {
        super(error.message(), error.cause());
        this.error = error;
    }

    public BackendError error() {
        return error;
    }
}
