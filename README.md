# gameq

A local desktop game backlog and play queue manager built with Java 25 and JavaFX.
Currently a minimal window; organizer features are still in development.

## Run

Requires JDK 25. macOS is the initial target.

```sh
./gradlew run
```

## Dependency verification

Enable the repository hooks once per clone:

```sh
git config core.hooksPath .githooks
```

The pre-commit hook runs a refreshed build only when dependency declaration files
are staged. It verifies existing checksums but never changes which artifacts are
trusted.

After intentionally adding or updating a dependency, regenerate the checksums:

```sh
./scripts/update-dependency-verification
```

Review the `gradle/verification-metadata.xml` diff before committing it.
