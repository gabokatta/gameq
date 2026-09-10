package gameq.application;

public interface ApplicationError {

    String message();

    interface Caused extends ApplicationError {

        Throwable cause();
    }
}
