package lk.sasindi.smartcity360.dao;

import lk.sasindi.smartcity360.model.UserProfile;
import lk.sasindi.smartcity360.util.DBConnection;

import java.sql.*;

public class ProfileDAO {

    public UserProfile findById(int userId) throws SQLException {

        String sql = """
            SELECT
                u.id,
                u.full_name,
                u.email,
                u.phone_number,
                u.password_hash,
                u.roles_id,
                r.role_name,
                u.cities_id,
                c.city_name,
                u.created_at
            FROM users u
            INNER JOIN roles r ON r.id = u.roles_id
            LEFT JOIN cities c ON c.id = u.cities_id
            WHERE u.id = ?
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                int cityValue = rs.getInt("cities_id");
                Integer cityId = rs.wasNull() ? null : cityValue;

                return new UserProfile(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone_number"),
                        rs.getString("password_hash"),
                        rs.getInt("roles_id"),
                        rs.getString("role_name"),
                        cityId,
                        rs.getString("city_name"),
                        rs.getTimestamp("created_at")
                );
            }
        }
    }

    public boolean updateProfile(int userId, String fullName,
                                 String email, String phoneNumber)
            throws SQLException {

        String sql = """
            UPDATE users
            SET full_name = ?,
                email = ?,
                phone_number = ?
            WHERE id = ?
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, phoneNumber);

            ps.setInt(4, userId);

            return ps.executeUpdate() == 1;
        }
    }

    public boolean updatePassword(int userId, String passwordHash)
            throws SQLException {

        String sql = """
            UPDATE users
            SET password_hash = ?
            WHERE id = ?
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, passwordHash);
            ps.setInt(2, userId);

            return ps.executeUpdate() == 1;
        }
    }
}
