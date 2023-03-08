package com.jdaalba.eda.resilience;

import com.jdaalba.eda.resilience.reader.UserReader;
import com.jdaalba.eda.resilience.serde.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionProvider {

    private static final Logger log = LoggerFactory.getLogger(ConnectionProvider.class);

    private static final String url = "jdbc:postgresql://localhost:5432/resilience";
    private static final String user = "postgres";
    private static final String password = "pass";

    public static void main(String[] args) {
        var sql = """
                INSERT INTO outbox (destination, message, class, emitted_on)
                VALUES (?, ?, ?, NOW())
                """;
        try (
                var connection = getConnection();
                var ps = connection.prepareStatement(sql)
        ) {
            new UserReader().read()
                    .forEach(event -> {
                        try {
                            ps.setString(1, "users");
                            ps.setBytes(2, JsonMapper.asBytes(event));
                            ps.setString(3, event.getClass().getName());
                            ps.execute();
                        } catch (SQLException e) {
                            log.error("SQL Error: ", e);
                        }
                    });
        } catch (SQLException e) {
            log.error("SQL Error: ", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
