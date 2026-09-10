# gameq

A local desktop game backlog and play queue manager built with Java 25 and JavaFX.
Currently a minimal window; organizer features are still in development.

## Run

Requires JDK 25. macOS is the initial target.

```sh
./gradlew run
```

## Package for macOS

Build a self-contained application image for the current Mac architecture:

```sh
./gradlew packageApp
```

The application is written to `build/jpackage/GameQ.app`. This development image
is ad-hoc signed; it is not notarized or ready for public distribution.
