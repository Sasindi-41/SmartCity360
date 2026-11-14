package lk.sasindi.smartcity360.controller;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lk.sasindi.smartcity360.service.StreetlightService;
import lk.sasindi.smartcity360.service.StreetlightService.DuplicateStreetlightException;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet(
        name = "StreetlightServlet",
        urlPatterns = "/api/streetlights"
)
public class StreetlightServlet extends HttpServlet {

    private final StreetlightService streetlightService =
            new StreetlightService();

    private final Jsonb jsonb =
            JsonbBuilder.create();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        prepareResponse(response);

        try {
            Map<String, Object> data =
                    streetlightService.getDashboardData();

            response.setStatus(HttpServletResponse.SC_OK);
            jsonb.toJson(data, response.getWriter());

        } catch (SQLException exception) {
            getServletContext().log(
                    "Streetlight dashboard database error.",
                    exception
            );

            sendError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load streetlight data. Check the server log."
            );

        } catch (Exception exception) {
            getServletContext().log(
                    "Unexpected streetlight dashboard error.",
                    exception
            );

            sendError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An unexpected error occurred while loading streetlights."
            );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        prepareResponse(response);

        try {
            Map<?, ?> payload = readPayload(request);

            int generatedId = streetlightService.createStreetlight(
                    null,
                    integerValue(payload.get("streetId")),
                    integerValue(payload.get("configurationId")),
                    dateValue(payload.get("installationDate")),
                    decimalValue(payload.get("latitude")),
                    decimalValue(payload.get("longitude")),
                    integerValue(payload.get("statusId"))
            );

            String displayId = String.format(
                    java.util.Locale.ROOT, "SL-DEMO-%03d", generatedId);

            Map<String, Object> result = success(
                    displayId + " was successfully created."
            );
            result.put("id", generatedId);
            result.put("displayId", displayId);
            result.put("poleCode", displayId);

            response.setStatus(HttpServletResponse.SC_CREATED);
            jsonb.toJson(result, response.getWriter());

        } catch (DuplicateStreetlightException exception) {
            sendError(response, HttpServletResponse.SC_CONFLICT,
                    exception.getMessage());

        } catch (IllegalArgumentException | DateTimeParseException exception) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                    exception.getMessage());

        } catch (SQLException exception) {
            logDatabaseError("Unable to create streetlight.", exception);
            sendDatabaseError(response, exception, "creating");

        } catch (Exception exception) {
            getServletContext().log(
                    "Invalid streetlight creation request.",
                    exception
            );
            sendError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid request. Please check the submitted details."
            );
        }
    }

    @Override
    protected void doPut(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        prepareResponse(response);

        try {
            Map<?, ?> payload = readPayload(request);

            streetlightService.updateStreetlight(
                    integerValue(payload.get("id")),
                    stringValue(payload.get("poleCode")),
                    integerValue(payload.get("streetId")),
                    integerValue(payload.get("configurationId")),
                    dateValue(payload.get("installationDate")),
                    decimalValue(payload.get("latitude")),
                    decimalValue(payload.get("longitude")),
                    integerValue(payload.get("statusId"))
            );

            response.setStatus(HttpServletResponse.SC_OK);
            jsonb.toJson(
                    success("Streetlight details were successfully updated."),
                    response.getWriter()
            );

        } catch (DuplicateStreetlightException exception) {
            sendError(response, HttpServletResponse.SC_CONFLICT,
                    exception.getMessage());

        } catch (IllegalArgumentException | DateTimeParseException exception) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                    exception.getMessage());

        } catch (SQLException exception) {
            logDatabaseError("Unable to update streetlight.", exception);
            sendDatabaseError(response, exception, "updating");

        } catch (Exception exception) {
            getServletContext().log(
                    "Invalid streetlight update request.",
                    exception
            );
            sendError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid update request. Please check the submitted details."
            );
        }
    }

    @Override
    protected void doDelete(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        prepareResponse(response);

        try {
            int id = integerValue(request.getParameter("id"));

            streetlightService.deleteStreetlight(id);

            response.setStatus(HttpServletResponse.SC_OK);
            jsonb.toJson(
                    success("Streetlight was successfully deleted."),
                    response.getWriter()
            );

        } catch (IllegalArgumentException exception) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                    exception.getMessage());

        } catch (SQLException exception) {
            logDatabaseError("Unable to delete streetlight.", exception);

            if ("23000".equals(exception.getSQLState())) {
                sendError(
                        response,
                        HttpServletResponse.SC_CONFLICT,
                        "This streetlight is referenced by another record and cannot be deleted."
                );
            } else {
                sendError(
                        response,
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Database error while deleting the streetlight."
                );
            }

        } catch (Exception exception) {
            getServletContext().log(
                    "Invalid streetlight deletion request.",
                    exception
            );
            sendError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Unable to process the delete request."
            );
        }
    }

    private Map<?, ?> readPayload(HttpServletRequest request)
            throws IOException {

        Map<?, ?> payload = jsonb.fromJson(request.getReader(), Map.class);

        if (payload == null) {
            throw new IllegalArgumentException("Request body is missing.");
        }

        return payload;
    }

    private void prepareResponse(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");
    }

    private String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private int integerValue(Object value) {
        if (value == null || String.valueOf(value).isBlank()) {
            return 0;
        }

        if (value instanceof Number number) {
            return number.intValue();
        }

        return Integer.parseInt(String.valueOf(value));
    }

    private LocalDate dateValue(Object value) {
        if (value == null || String.valueOf(value).isBlank()) {
            return null;
        }

        try {
            return LocalDate.parse(String.valueOf(value));
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException(
                    "Installation date must use the YYYY-MM-DD format."
            );
        }
    }

    private BigDecimal decimalValue(Object value) {
        if (value == null || String.valueOf(value).isBlank()) {
            return null;
        }

        try {
            return new BigDecimal(String.valueOf(value));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Latitude and longitude must be valid numbers."
            );
        }
    }

    private Map<String, Object> success(String message) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("message", message);
        return result;
    }

    private void sendError(
            HttpServletResponse response,
            int status,
            String message) throws IOException {

        response.setStatus(status);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", false);
        result.put("message", message);

        jsonb.toJson(result, response.getWriter());
    }

    private void logDatabaseError(String message, SQLException exception) {
        getServletContext().log(message, exception);
    }

    private void sendDatabaseError(
            HttpServletResponse response,
            SQLException exception,
            String operation) throws IOException {

        if ("23000".equals(exception.getSQLState())) {
            sendError(
                    response,
                    HttpServletResponse.SC_CONFLICT,
                    "A selected database record is invalid. Please refresh and try again."
            );
        } else {
            sendError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Database error while " + operation + " the streetlight."
            );
        }
    }

    @Override
    public void destroy() {
        try {
            jsonb.close();
        } catch (Exception ignored) {

        }
    }
}
