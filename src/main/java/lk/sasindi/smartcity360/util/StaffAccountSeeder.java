
package lk.sasindi.smartcity360.util;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

public class StaffAccountSeeder {

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final int ACTIVE_STATUS_ID = 1;

    private static final String[][] STAFF = {
            {
                    "Local Staff",
                    "local.staff@smartcity360.local",
                    "0771000001",
                    "2"
            },
            {
                    "Supervisor",
                    "supervisor@smartcity360.local",
                    "0771000002",
                    "3"
            },
            {
                    "City Manager",
                    "city.manager@smartcity360.local",
                    "0771000003",
                    "4"
            }
    };

    public static void main(String[] args) {

        try (Connection connection = DBConnection.getConnection()) {

            for (String[] staff : STAFF) {

                String name = staff[0];
                String email = staff[1];
                String phone = staff[2];
                int roleId = Integer.parseInt(staff[3]);

                if (emailExists(connection, email)) {
                    System.out.println(
                            "Already exists: " + email
                    );
                    continue;
                }

                String temporaryPassword = generatePassword();
                String passwordHash =
                        PasswordUtil.hashPassword(temporaryPassword);

                String sql = """
                    INSERT INTO users
                    (full_name, email, password_hash,
                     phone_number, status_id, roles_id)
                    VALUES (?, ?, ?, ?, ?, ?)
                    """;

                try (PreparedStatement statement =
                             connection.prepareStatement(sql)) {

                    statement.setString(1, name);
                    statement.setString(2, email);
                    statement.setString(3, passwordHash);
                    statement.setString(4, phone);
                    statement.setInt(5, ACTIVE_STATUS_ID);
                    statement.setInt(6, roleId);

                    statement.executeUpdate();
                }

                System.out.println("--------------------------------");
                System.out.println("Created: " + name);
                System.out.println("Email: " + email);
                System.out.println("Temporary password: "
                        + temporaryPassword);
                System.out.println("--------------------------------");
            }

            System.out.println("Staff account seeding completed.");

        } catch (SQLException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    private static boolean emailExists(
            Connection connection,
            String email
    ) throws SQLException {

        String sql = "SELECT id FROM users WHERE email = ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, email);

            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    private static String generatePassword() {

        byte[] bytes = new byte[24];
        RANDOM.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }
}
