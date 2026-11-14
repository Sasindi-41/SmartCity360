
package lk.sasindi.smartcity360.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import lk.sasindi.smartcity360.patterns.composite.Building;
import lk.sasindi.smartcity360.patterns.composite.City;
import lk.sasindi.smartcity360.patterns.composite.CityComponent;
import lk.sasindi.smartcity360.patterns.composite.PowerStatus;
import lk.sasindi.smartcity360.patterns.composite.UsageReport;
import lk.sasindi.smartcity360.model.PowerOperationRecord;
import lk.sasindi.smartcity360.model.PowerOperationResult;
import lk.sasindi.smartcity360.util.UsageReportPdf;
import lk.sasindi.smartcity360.service.HierarchyService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@WebServlet("/hierarchy/*")
public class HierarchyServlet extends HttpServlet {

    private HierarchyService hierarchyService;

    @Override
    public void init() {
        hierarchyService = new HierarchyService();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        String path = request.getPathInfo();

        if ("/data".equals(path)) {
            getHierarchyData(response);
            return;
        }

        if ("/building-types".equals(path)) {
            getBuildingTypes(response);
            return;
        }

        if ("/report/download".equals(path)) {
            downloadUsageReport(request, response);
            return;
        }

        if ("/history".equals(path)) {
            getPowerHistory(request, response);
            return;
        }

        request.setAttribute("activePage", "hierarchy");

        request.getRequestDispatcher(
                "/city-hierarchy.jsp"
        ).forward(request, response);
    }

    private void getHierarchyData(
            HttpServletResponse response
    ) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {

            List<City> cities = hierarchyService.getAllCities();

            StringBuilder json = new StringBuilder("[");

            for (int i = 0; i < cities.size(); i++) {

                if (i > 0) {
                    json.append(",");
                }

                appendComponent(json, cities.get(i));
            }

            json.append("]");

            response.getWriter().write(json.toString());

        } catch (SQLException e) {

            getServletContext().log(
                    "Failed to load city hierarchy.",
                    e
            );

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            response.getWriter().write(
                    "{\"error\":\"Unable to load infrastructure data.\"}"
            );
        }
    }

    private void getBuildingTypes(
            HttpServletResponse response
    ) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {

            List<String> buildingTypes =
                    hierarchyService.getBuildingTypes();

            StringBuilder json = new StringBuilder("[");

            for (int i = 0; i < buildingTypes.size(); i++) {

                if (i > 0) {
                    json.append(",");
                }

                json.append("\"")
                        .append(escapeJSON(buildingTypes.get(i)))
                        .append("\"");
            }

            json.append("]");

            response.getWriter().write(json.toString());

        } catch (SQLException e) {

            getServletContext().log(
                    "Failed to load building types.",
                    e
            );

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            response.getWriter().write(
                    "{\"error\":\"Unable to load building types.\"}"
            );
        }
    }

    private void appendComponent(
            StringBuilder json,
            CityComponent component
    ) {

        json.append("{");

        json.append("\"id\":")
                .append(component.getId())
                .append(",");

        json.append("\"code\":\"")
                .append(escapeJSON(component.getCode()))
                .append("\",");

        json.append("\"name\":\"")
                .append(escapeJSON(component.getName()))
                .append("\",");

        json.append("\"type\":\"")
                .append(escapeJSON(component.getType()))
                .append("\",");

        json.append("\"description\":\"")
                .append(escapeJSON(component.getDescription()))
                .append("\",");

        PowerStatus powerStatus = component.getPowerStatus();

        json.append("\"power\":")
                .append(powerStatus != PowerStatus.OFF)
                .append(",");

        json.append("\"powerStatus\":\"")
                .append(powerStatus.name())
                .append("\",");

        json.append("\"usage\":")
                .append(formatNumber(component.getEnergyUsage()))
                .append(",");

        json.append("\"totalBuildings\":")
                .append(component.getBuildings().size())
                .append(",");

        json.append("\"poweredBuildings\":")
                .append(component.getPoweredBuildings())
                .append(",");

        if (component instanceof Building building) {

            json.append("\"buildingType\":\"")
                    .append(escapeJSON(building.getBuildingType()))
                    .append("\",");

            json.append("\"addressLine1\":\"")
                    .append(escapeJSON(building.getAddressLine1()))
                    .append("\",");

            json.append("\"addressLine2\":\"")
                    .append(escapeJSON(building.getAddressLine2()))
                    .append("\",");
        }

        json.append("\"children\":[");

        List<CityComponent> children = component.getChildren();

        for (int i = 0; i < children.size(); i++) {

            if (i > 0) {
                json.append(",");
            }

            appendComponent(json, children.get(i));
        }

        json.append("]}");
    }

    private String escapeJSON(String value) {

        if (value == null) {
            return "";
        }

        StringBuilder escaped = new StringBuilder();

        for (char character : value.toCharArray()) {

            switch (character) {

                case '"' -> escaped.append("\\\"");
                case '\\' -> escaped.append("\\\\");
                case '\b' -> escaped.append("\\b");
                case '\f' -> escaped.append("\\f");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");

                default -> {

                    if (character < 0x20) {

                        escaped.append(
                                String.format(
                                        "\\u%04x",
                                        (int) character
                                )
                        );

                    } else {

                        escaped.append(character);
                    }
                }
            }
        }

        return escaped.toString();
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        request.setCharacterEncoding("UTF-8");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        try {

            if ("add".equals(action)) {

                addComponent(request, response);

            } else if ("shutdown".equals(action)) {

                changePower(request, response, false);

            } else if ("restore".equals(action)) {

                changePower(request, response, true);

            } else {

                response.setStatus(
                        HttpServletResponse.SC_BAD_REQUEST
                );

                writeError(response, "Invalid action.");
            }

        } catch (IllegalArgumentException e) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST
            );

            writeError(response, e.getMessage());

        } catch (SQLException e) {

            getServletContext().log(
                    "Hierarchy database operation failed.",
                    e
            );


            if ("23000".equals(e.getSQLState())) {

                response.setStatus(
                        HttpServletResponse.SC_CONFLICT
                );

                writeError(
                        response,
                        "The component conflicts with an existing database record."
                );

            } else {

                response.setStatus(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                );

                writeError(
                        response,
                        "The database operation could not be completed."
                );
            }
        }
    }

    private void addComponent(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, IOException {

        int parentId;

        try {

            String parentIdValue = request.getParameter("parentId");

            if (parentIdValue == null || parentIdValue.isBlank()) {
                throw new NumberFormatException();
            }

            parentId = Integer.parseInt(parentIdValue);

        } catch (NumberFormatException e) {

            throw new IllegalArgumentException(
                    "Please select a valid parent component."
            );
        }

        String parentType = request.getParameter("parentType");

        String name = request.getParameter("name");

        String type = request.getParameter("type");

        String description = request.getParameter("description");

        String buildingType = request.getParameter("buildingType");

        String addressLine1 = request.getParameter("addressLine1");

        String addressLine2 = request.getParameter("addressLine2");

        int newId = hierarchyService.addChildComponent(
                parentId,
                parentType,
                type,
                name,
                description,
                buildingType,
                addressLine1,
                addressLine2
        );

        response.setStatus(
                HttpServletResponse.SC_CREATED
        );

        response.getWriter().write(
                "{\"success\":true,\"id\":" + newId +
                        ",\"message\":\"Component created successfully.\"}"
        );
    }

    private void changePower(
            HttpServletRequest request,
            HttpServletResponse response,
            boolean powerOn
    ) throws SQLException, IOException {

        String componentType = request.getParameter("componentType");

        int componentId = parseId(request.getParameter("componentId"));

        PowerOperationResult result = powerOn
                ? hierarchyService.restorePower(
                componentType,
                componentId,
                getLoggedInUserId(request)
        )
                : hierarchyService.shutdownPower(
                componentType,
                componentId,
                getLoggedInUserId(request)
        );

        String action = powerOn ? "restored" : "shut down";

        String message = result.getAffectedBuildings() == 0
                ? "No change was needed. All buildings under "
                + result.getComponentName()
                + " were already "
                + (powerOn ? "powered on." : "powered off.")
                : "Power " + action + " for "
                + result.getComponentName() + ". "
                + result.getAffectedBuildings()
                + " building(s) updated.";

        response.getWriter().write(
                "{\"success\":true,"
                        + "\"message\":\"" + escapeJSON(message) + "\","
                        + "\"operation\":\""
                        + result.getOperation() + "\","
                        + "\"affectedBuildings\":"
                        + result.getAffectedBuildings() + ","
                        + "\"totalBuildings\":"
                        + result.getTotalBuildings() + ","
                        + "\"powerStatus\":\""
                        + result.getResultingStatus().name() + "\"}"
        );
    }

    private void getPowerHistory(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        int limit = 10;

        try {

            String value = request.getParameter("limit");

            if (value != null && !value.isBlank()) {
                limit = Integer.parseInt(value.trim());
            }

        } catch (NumberFormatException e) {
            limit = 10;
        }

        try {

            List<PowerOperationRecord> history =
                    hierarchyService.getPowerHistory(limit);

            StringBuilder json = new StringBuilder();

            json.append("{\"success\":true,\"history\":[");

            for (int i = 0; i < history.size(); i++) {

                PowerOperationRecord record = history.get(i);

                if (i > 0) {
                    json.append(",");
                }

                json.append("{")
                        .append("\"id\":")
                        .append(record.getId()).append(",")
                        .append("\"componentType\":\"")
                        .append(escapeJSON(record.getComponentType()))
                        .append("\",")
                        .append("\"componentId\":")
                        .append(record.getComponentId()).append(",")
                        .append("\"componentName\":\"")
                        .append(escapeJSON(record.getComponentName()))
                        .append("\",")
                        .append("\"operation\":\"")
                        .append(escapeJSON(record.getOperation()))
                        .append("\",")
                        .append("\"affectedBuildings\":")
                        .append(record.getAffectedBuildings()).append(",")
                        .append("\"performedBy\":\"")
                        .append(escapeJSON(record.getPerformedBy()))
                        .append("\",")
                        .append("\"performedAt\":\"")
                        .append(escapeJSON(record.getPerformedAt()))
                        .append("\"}");
            }

            json.append("]}");

            response.getWriter().write(json.toString());

        } catch (SQLException e) {

            getServletContext().log(
                    "Failed to load power operation history.",
                    e
            );

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            writeError(
                    response,
                    "Unable to load the power operation history."
            );
        }
    }

    private void downloadUsageReport(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        try {

            UsageReport report = hierarchyService.getUsageReport(
                    request.getParameter("type"),
                    parseId(request.getParameter("id"))
            );

            String fileName = "usage-report-"
                    + report.getCode().replaceAll("[^A-Za-z0-9_-]", "")
                    + ".pdf";

            String generatedAt = LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern(
                            "dd MMM yyyy, HH:mm",
                            Locale.ENGLISH
                    )
            );

            byte[] content = UsageReportPdf.build(report, generatedAt);

            response.setContentType("application/pdf");

            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=\"" + fileName + "\""
            );

            response.setContentLength(content.length);

            response.getOutputStream().write(content);

        } catch (IllegalArgumentException e) {

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            writeError(response, e.getMessage());

        } catch (SQLException e) {

            getServletContext().log(
                    "Failed to download usage report.",
                    e
            );

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            writeError(
                    response,
                    "Unable to download the usage report."
            );
        }
    }

    private int parseId(String value) {

        try {

            if (value == null || value.isBlank()) {
                throw new NumberFormatException();
            }

            return Integer.parseInt(value.trim());

        } catch (NumberFormatException e) {

            throw new IllegalArgumentException(
                    "Please select a valid component."
            );
        }
    }


    private String formatNumber(double value) {

        return String.format(Locale.ROOT, "%.3f", value);
    }

    private Integer getLoggedInUserId(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session != null
                && session.getAttribute("userId") instanceof Number number) {

            return number.intValue();
        }

        return null;
    }

    private void writeError(
            HttpServletResponse response,
            String message
    ) throws IOException {

        response.getWriter().write(
                "{\"success\":false,\"message\":\"" +
                        escapeJSON(message) +
                        "\"}"
        );
    }
}
