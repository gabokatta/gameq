package gameq.application;

import java.util.Objects;

public sealed interface Result<T, E> {

    record Success<T, E>(T value) implements Result<T, E> {

        public Success {
            Objects.requireNonNull(value, "value");
        }
    }

    record Failure<T, E>(E error) implements Result<T, E> {

        public Failure {
            Objects.requireNonNull(error, "error");
        }
    }

    static <T, E> Result<T, E> success(T value) {
        return new Success<>(value);
    }

    static <T, E> Result<T, E> failure(E error) {
        return new Failure<>(error);
    }
}
