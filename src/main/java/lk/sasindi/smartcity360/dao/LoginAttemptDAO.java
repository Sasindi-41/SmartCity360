package lk.sasindi.smartcity360.dao;

import lk.sasindi.smartcity360.util.DBConnection;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.Locale;

public class LoginAttemptDAO {

    public static final int MAX_FAILED_ATTEMPTS = 5;
    public static final int WINDOW_MINUTES = 15;
    public static final int LOCKOUT_MINUTES = 15;

    public record LockStatus(boolean locked, int minutesRemaining) { }

    public static String createAttemptKey(String normalizedEmail, String remoteAddress) {
        String source = (normalizedEmail == null ? "" : normalizedEmail.trim().toLowerCase(Locale.ROOT))
                + "|" + (remoteAddress == null ? "unknown" : remoteAddress.trim());
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(source.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is not available.", ex);
        }
    }

    public LockStatus getLockStatus(String attemptKey) throws SQLException {
        String sql = """
                SELECT
                    CASE WHEN locked_until IS NOT NULL
                               AND locked_until > CURRENT_TIMESTAMP
                         THEN 1 ELSE 0 END AS is_locked,
                    GREATEST(
                        0,
                        TIMESTAMPDIFF(SECOND, CURRENT_TIMESTAMP, locked_until)
                    ) AS seconds_remaining
                FROM login_attempts
                WHERE attempt_key = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, attemptKey);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next() || rs.getInt("is_locked") != 1) {
                    return new LockStatus(false, 0);
                }
                int seconds = rs.getInt("seconds_remaining");
                int minutes = Math.max(1, (seconds + 59) / 60);
                return new LockStatus(true, minutes);
            }
        }
    }

    public int recordFailure(String attemptKey) throws SQLException {
        String insert = """
                INSERT IGNORE INTO login_attempts
                    (attempt_key, failed_attempts, first_failed_at, locked_until)
                VALUES (?, 0, CURRENT_TIMESTAMP, NULL)
                """;
        String select = """
                SELECT failed_attempts, first_failed_at
                FROM login_attempts
                WHERE attempt_key = ?
                FOR UPDATE
                """;
        String update = """
                UPDATE login_attempts
                SET failed_attempts = ?,
                    first_failed_at = ?,
                    locked_until = ?,
                    updated_at = CURRENT_TIMESTAMP
                WHERE attempt_key = ?
                """;

        try (Connection connection = DBConnection.getConnection()) {
            boolean originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement statement = connection.prepareStatement(insert)) {
                    statement.setString(1, attemptKey);
                    statement.executeUpdate();
                }

                int failures;
                Timestamp firstFailure;
                try (PreparedStatement statement = connection.prepareStatement(select)) {
                    statement.setString(1, attemptKey);
                    try (ResultSet rs = statement.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Unable to read login-attempt state.");
                        }
                        failures = rs.getInt("failed_attempts");
                        firstFailure = rs.getTimestamp("first_failed_at");
                    }
                }

                Instant now = Instant.now();
                Instant windowStart = now.minus(WINDOW_MINUTES, ChronoUnit.MINUTES);
                if (firstFailure == null || firstFailure.toInstant().isBefore(windowStart)) {
                    failures = 0;
                    firstFailure = Timestamp.from(now);
                }

                failures++;
                Timestamp lockedUntil = failures >= MAX_FAILED_ATTEMPTS
                        ? Timestamp.from(now.plus(LOCKOUT_MINUTES, ChronoUnit.MINUTES))
                        : null;

                try (PreparedStatement statement = connection.prepareStatement(update)) {
                    statement.setInt(1, failures);
                    statement.setTimestamp(2, firstFailure);
                    if (lockedUntil == null) {
                        statement.setNull(3, java.sql.Types.TIMESTAMP);
                    } else {
                        statement.setTimestamp(3, lockedUntil);
                    }
                    statement.setString(4, attemptKey);
                    statement.executeUpdate();
                }

                connection.commit();
                return failures;
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(originalAutoCommit);
            }
        }
    }

    public void clearFailures(String attemptKey) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM login_attempts WHERE attempt_key = ?")) {
            statement.setString(1, attemptKey);
            statement.executeUpdate();
        }
    }
}
