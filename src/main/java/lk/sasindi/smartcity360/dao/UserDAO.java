package lk.sasindi.smartcity360.dao;

import lk.sasindi.smartcity360.model.User;
import lk.sasindi.smartcity360.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    private static final int CITIZEN_ROLE_ID = 1;
    private static final int ACTIVE_STATUS_ID = 1;

    public boolean emailExists(String email) throws SQLException {

        String sql = "SELECT id FROM users WHERE email = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, email);

            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    public boolean registerUser(
            String fullName,
            String email,
            String passwordHash,
            String phoneNumber
    ) throws SQLException {

        String sql = """
            INSERT INTO users
            (full_name, email, password_hash, phone_number,
             roles_id, status_id)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, fullName);
            statement.setString(2, email);
            statement.setString(3, passwordHash);
            statement.setString(4, phoneNumber);
            statement.setInt(5, CITIZEN_ROLE_ID);
            statement.setInt(6, ACTIVE_STATUS_ID);

            return statement.executeUpdate() == 1;
        }
    }

    public User findActiveUserByEmail(String email)
            throws SQLException {

        String sql = """
                SELECT id, full_name, email, password_hash,
                       roles_id, status_id
                FROM users
                WHERE email = ? AND status_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, email);
            statement.setInt(2, ACTIVE_STATUS_ID);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {

                    return new User(
                            result.getInt("id"),
                            result.getString("full_name"),
                            result.getString("email"),
                            result.getString("password_hash"),
                            result.getInt("roles_id"),
                            result.getInt("status_id")
                    );
                }
            }
        }

        return null;
    }
}