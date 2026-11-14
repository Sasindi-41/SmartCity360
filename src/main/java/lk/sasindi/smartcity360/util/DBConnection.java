package lk.sasindi.smartcity360.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {

    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/smartcity360_db"
                    + "?useSSL=false&serverTimezone=UTC"
                    + "&allowPublicKeyRetrieval=true";

    private static final String DEFAULT_USERNAME = "root";

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        String url = environmentOrDefault("SMARTCITY_DB_URL", DEFAULT_URL);
        String username = environmentOrDefault(
                "SMARTCITY_DB_USERNAME", DEFAULT_USERNAME);
        String password = System.getenv("SMARTCITY_DB_PASSWORD");

        if (password == null || password.isBlank()) {
            throw new SQLException(
                    "Database password is not configured. Set the "
                            + "SMARTCITY_DB_PASSWORD environment variable."
            );
        }

        return DriverManager.getConnection(url, username, password);
    }

    private static String environmentOrDefault(String key, String fallback) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
