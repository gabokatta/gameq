package gameq.persistence;

import gameq.domain.Game;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;

public final class GameStore implements AutoCloseable {

    private static final String INSERT_GAME =
            "INSERT INTO games (title, added_at) VALUES (?, ?) RETURNING id";
    private static final String SELECT_GAMES =
            "SELECT id, title, added_at FROM games ORDER BY added_at, id";

    private final Connection connection;

    private GameStore(Connection connection) {
        this.connection = connection;
    }

    public static GameStore open(Path database) {
        var absoluteDatabase = database.toAbsolutePath().normalize();
        var url = "jdbc:sqlite:" + absoluteDatabase;

        try {
            Files.createDirectories(absoluteDatabase.getParent());
            Flyway.configure().dataSource(url, null, null).load().migrate();
            return new GameStore(DriverManager.getConnection(url));
        } catch (IOException | SQLException | FlywayException exception) {
            throw new GameStoreException("Could not open library database", exception);
        }
    }

    public Game add(String title, Instant addedAt) {
        var addedAtEpochMillis = addedAt.toEpochMilli();

        try (var statement = connection.prepareStatement(INSERT_GAME)) {
            statement.setString(1, title);
            statement.setLong(2, addedAtEpochMillis);

            try (var rows = statement.executeQuery()) {
                if (!rows.next()) {
                    throw new GameStoreException("Game insert returned no id");
                }
                return new Game(
                        rows.getLong("id"), title, Instant.ofEpochMilli(addedAtEpochMillis));
            }
        } catch (SQLException exception) {
            throw new GameStoreException("Could not add game", exception);
        }
    }

    public List<Game> all() {
        var games = new ArrayList<Game>();

        try (var statement = connection.prepareStatement(SELECT_GAMES);
                var rows = statement.executeQuery()) {
            while (rows.next()) {
                games.add(
                        new Game(
                                rows.getLong("id"),
                                rows.getString("title"),
                                Instant.ofEpochMilli(rows.getLong("added_at"))));
            }
            return List.copyOf(games);
        } catch (SQLException exception) {
            throw new GameStoreException("Could not list games", exception);
        }
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException exception) {
            throw new GameStoreException("Could not close library database", exception);
        }
    }
}
