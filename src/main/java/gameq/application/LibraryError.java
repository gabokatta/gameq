package gameq.application;

public enum LibraryError {
    INVALID_TITLE("Game title must not be blank"),
    OPEN_FAILED("Could not open the game library"),
    SAVE_FAILED("Could not save the game"),
    LOAD_FAILED("Could not load the game library"),
    CLOSE_FAILED("Could not close the game library");

    private final String message;

    LibraryError(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
