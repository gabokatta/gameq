package gameq.persistence;

public final class GameStoreException extends RuntimeException {

    GameStoreException(String message) {
        super(message);
    }

    GameStoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
