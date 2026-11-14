package lk.sasindi.smartcity360.service;

import lk.sasindi.smartcity360.dao.JdbcEscalationRepository;
import lk.sasindi.smartcity360.patterns.chain.ComplaintRoutingChain;
import lk.sasindi.smartcity360.patterns.chain.EscalationPolicy;
import lk.sasindi.smartcity360.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class ComplaintService {

    public int submitComplaint(
            int submitterUserId,
            int roleId,
            String title,
            String description,
            int categoryId,
            int priorityId,
            int districtId,
            int streetId,
            Integer buildingId
    ) throws SQLException {

        validateInput(
                title,
                description,
                categoryId,
                priorityId,
                districtId,
                streetId
        );

        try (Connection connection = DBConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {

                verifyActiveSubmitter(connection, submitterUserId, roleId);

                verifyStreet(connection, districtId, streetId);

                if (buildingId != null) {
                    verifyBuilding(connection, streetId, buildingId);
                }

                findLookupName(
                        connection,
                        "complaint_category",
                        categoryId
                );

                int openStatusId = findLookupId(
                        connection,
                        "complaint_status",
                        "Open"
                );

                String priorityName = findLookupName(
                        connection,
                        "priority",
                        priorityId
                );

                int requiredLevel = getRequiredLevel(priorityName);

                int complaintId = insertComplaint(
                        connection,
                        submitterUserId,
                        title.trim(),
                        description.trim(),
                        categoryId,
                        priorityId,
                        openStatusId,
                        streetId,
                        buildingId
                );

                ComplaintRoutingChain chain =
                        new ComplaintRoutingChain(requiredLevel);

                int finalAssigneeId = chain.process(
                        new JdbcEscalationRepository(connection),
                        complaintId,
                        submitterUserId,
                        priorityId
                );

                updateCurrentAssignee(
                        connection,
                        complaintId,
                        finalAssigneeId
                );

                insertStatusHistory(
                        connection,
                        complaintId,
                        submitterUserId,
                        openStatusId
                );

                connection.commit();

                return complaintId;

            } catch (Exception e) {

                connection.rollback();

                if (e instanceof SQLException) {
                    throw (SQLException) e;
                }

                throw new SQLException(
                        "Unable to submit complaint.",
                        e
                );

            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private void validateInput(
            String title,
            String description,
            int categoryId,
            int priorityId,
            int districtId,
            int streetId
    ) throws SQLException {

        if (title == null || title.isBlank() || title.trim().length() > 180) {
            throw new SQLException(
                    "Enter a complaint title (maximum 180 characters)."
            );
        }

        if (description == null || description.isBlank()) {
            throw new SQLException(
                    "Enter a complaint description."
            );
        }

        if (categoryId <= 0 ||
                priorityId <= 0 ||
                districtId <= 0 ||
                streetId <= 0) {

            throw new SQLException(
                    "Select a valid category, priority, district and street."
            );
        }
    }

    private void verifyActiveSubmitter(
            Connection connection,
            int submitterUserId,
            int roleId
    ) throws SQLException {

        if (submitterUserId <= 0 || roleId < 1 || roleId > 5) {
            throw new SQLException(
                    "Your account is not permitted to submit civic requests."
            );
        }

        String sql = """
                SELECT id
                FROM users
                WHERE id = ?
                  AND status_id = 1
                  AND roles_id = ?
                  AND roles_id IN (1, 2, 3, 4, 5)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, submitterUserId);
            ps.setInt(2, roleId);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    throw new SQLException(
                            "Your account is inactive or your assigned role does not match your login session."
                    );
                }
            }
        }
    }

    private void verifyStreet(
            Connection connection,
            int districtId,
            int streetId
    ) throws SQLException {

        String sql = """
                SELECT s.id
                FROM streets s
                JOIN districts d
                    ON d.id = s.districts_id
                WHERE s.id = ?
                  AND d.id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, streetId);
            ps.setInt(2, districtId);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    throw new SQLException(
                            "The selected street does not belong to that district."
                    );
                }
            }
        }
    }

    private void verifyBuilding(
            Connection connection,
            int streetId,
            int buildingId
    ) throws SQLException {

        String sql = """
                SELECT id
                FROM buildings
                WHERE id = ?
                  AND streets_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, buildingId);
            ps.setInt(2, streetId);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    throw new SQLException(
                            "The selected building does not belong to that street."
                    );
                }
            }
        }
    }

    private int insertComplaint(
            Connection connection,
            int citizenId,
            String title,
            String description,
            int categoryId,
            int priorityId,
            int statusId,
            int streetId,
            Integer buildingId
    ) throws SQLException {

        String sql = """
                INSERT INTO complaints
                (
                    priority_id,
                    complaint_status_id,
                    complaint_category_id,
                    streets_id,
                    buildings_id,
                    complaint_title,
                    complaint_description,
                    submitted_at,
                    updated_at,
                    citizen_id,
                    current_assignee_id
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP,
                        CURRENT_TIMESTAMP, ?, NULL)
                """;

        try (PreparedStatement ps = connection.prepareStatement(
                sql,
                PreparedStatement.RETURN_GENERATED_KEYS
        )) {

            ps.setInt(1, priorityId);
            ps.setInt(2, statusId);
            ps.setInt(3, categoryId);
            ps.setInt(4, streetId);

            if (buildingId == null) {
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setInt(5, buildingId);
            }

            ps.setString(6, title);
            ps.setString(7, description);
            ps.setInt(8, citizenId);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {

                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        throw new SQLException(
                "Could not create the complaint record."
        );
    }

    private void updateCurrentAssignee(
            Connection connection,
            int complaintId,
            int assigneeId
    ) throws SQLException {

        String sql = """
                UPDATE complaints
                SET current_assignee_id = ?,
                    updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, assigneeId);
            ps.setInt(2, complaintId);

            ps.executeUpdate();
        }
    }

    private void insertStatusHistory(
            Connection connection,
            int complaintId,
            int citizenId,
            int newStatusId
    ) throws SQLException {

        String sql = """
                INSERT INTO complaint_status_history
                (
                    remarks,
                    changed_at,
                    complaints_id,
                    users_id,
                    old_status_id,
                    new_status_id
                )
                VALUES (?, CURRENT_TIMESTAMP, ?, ?, NULL, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, "Complaint submitted.");
            ps.setInt(2, complaintId);
            ps.setInt(3, citizenId);
            ps.setInt(4, newStatusId);

            ps.executeUpdate();
        }
    }

    private int findLookupId(
            Connection connection,
            String table,
            String name
    ) throws SQLException {

        String sql = "SELECT id FROM " + table
                + " WHERE LOWER(name) = LOWER(?) LIMIT 1";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    throw new SQLException(
                            "Required lookup value is missing: " + name
                    );
                }

                return rs.getInt("id");
            }
        }
    }

    private String findLookupName(
            Connection connection,
            String table,
            int id
    ) throws SQLException {

        String sql = "SELECT name FROM " + table
                + " WHERE id = ? LIMIT 1";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    throw new SQLException(
                            "Selected lookup value does not exist."
                    );
                }

                return rs.getString("name");
            }
        }
    }

    private int getRequiredLevel(String priorityName)
            throws SQLException {

        try {
            return EscalationPolicy.requiredLevelFor(priorityName);
        } catch (IllegalArgumentException e) {
            throw new SQLException(e.getMessage(), e);
        }
    }
}
