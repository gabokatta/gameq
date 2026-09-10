package gameq.database.games;

import gameq.database.Database;
import gameq.domain.Game;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class GameStore {

    private static final String INSERT_GAME = """
            INSERT INTO games (title, added_at)
            VALUES (?, ?)
            RETURNING id
            """;
    private static final String SELECT_GAMES = """
            SELECT id, title, added_at
            FROM games
            ORDER BY added_at, id
            """;

    private final Database database;

    public GameStore(Database database) {
        this.database = Objects.requireNonNull(database, "database");
    }

    public Game add(String title, Instant addedAt) {
        var addedAtEpochMillis = addedAt.toEpochMilli();

        try (var statement = database.connection().prepareStatement(INSERT_GAME)) {
            statement.setString(1, title);
            statement.setLong(2, addedAtEpochMillis);

            try (var rows = statement.executeQuery()) {
                if (!rows.next()) {
                    throw new GameStoreException("Game insert returned no id");
                }

                var id = rows.getLong("id");
                var savedAt = Instant.ofEpochMilli(addedAtEpochMillis);
                return new Game(id, title, savedAt);
            }
        } catch (SQLException cause) {
            throw new GameStoreException("Could not add game", cause);
        }
    }

    public List<Game> all() {
        var games = new ArrayList<Game>();

        try (var statement = database.connection().prepareStatement(SELECT_GAMES);
                var rows = statement.executeQuery()) {
            while (rows.next()) {
                var id = rows.getLong("id");
                var title = rows.getString("title");
                var addedAtEpochMillis = rows.getLong("added_at");
                var addedAt = Instant.ofEpochMilli(addedAtEpochMillis);

                games.add(new Game(id, title, addedAt));
            }
            return List.copyOf(games);
        } catch (SQLException cause) {
            throw new GameStoreException("Could not list games", cause);
        }
    }
}
