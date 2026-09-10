package gameq.database;

public final class DatabaseException extends RuntimeException {

    DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
