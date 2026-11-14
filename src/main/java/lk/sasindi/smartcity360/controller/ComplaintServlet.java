package lk.sasindi.smartcity360.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import lk.sasindi.smartcity360.service.ComplaintService;
import lk.sasindi.smartcity360.util.DBConnection;
import lk.sasindi.smartcity360.util.JsonUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/api/complaints")
public class ComplaintServlet extends HttpServlet {

    private final ComplaintService complaintService = new ComplaintService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null ||
                !(session.getAttribute("userId") instanceof Number)) {

            sendError(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Please sign in to continue."
            );
            return;
        }

        int userId = ((Number) session.getAttribute("userId")).intValue();

        Object roleValue = session.getAttribute("roleId");

        if (!(roleValue instanceof Number)) {
            sendError(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Your login session is invalid. Please sign in again."
            );
            return;
        }

        int roleId = ((Number) roleValue).intValue();

        String view = request.getParameter("view");
        String districtParameter = request.getParameter("districtId");
        String streetParameter = request.getParameter("streetId");

        try (Connection connection = DBConnection.getConnection()) {

            if ("dashboard".equalsIgnoreCase(view)) {
                response.getWriter().write(
                        loadDashboard(connection, userId, roleId)
                );
                return;
            }

            if ("queue".equalsIgnoreCase(view)) {
                response.getWriter().write(
                        loadComplaintQueue(connection, userId, roleId)
                );
                return;
            }

            if ("activity".equalsIgnoreCase(view)) {
                response.getWriter().write(
                        loadRecentActivity(connection, userId, roleId)
                );
                return;
            }

            if (districtParameter != null && !districtParameter.isBlank()) {

                int districtId = parsePositiveInteger(districtParameter);

                response.getWriter().write(
                        loadStreets(connection, districtId)
                );
                return;
            }

            if (streetParameter != null && !streetParameter.isBlank()) {

                int streetId = parsePositiveInteger(streetParameter);

                response.getWriter().write(
                        loadBuildings(connection, streetId)
                );
                return;
            }

            StringBuilder json = new StringBuilder("{");

            json.append("\"categories\":")
                    .append(loadLookup(connection, "complaint_category"));

            json.append(",\"priorities\":")
                    .append(loadLookup(connection, "priority"));

            json.append(",\"districts\":")
                    .append(loadDistricts(connection));

            json.append("}");

            response.getWriter().write(json.toString());

        } catch (IllegalArgumentException e) {

            sendError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid selection value."
            );

        } catch (SQLException e) {

            getServletContext().log(
                    "Error loading complaint data.",
                    e
            );

            sendError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load complaint data. Please try again."
            );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null ||
                !(session.getAttribute("userId") instanceof Number)) {

            sendError(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Your session has expired. Please sign in again."
            );
            return;
        }

        int submitterUserId = ((Number) session.getAttribute("userId")).intValue();
        Object roleValue = session.getAttribute("roleId");

        if (!(roleValue instanceof Number)) {
            sendError(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Your login session is invalid. Please sign in again."
            );
            return;
        }

        int roleId = ((Number) roleValue).intValue();

        if (roleId < 1 || roleId > 5) {
            sendError(
                    response,
                    HttpServletResponse.SC_FORBIDDEN,
                    "Your account is not permitted to submit civic requests."
            );
            return;
        }

        try {

            String title = request.getParameter("title");
            String description = request.getParameter("description");

            int categoryId = parsePositiveInteger(
                    request.getParameter("categoryId")
            );

            int priorityId = parsePositiveInteger(
                    request.getParameter("priorityId")
            );

            int districtId = parsePositiveInteger(
                    request.getParameter("districtId")
            );

            int streetId = parsePositiveInteger(
                    request.getParameter("streetId")
            );

            Integer buildingId = parseOptionalPositiveInteger(
                    request.getParameter("buildingId")
            );

            int complaintId = complaintService.submitComplaint(
                    submitterUserId,
                    roleId,
                    title,
                    description,
                    categoryId,
                    priorityId,
                    districtId,
                    streetId,
                    buildingId
            );

            response.setStatus(HttpServletResponse.SC_CREATED);

            response.getWriter().write(
                    "{\"success\":true,"
                            + "\"message\":\"Your civic request was submitted successfully.\","
                            + "\"complaintId\":" + complaintId
                            + "}"
            );

        } catch (IllegalArgumentException e) {

            sendError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );

        } catch (SQLException e) {

            getServletContext().log(
                    "Complaint submission failed.",
                    e
            );

            sendError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to submit your complaint. Please try again."
            );
        }
    }

    private String loadComplaintQueue(
            Connection connection,
            int userId,
            int roleId
    ) throws SQLException {

        String accessCondition = complaintScopeCondition(roleId, " WHERE ");

        String sql = """
                SELECT
                    c.id,
                    c.complaint_title,
                    cc.name AS category_name,
                    p.name AS priority_name,
                    cs.name AS status_name,
                    s.street_name,
                    d.district_name,
                    b.building_name,
                    citizen.full_name AS citizen_name,
                    assignee.full_name AS assignee_name,
                    c.submitted_at
                FROM complaints c
                JOIN complaint_category cc
                    ON cc.id = c.complaint_category_id
                JOIN priority p
                    ON p.id = c.priority_id
                JOIN complaint_status cs
                    ON cs.id = c.complaint_status_id
                JOIN streets s
                    ON s.id = c.streets_id
                JOIN districts d
                    ON d.id = s.districts_id
                LEFT JOIN buildings b
                    ON b.id = c.buildings_id
                JOIN users citizen
                    ON citizen.id = c.citizen_id
                LEFT JOIN users assignee
                    ON assignee.id = c.current_assignee_id
                """ + accessCondition + """
                ORDER BY c.submitted_at DESC, c.id DESC
                """;

        StringBuilder json = new StringBuilder("[");

        boolean first = true;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            bindComplaintScope(ps, userId, roleId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    if (!first) {
                        json.append(",");
                    }

                    json.append("{")
                            .append("\"id\":").append(rs.getInt("id"))
                            .append(",\"title\":")
                            .append(jsonString(rs.getString("complaint_title")))
                            .append(",\"category\":")
                            .append(jsonString(rs.getString("category_name")))
                            .append(",\"priority\":")
                            .append(jsonString(rs.getString("priority_name")))
                            .append(",\"status\":")
                            .append(jsonString(rs.getString("status_name")))
                            .append(",\"street\":")
                            .append(jsonString(rs.getString("street_name")))
                            .append(",\"district\":")
                            .append(jsonString(rs.getString("district_name")))
                            .append(",\"building\":")
                            .append(jsonString(rs.getString("building_name")))
                            .append(",\"citizen\":")
                            .append(jsonString(rs.getString("citizen_name")))
                            .append(",\"assignee\":")
                            .append(jsonString(rs.getString("assignee_name")))
                            .append(",\"submittedAt\":")
                            .append(jsonString(
                                    rs.getTimestamp("submitted_at") == null
                                            ? null
                                            : rs.getTimestamp("submitted_at").toString()
                            ))
                            .append("}");

                    first = false;
                }
            }
        }

        return json.append("]").toString();
    }

    private String loadDashboard(
            Connection connection,
            int userId,
            int roleId
    ) throws SQLException {

        String accessCondition = complaintScopeCondition(roleId, " WHERE ");

        String sql = """
                SELECT
                    LOWER(cs.name) AS status_name,
                    COUNT(c.id) AS total
                FROM complaints c
                JOIN complaint_status cs
                    ON cs.id = c.complaint_status_id
                """ + accessCondition + """
                GROUP BY LOWER(cs.name)
                """;

        int open = 0;
        int inProgress = 0;
        int resolved = 0;
        int total = 0;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            bindComplaintScope(ps, userId, roleId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    String status = rs.getString("status_name");
                    int count = rs.getInt("total");

                    total += count;

                    if ("open".equals(status)) {
                        open += count;
                    } else if ("in progress".equals(status)
                            || "in-progress".equals(status)
                            || "in_progress".equals(status)) {
                        inProgress += count;
                    } else if ("resolved".equals(status)
                            || "closed".equals(status)) {
                        resolved += count;
                    }
                }
            }
        }

        return "{"
                + "\"open\":" + open + ","
                + "\"inProgress\":" + inProgress + ","
                + "\"resolved\":" + resolved + ","
                + "\"total\":" + total
                + "}";
    }

    private String loadRecentActivity(
            Connection connection,
            int userId,
            int roleId
    ) throws SQLException {

        String accessCondition = complaintScopeCondition(roleId, " AND ");

        String sql = """
                SELECT
                    ce.id,
                    ce.remarks,
                    ce.action_at,
                    c.id AS complaint_id,
                    action.name AS action_name,
                    level.level_name,
                    assignedFrom.full_name AS assigned_from_name,
                    assignedTo.full_name AS assigned_to_name
                FROM complaint_escalations ce
                JOIN complaints c
                    ON c.id = ce.complaints_id
                JOIN action_type action
                    ON action.id = ce.action_type_id
                JOIN escalation_levels level
                    ON level.id = ce.escalation_levels_id
                LEFT JOIN users assignedFrom
                    ON assignedFrom.id = ce.assigned_from_id
                JOIN users assignedTo
                    ON assignedTo.id = ce.assigned_to_id
                WHERE 1 = 1
                """ + accessCondition + """
                ORDER BY ce.action_at DESC, ce.id DESC
                LIMIT 3
                """;

        StringBuilder json = new StringBuilder("[");

        boolean first = true;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            bindComplaintScope(ps, userId, roleId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    if (!first) {
                        json.append(",");
                    }

                    json.append("{")
                            .append("\"id\":").append(rs.getInt("id"))
                            .append(",\"complaintId\":")
                            .append(rs.getInt("complaint_id"))
                            .append(",\"action\":")
                            .append(jsonString(rs.getString("action_name")))
                            .append(",\"level\":")
                            .append(jsonString(rs.getString("level_name")))
                            .append(",\"from\":")
                            .append(jsonString(rs.getString("assigned_from_name")))
                            .append(",\"to\":")
                            .append(jsonString(rs.getString("assigned_to_name")))
                            .append(",\"remarks\":")
                            .append(jsonString(rs.getString("remarks")))
                            .append(",\"actionAt\":")
                            .append(jsonString(
                                    rs.getTimestamp("action_at") == null
                                            ? null
                                            : rs.getTimestamp("action_at").toString()
                            ))
                            .append("}");

                    first = false;
                }
            }
        }

        return json.append("]").toString();
    }

    private String loadLookup(
            Connection connection,
            String table
    ) throws SQLException {

        String sql = "SELECT id, name FROM " + table + " ORDER BY name";

        StringBuilder json = new StringBuilder("[");
        boolean first = true;

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                if (!first) {
                    json.append(",");
                }

                json.append("{")
                        .append("\"id\":").append(rs.getInt("id"))
                        .append(",\"name\":")
                        .append(jsonString(rs.getString("name")))
                        .append("}");

                first = false;
            }
        }

        return json.append("]").toString();
    }

    private String loadDistricts(Connection connection)
            throws SQLException {

        String sql = """
                SELECT id, district_name
                FROM districts
                ORDER BY district_name
                """;

        StringBuilder json = new StringBuilder("[");
        boolean first = true;

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                if (!first) {
                    json.append(",");
                }

                json.append("{")
                        .append("\"id\":").append(rs.getInt("id"))
                        .append(",\"name\":")
                        .append(jsonString(rs.getString("district_name")))
                        .append("}");

                first = false;
            }
        }

        return json.append("]").toString();
    }

    private String loadStreets(
            Connection connection,
            int districtId
    ) throws SQLException {

        String sql = """
                SELECT id, street_name
                FROM streets
                WHERE districts_id = ?
                ORDER BY street_name
                """;

        StringBuilder json = new StringBuilder("[");
        boolean first = true;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, districtId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    if (!first) {
                        json.append(",");
                    }

                    json.append("{")
                            .append("\"id\":").append(rs.getInt("id"))
                            .append(",\"name\":")
                            .append(jsonString(rs.getString("street_name")))
                            .append("}");

                    first = false;
                }
            }
        }

        return json.append("]").toString();
    }

    private String loadBuildings(
            Connection connection,
            int streetId
    ) throws SQLException {

        String sql = """
                SELECT id, building_name, building_code, building_type
                FROM buildings
                WHERE streets_id = ?
                ORDER BY building_name
                """;

        StringBuilder json = new StringBuilder("[");
        boolean first = true;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, streetId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    if (!first) {
                        json.append(",");
                    }

                    json.append("{")
                            .append("\"id\":").append(rs.getInt("id"))
                            .append(",\"name\":")
                            .append(jsonString(rs.getString("building_name")))
                            .append(",\"code\":")
                            .append(jsonString(rs.getString("building_code")))
                            .append(",\"type\":")
                            .append(jsonString(rs.getString("building_type")))
                            .append("}");

                    first = false;
                }
            }
        }

        return json.append("]").toString();
    }

    private int parsePositiveInteger(String value) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Please complete all required selections."
            );
        }

        try {
            int number = Integer.parseInt(value);

            if (number <= 0) {
                throw new IllegalArgumentException(
                        "Please select a valid option."
                );
            }

            return number;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Invalid selection value."
            );
        }
    }

    private Integer parseOptionalPositiveInteger(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return parsePositiveInteger(value);
    }


    private String complaintScopeCondition(int roleId, String prefix) {
        return switch (roleId) {
            case 1 -> prefix + " c.citizen_id = ? ";
            case 2, 3 -> prefix + " (c.current_assignee_id = ? OR c.citizen_id = ?) ";
            case 4, 5 -> "";
            default -> throw new IllegalArgumentException("Unsupported user role.");
        };
    }

    private void bindComplaintScope(
            PreparedStatement statement,
            int userId,
            int roleId
    ) throws SQLException {
        if (roleId == 1) {
            statement.setInt(1, userId);
        } else if (roleId == 2 || roleId == 3) {
            statement.setInt(1, userId);
            statement.setInt(2, userId);
        }
    }

    private String jsonString(String value) {

        if (value == null) {
            return "null";
        }

        return "\"" + JsonUtil.escape(value) + "\"";
    }

    private void sendError(
            HttpServletResponse response,
            int status,
            String message
    ) throws IOException {

        response.setStatus(status);

        response.getWriter().write(
                "{\"success\":false,\"message\":"
                        + jsonString(message)
                        + "}"
        );
    }
}