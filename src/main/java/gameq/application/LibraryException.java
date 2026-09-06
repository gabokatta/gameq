package gameq.application;

public final class LibraryException extends RuntimeException {

    private final LibraryError error;

    LibraryException(LibraryError error, Throwable cause) {
        super(error.message(), cause);
        this.error = error;
    }

    public LibraryError error() {
        return error;
    }
}
