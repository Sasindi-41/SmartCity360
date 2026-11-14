package lk.sasindi.smartcity360.dao;

import lk.sasindi.smartcity360.patterns.chain.EscalationRecord;
import lk.sasindi.smartcity360.patterns.chain.EscalationRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class JdbcEscalationRepository implements EscalationRepository {

    private final Connection connection;

    public JdbcEscalationRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public int findActiveStaffId(int roleId) throws SQLException {

        String sql = """
                SELECT id
                FROM users
                WHERE roles_id = ?
                  AND status_id = 1
                ORDER BY id
                LIMIT 1
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, roleId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("id") : -1;
            }
        }
    }

    @Override
    public int findEscalationLevelId(int roleId, int sequenceNumber)
            throws SQLException {

        String sql = """
                SELECT id
                FROM escalation_levels
                WHERE roles_id = ?
                  AND sequence_number = ?
                LIMIT 1
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, roleId);
            ps.setInt(2, sequenceNumber);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("id") : -1;
            }
        }
    }

    @Override
    public int findActionTypeId(String actionName) throws SQLException {

        String sql = """
                SELECT id
                FROM action_type
                WHERE LOWER(name) = LOWER(?)
                LIMIT 1
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, actionName);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    throw new SQLException(
                            "Action type not found: " + actionName
                    );
                }

                return rs.getInt("id");
            }
        }
    }

    @Override
    public void recordEscalation(EscalationRecord record)
            throws SQLException {

        String sql = """
                INSERT INTO complaint_escalations
                (
                    remarks,
                    action_at,
                    assigned_to_id,
                    assigned_from_id,
                    complaints_id,
                    action_type_id,
                    escalation_levels_id
                )
                VALUES (?, CURRENT_TIMESTAMP, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, record.remarks());
            ps.setInt(2, record.assignedToId());
            ps.setInt(3, record.assignedFromId());
            ps.setInt(4, record.complaintId());
            ps.setInt(5, record.actionTypeId());
            ps.setInt(6, record.escalationLevelId());

            ps.executeUpdate();
        }
    }
}
