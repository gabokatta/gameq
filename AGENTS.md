# gameq agent guidance

## Project

gameq is a local, single-user desktop organizer for a game library, play queue,
playthrough history, ratings, and notes. macOS is the first supported target.
Online metadata is optional; personal organization must work offline.

The stack is Java 25, Gradle, JavaFX, and SQLite. Build the backend through
testable Java contracts before designing the full UI. A small desktop shell
proves launch, persistence, and packaging early.

## Architecture

- JavaFX calls Java application services in the same process. Keep domain rules
  and persistence outside views; backend contracts must not expose JavaFX types.
- Use plain JDBC and explicit dependency construction. There is no need for
  Micronaut, an ORM, or an HTTP server between the UI and backend.
- SQLite owns library data and cover bytes. Apply versioned migrations and
  preserve existing user data. Queue and playthrough changes must be atomic.
- IGDB enriches local entries. Manual entry remains available, and refreshes
  preserve personal data and local overrides.
- Keep credentials outside the library database, behind a credential-store
  interface. macOS uses Keychain, with session-only credentials as a fallback.
- Keep disk and network work off the JavaFX application thread.
- Use standard JavaFX controls with AtlantaFX and one Ikonli icon family.
  CSSFX is development-only. Scene Builder is optional tooling.

## Domain essentials

- A game is a library entry; a playthrough is one attempt at that game. Replays
  preserve earlier attempts. Ratings and titled notes belong to the game.
- Adding a game does not queue it. Queue membership and ordering are explicit.
- Only the first queued game may be in progress. Starting and resuming are
  explicit; reordering or finishing never automatically starts another game.
- Moving the current game away from first pauses its playthrough. Completing or
  dropping a playthrough removes its game from the queue.
- Dates describe elapsed calendar days, not hours played. Unknown historical
  dates remain unknown.
- Provider identity establishes an external match. A matching title alone must
  not merge entries or discard personal data.

## Validation

- After completing code changes, run `./gradlew spotlessApply` and review the
  resulting diff before final validation and handoff.
- Test public application behavior with real temporary SQLite databases,
  including migrations, transactions, and persistence across restarts.
- Test provider integration through a local HTTP test server. Keep request
  handling, parsing, mapping, and persistence real. Normal tests need neither
  internet access nor provider credentials.
- Verify native credential access, theme/icon resources, and JavaFX/SQLite
  loading in the packaged macOS app. IDE execution does not prove packaging.
- Use the Gradle wrapper and configured Java 25 toolchain.
  Consult the build and README for current commands; report the checks actually
  run and any failures.

## Local planning workflow

`nest` is the maintainer's personal collection of local agent configuration,
skills, and workflow tools. It is not a gameq dependency, and contributors do
not need it installed. References to `implement`, `tdd`, or `code-review` in
planning discussions may refer to those local skills.

When implementing or reviewing a planned slice, read its requested spec under
`.nest/specs/` for scope and acceptance criteria. The current local backend spec
is `.nest/specs/gameq-backend.md`. These disposable files are ignored in the
maintainer's setup and may be absent from a fresh clone.

For spec-driven work, implement one selected slice, validate it, and stop for
review. Keep progress and detailed acceptance criteria in the spec. If a task
requires a missing spec, request it; otherwise use the explicit task and tracked
project documentation. This file supplies project context alongside any personal
global agent instructions, without requiring them from contributors.
