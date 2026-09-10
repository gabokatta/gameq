package gameq.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;

public final class Database implements AutoCloseable {

    private final Connection connection;

    private Database(Connection connection) {
        this.connection = connection;
    }

    public static Database open(Path path) {
        var database = path.toAbsolutePath().normalize();
        var url = "jdbc:sqlite:" + database;

        try {
            Files.createDirectories(database.getParent());
            Flyway.configure().dataSource(url, null, null).load().migrate();
            var connection = DriverManager.getConnection(url);
            return new Database(connection);
        } catch (IOException | SQLException | FlywayException cause) {
            var message = "Could not open database at \"%s\"".formatted(database);
            throw new DatabaseException(message, cause);
        }
    }

    public Connection connection() {
        return connection;
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException cause) {
            throw new DatabaseException("Could not close database", cause);
        }
    }
}
