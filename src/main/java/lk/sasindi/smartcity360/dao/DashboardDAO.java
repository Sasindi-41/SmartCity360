package lk.sasindi.smartcity360.dao;

import lk.sasindi.smartcity360.model.DashboardActivity;
import lk.sasindi.smartcity360.model.DashboardChartPoint;
import lk.sasindi.smartcity360.model.DashboardSummary;
import lk.sasindi.smartcity360.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardDAO {
    private static final int CITIZEN_ROLE_ID = 1;
    private static final int LOCAL_STAFF_ROLE_ID = 2;
    private static final int SUPERVISOR_ROLE_ID = 3;
    private static final int CITY_MANAGER_ROLE_ID = 4;
    private static final int ADMIN_ROLE_ID = 5;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("dd MMM, hh:mm a");

    public DashboardSummary getSummary(int userId, int roleId, String fullName) throws SQLException {
        DashboardSummary summary = new DashboardSummary();
        summary.setFirstName(firstName(fullName));

        try (Connection connection = DBConnection.getConnection()) {
            summary.setCityCount(count(connection, "SELECT COUNT(*) FROM cities"));
            summary.setDistrictCount(count(connection, "SELECT COUNT(*) FROM districts"));
            summary.setStreetCount(count(connection, "SELECT COUNT(*) FROM streets"));
            summary.setBuildingCount(count(connection, "SELECT COUNT(*) FROM buildings"));
            summary.setEnergyMeterCount(count(connection, "SELECT COUNT(*) FROM energy_meters"));
            summary.setStreetlightCount(count(connection, "SELECT COUNT(*) FROM streetlights"));
            summary.setActivePolicyCount(count(connection, "SELECT COUNT(*) FROM city_policies WHERE is_active = 1"));
            summary.setEnergyReadingCount(count(connection, "SELECT COUNT(*) FROM energy_readings"));
            summary.setPolicyEvaluationCount(count(connection, "SELECT COUNT(*) FROM policy_evaluations"));
            summary.setOpenComplaintCount(countOpenComplaints(connection, userId, roleId));
            summary.setTotalComplaintCount(countScopedComplaints(connection, userId, roleId));
            summary.setEnergyTrend(loadEnergyTrend(connection));
            summary.setComplaintStatusBreakdown(loadComplaintStatusBreakdown(connection, userId, roleId));
            summary.setBuildingDistribution(loadBuildingDistribution(connection));
            summary.setStreetlightModeBreakdown(loadStreetlightModeBreakdown(connection));
            summary.setPolicyResultBreakdown(loadPolicyResultBreakdown(connection));
            summary.setRecentActivities(loadRecentActivities(connection, userId, roleId));
        }
        return summary;
    }

    private int count(Connection connection, String sql) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private int countOpenComplaints(Connection connection, int userId, int roleId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM complaints c INNER JOIN complaint_status cs ON cs.id=c.complaint_status_id " +
                "WHERE LOWER(cs.name) NOT IN ('resolved','closed','rejected')" + complaintScope(roleId);
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            bindComplaintScope(ps, userId, roleId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        }
    }

    private int countScopedComplaints(Connection connection, int userId, int roleId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM complaints c WHERE 1=1" + complaintScope(roleId);
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            bindComplaintScope(ps, userId, roleId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        }
    }

    private String complaintScope(int roleId) {
        if (roleId == CITIZEN_ROLE_ID) return " AND c.citizen_id = ?";
        if (roleId == LOCAL_STAFF_ROLE_ID || roleId == SUPERVISOR_ROLE_ID) return " AND c.current_assignee_id = ?";
        return "";
    }

    private void bindComplaintScope(PreparedStatement ps, int userId, int roleId) throws SQLException {
        if (roleId == CITIZEN_ROLE_ID || roleId == LOCAL_STAFF_ROLE_ID || roleId == SUPERVISOR_ROLE_ID) {
            ps.setInt(1, userId);
        }
    }

    private List<DashboardChartPoint> loadEnergyTrend(Connection connection) throws SQLException {
        Map<LocalDate, Long> values = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) values.put(today.minusDays(i), 0L);

        String sql = "SELECT DATE(msr.received_at) AS reading_day, COUNT(*) AS reading_count " +
                "FROM energy_readings er " +
                "INNER JOIN meter_stream_records msr ON msr.id = er.meter_stream_records_id " +
                "WHERE msr.received_at >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) " +
                "GROUP BY DATE(msr.received_at) ORDER BY reading_day";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Date day = rs.getDate("reading_day");
                if (day != null) values.put(day.toLocalDate(), rs.getLong("reading_count"));
            }
        }
        List<DashboardChartPoint> points = new ArrayList<>();
        DateTimeFormatter labelFormat = DateTimeFormatter.ofPattern("dd MMM");
        values.forEach((day, value) -> points.add(new DashboardChartPoint(day.format(labelFormat), value)));
        return points;
    }

    private List<DashboardChartPoint> loadComplaintStatusBreakdown(Connection connection, int userId, int roleId) throws SQLException {
        List<DashboardChartPoint> points = new ArrayList<>();
        String sql = "SELECT cs.name AS label, COUNT(*) AS amount FROM complaints c " +
                "INNER JOIN complaint_status cs ON cs.id=c.complaint_status_id " +
                "WHERE 1=1" + complaintScope(roleId) + " GROUP BY cs.id, cs.name ORDER BY amount DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            bindComplaintScope(ps, userId, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) points.add(new DashboardChartPoint(rs.getString("label"), rs.getLong("amount")));
            }
        }
        return points;
    }

    private List<DashboardChartPoint> loadBuildingDistribution(Connection connection) throws SQLException {
        List<DashboardChartPoint> points = new ArrayList<>();
        String sql = "SELECT c.city_name AS label, COUNT(b.id) AS amount " +
                "FROM cities c LEFT JOIN districts d ON d.cities_id=c.id " +
                "LEFT JOIN streets s ON s.districts_id=d.id " +
                "LEFT JOIN buildings b ON b.streets_id=s.id " +
                "GROUP BY c.id,c.city_name ORDER BY amount DESC,c.city_name LIMIT 7";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) points.add(new DashboardChartPoint(rs.getString("label"), rs.getLong("amount")));
        }
        return points;
    }

    private List<DashboardChartPoint> loadStreetlightModeBreakdown(Connection connection) throws SQLException {
        List<DashboardChartPoint> points = new ArrayList<>();
        String sql = "SELECT COALESCE(NULLIF(TRIM(sc.operating_mode),''),'Unassigned') AS label, COUNT(sl.id) AS amount " +
                "FROM streetlights sl LEFT JOIN streetlight_configurations sc " +
                "ON sc.id=sl.streetlight_configurations_id GROUP BY label ORDER BY amount DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) points.add(new DashboardChartPoint(rs.getString("label"), rs.getLong("amount")));
        }
        return points;
    }

    private List<DashboardChartPoint> loadPolicyResultBreakdown(Connection connection) throws SQLException {
        List<DashboardChartPoint> points = new ArrayList<>();
        String sql = "SELECT COALESCE(er.name,'Unspecified') AS label, COUNT(pe.id) AS amount " +
                "FROM policy_evaluations pe LEFT JOIN evaluation_result er " +
                "ON er.id=pe.evaluation_result_id GROUP BY er.id,er.name ORDER BY amount DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) points.add(new DashboardChartPoint(rs.getString("label"), rs.getLong("amount")));
        }
        return points;
    }

    private List<DashboardActivity> loadRecentActivities(Connection connection, int userId, int roleId) throws SQLException {
        List<DashboardActivity> activities = new ArrayList<>();
        String sql;
        if (roleId == CITY_MANAGER_ROLE_ID || roleId == ADMIN_ROLE_ID) {

            ensurePowerSchema();
            sql = """
                SELECT activity_type, activity_title, activity_description, activity_time FROM (
                    SELECT 'CIVIC REQUEST' activity_type, c.complaint_title activity_title,
                           CONCAT('Status: ',cs.name) activity_description, c.updated_at activity_time
                    FROM complaints c INNER JOIN complaint_status cs ON cs.id=c.complaint_status_id
                    UNION ALL
                    SELECT 'ENERGY STREAM', CONCAT('Meter ',em.serial_number),
                           CONCAT('Authentication: ',ast.name,' · Processing: ',ps.name), msr.received_at
                    FROM meter_stream_records msr INNER JOIN energy_meters em ON em.id=msr.energy_meters_id
                    INNER JOIN authentication_status ast ON ast.id=msr.authentication_status_id
                    INNER JOIN processing_status ps ON ps.id=msr.processing_status_id
                    UNION ALL
                    SELECT 'POLICY EVALUATION',cp.policy_name,CONCAT('Result: ',er.name),pe.evaluated_at
                    FROM policy_evaluations pe INNER JOIN city_policies cp ON cp.id=pe.city_policies_id
                    INNER JOIN evaluation_result er ON er.id=pe.evaluation_result_id
                    UNION ALL
                    SELECT 'POWER OPERATION',CONCAT(component_type,': ',component_name),
                           CONCAT('Operation: ',operation),performed_at FROM power_operation_log
                ) recent ORDER BY activity_time DESC LIMIT 3
                """;
        } else {
            sql = "SELECT 'CIVIC REQUEST' activity_type,c.complaint_title activity_title, " +
                    "CONCAT('Status: ',cs.name) activity_description,c.updated_at activity_time " +
                    "FROM complaints c INNER JOIN complaint_status cs ON cs.id=c.complaint_status_id " +
                    "WHERE 1=1" + complaintScope(roleId) + " ORDER BY activity_time DESC LIMIT 3";
        }
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (roleId != CITY_MANAGER_ROLE_ID && roleId != ADMIN_ROLE_ID) bindComplaintScope(ps, userId, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp timestamp = rs.getTimestamp("activity_time");
                    String time = timestamp == null ? "" : timestamp.toLocalDateTime().format(TIME_FORMAT);
                    activities.add(new DashboardActivity(rs.getString("activity_type"), rs.getString("activity_title"),
                            rs.getString("activity_description"), time));
                }
            }
        }
        return activities;
    }

    private static volatile boolean powerSchemaReady = false;

    private static void ensurePowerSchema() throws SQLException {
        if (powerSchemaReady) return;
        synchronized (DashboardDAO.class) {
            if (!powerSchemaReady) {
                new HierarchyDAO().ensurePowerSchema();
                powerSchemaReady = true;
            }
        }
    }

    private String firstName(String fullName) {
        if (fullName == null || fullName.isBlank()) return "there";
        String trimmed = fullName.trim();
        int space = trimmed.indexOf(' ');
        return space > 0 ? trimmed.substring(0, space) : trimmed;
    }
}
