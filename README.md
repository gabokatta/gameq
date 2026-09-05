# gameq

A local desktop game backlog and play queue manager.

The project currently contains a minimal JavaFX window. Library storage and
organizer features are planned in later slices.

## Requirements

- JDK 25, installed locally.
- macOS is the initial development target.

The Gradle wrapper downloads the pinned Gradle version and JavaFX dependencies
on the first build. A separate Gradle or JavaFX SDK installation is unnecessary.

## Run

```sh
./gradlew run
```

## Build

```sh
./gradlew build
```

This compiles the application and produces Gradle application distributions.
There are no automated tests yet. A bundled macOS application with its own Java
runtime will be added during the desktop viability work.

## IntelliJ IDEA

Open the repository as a Gradle project. Set the project SDK and Gradle JVM to
JDK 25, then run the Gradle `run` task. The build configures the Java 25 toolchain
and JavaFX module path.
