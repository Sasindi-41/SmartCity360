package lk.sasindi.smartcity360.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lk.sasindi.smartcity360.dao.EnergyDAO;
import lk.sasindi.smartcity360.model.EnergyMeter;
import lk.sasindi.smartcity360.model.EnergyProcessResult;
import lk.sasindi.smartcity360.model.StreamProcessingLog;
import lk.sasindi.smartcity360.service.EnergyService;

import java.io.IOException;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@WebServlet("/energy/*")
public class EnergyServlet extends HttpServlet {

    private EnergyService energyService;

    @Override
    public void init() throws ServletException {
        energyService = new EnergyService();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        prepareResponse(response);

        String path = request.getPathInfo();

        try {

            if (path == null || "/".equals(path)) {
                sendError(
                        response,
                        HttpServletResponse.SC_NOT_FOUND,
                        "Energy endpoint not found."
                );
                return;
            }

            switch (path) {

                case "/meters":
                    sendMeters(response);
                    break;

                case "/buildings":
                    sendBuildings(response);
                    break;

                case "/options":
                    sendMeterOptions(response);
                    break;

                case "/summary":
                    sendSummary(response);
                    break;

                case "/history":
                    sendHistory(request, response);
                    break;

                default:
                    sendError(
                            response,
                            HttpServletResponse.SC_NOT_FOUND,
                            "Energy endpoint not found."
                    );
            }

        } catch (Exception ex) {

            log("Energy GET request failed.", ex);

            sendError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to retrieve energy information."
            );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        prepareResponse(response);

        String path = request.getPathInfo();

        if ("/register".equals(path)) {
            try {
                String meterType = requiredParameter(request, "meterType");
                int buildingId = Integer.parseInt(requiredParameter(request, "buildingId"));
                LocalDate installationDate = optionalDate(request.getParameter("installationDate"));

                EnergyDAO.CreatedMeter created = energyService.registerMeter(
                        meterType, buildingId, installationDate);
                response.setStatus(HttpServletResponse.SC_CREATED);
                write(response, "{\"success\":true,\"meterId\":" + created.meterId()
                        + ",\"serialNumber\":" + quote(created.serialNumber())
                        + ",\"message\":\"Energy meter registered successfully.\"}");
            } catch (NumberFormatException ex) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Select a valid assigned building.");
            } catch (IllegalArgumentException ex) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            } catch (Exception ex) {
                log("Energy meter registration failed.", ex);
                sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Unable to register the meter. Check the server log and SMARTCITY_AES_KEY.");
            }
            return;
        }

        if ("/update".equals(path)) {
            try {
                int meterId = Integer.parseInt(requiredParameter(request, "meterId"));
                String meterType = requiredParameter(request, "meterType");
                int buildingId = Integer.parseInt(requiredParameter(request, "buildingId"));
                int statusId = Integer.parseInt(requiredParameter(request, "statusId"));
                LocalDate installationDate = optionalDate(request.getParameter("installationDate"));
                energyService.updateMeter(meterId, meterType, buildingId,
                        installationDate, statusId);
                write(response, "{\"success\":true,\"message\":\"Energy meter updated successfully.\"}");
            } catch (NumberFormatException ex) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Select a valid meter, building and status.");
            } catch (IllegalArgumentException ex) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            } catch (Exception ex) {
                log("Energy meter update failed.", ex);
                sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Unable to update the energy meter.");
            }
            return;
        }

        if ("/token".equals(path)) {
            try {
                int meterId = Integer.parseInt(
                        requiredParameter(request, "meterId")
                );
                EnergyDAO.MeterToken meterToken =
                        energyService.getOrIssueMeterToken(meterId);

                String json = "{"
                        + "\"success\":true,"
                        + "\"token\":" + quote(meterToken.token()) + ","
                        + "\"newlyIssued\":" + meterToken.newlyIssued() + ","
                        + "\"message\":"
                        + quote(meterToken.newlyIssued()
                        ? "A new authentication token was issued for this meter."
                        : "The meter authentication token was loaded.")
                        + "}";

                write(response, json);

            } catch (NumberFormatException ex) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid energy meter ID.");
            } catch (IllegalArgumentException ex) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                        ex.getMessage());
            } catch (Exception ex) {
                log("Unable to retrieve or issue the meter authentication token.", ex);
                sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Unable to load the meter authentication token. Check the server log.");
            }
            return;
        }

        if (!"/process".equals(path)) {
            sendError(
                    response,
                    HttpServletResponse.SC_NOT_FOUND,
                    "Energy endpoint not found."
            );
            return;
        }

        try {

            int meterId = Integer.parseInt(
                    requiredParameter(request, "meterId")
            );

            String token = requiredParameter(request, "token");

            double readingValue = Double.parseDouble(
                    requiredParameter(request, "readingValue")
            );

            String unit = requiredParameter(request, "unit");

            boolean compressionEnabled = isEnabled(
                    request,
                    "compression"
            );

            boolean encryptionEnabled = isEnabled(
                    request,
                    "encryption"
            );

            EnergyProcessResult result =
                    energyService.processReading(
                            meterId,
                            token,
                            readingValue,
                            unit,
                            compressionEnabled,
                            encryptionEnabled
                    );

            response.setStatus(
                    result.isSuccess()
                            ? HttpServletResponse.SC_OK
                            : HttpServletResponse.SC_BAD_REQUEST
            );

            write(response, resultToJson(result));

        } catch (NumberFormatException ex) {

            sendError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid meter ID or reading value."
            );

        } catch (IllegalArgumentException ex) {

            sendError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    ex.getMessage()
            );

        } catch (Exception ex) {

            log("Energy processing request failed.", ex);

            sendError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to save the energy processing result. "
                            + "Check the Payara server log for details."
            );
        }
    }

    private boolean isEnabled(
            HttpServletRequest request,
            String parameter
    ) {

        String value = request.getParameter(parameter);

        if (value == null || value.isBlank()) {
            return false;
        }

        value = value.trim();

        if (value.equalsIgnoreCase("true")
                || value.equalsIgnoreCase("on")
                || value.equals("1")
                || value.equalsIgnoreCase("yes")
                || value.equalsIgnoreCase("checked")) {

            return true;
        }

        if (value.equalsIgnoreCase("false")
                || value.equalsIgnoreCase("off")
                || value.equals("0")
                || value.equalsIgnoreCase("no")) {

            return false;
        }

        throw new IllegalArgumentException(
                "Invalid value for " + parameter + " option."
        );
    }

    private void sendMeters(HttpServletResponse response)
            throws Exception {

        List<EnergyMeter> meters = energyService.getAllMeters();

        StringBuilder json = new StringBuilder();

        json.append("{\"success\":true,\"meters\":[");

        for (int i = 0; i < meters.size(); i++) {

            EnergyMeter meter = meters.get(i);

            if (i > 0) {
                json.append(",");
            }

            json.append("{")
                    .append("\"id\":").append(meter.getId()).append(",")
                    .append("\"serialNumber\":")
                    .append(quote(meter.getSerialNumber())).append(",")
                    .append("\"meterType\":")
                    .append(quote(meter.getMeterType())).append(",")
                    .append("\"installationDate\":")
                    .append(quote(meter.getInstallationDate())).append(",")
                    .append("\"status\":")
                    .append(quote(meter.getStatus())).append(",")
                    .append("\"buildingName\":")
                    .append(quote(meter.getBuildingName())).append(",")
                    .append("\"streetName\":")
                    .append(quote(meter.getStreetName())).append(",")
                    .append("\"districtName\":")
                    .append(quote(meter.getDistrictName())).append(",")
                    .append("\"cityName\":")
                    .append(quote(meter.getCityName())).append(",")
                    .append("\"buildingId\":").append(meter.getBuildingId()).append(",")
                    .append("\"statusId\":").append(meter.getStatusId()).append(",")
                    .append("\"location\":")
                    .append(quote(meter.getLocation()))
                    .append("}");
        }

        json.append("]}");

        write(response, json.toString());
    }

    private void sendBuildings(HttpServletResponse response) throws Exception {
        List<EnergyDAO.BuildingOption> buildings = energyService.getBuildingOptions();
        StringBuilder json = new StringBuilder("{\"success\":true,\"buildings\":[");
        for (int i = 0; i < buildings.size(); i++) {
            if (i > 0) json.append(',');
            EnergyDAO.BuildingOption building = buildings.get(i);
            json.append("{\"id\":").append(building.id())
                    .append(",\"label\":").append(quote(building.label())).append('}');
        }
        json.append("]}");
        write(response, json.toString());
    }

    private void sendMeterOptions(HttpServletResponse response) throws Exception {
        List<String> types = energyService.getMeterTypeOptions();
        List<EnergyDAO.LookupOption> statuses = energyService.getMeterStatusOptions();
        StringBuilder json = new StringBuilder("{\"success\":true,\"meterTypes\":[");
        for (int i = 0; i < types.size(); i++) {
            if (i > 0) json.append(',');
            json.append(quote(types.get(i)));
        }
        json.append("],\"statuses\":[");
        for (int i = 0; i < statuses.size(); i++) {
            if (i > 0) json.append(',');
            EnergyDAO.LookupOption status = statuses.get(i);
            json.append("{\"id\":").append(status.id())
                    .append(",\"name\":").append(quote(status.name())).append('}');
        }
        json.append("]}");
        write(response, json.toString());
    }

    private LocalDate optionalDate(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Enter a valid installation date.");
        }
    }

    private void sendSummary(HttpServletResponse response)
            throws Exception {

        String json = "{"
                + "\"success\":true,"
                + "\"meterCount\":" + energyService.getMeterCount() + ","
                + "\"activeMeterCount\":"
                + energyService.getActiveMeterCount() + ","
                + "\"streamCount\":" + energyService.getStreamCount() + ","
                + "\"successfulStreamCount\":"
                + energyService.getSuccessfulStreamCount() + ","
                + "\"readingCount\":" + energyService.getReadingCount() + ","
                + "\"totalEnergyConsumption\":"
                + energyService.getTotalEnergyConsumption()
                + "}";

        write(response, json);
    }

    private void sendHistory(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {

        int limit = 20;

        String limitParameter = request.getParameter("limit");

        if (limitParameter != null && !limitParameter.isBlank()) {

            try {
                limit = Integer.parseInt(limitParameter);
            } catch (NumberFormatException ignored) {
                limit = 20;
            }
        }

        List<EnergyProcessResult> history =
                energyService.getRecentHistory(limit);

        StringBuilder json = new StringBuilder();

        json.append("{\"success\":true,\"history\":[");

        for (int i = 0; i < history.size(); i++) {

            EnergyProcessResult item = history.get(i);

            if (i > 0) {
                json.append(",");
            }

            json.append("{")
                    .append("\"streamRecordId\":")
                    .append(item.getStreamRecordId()).append(",")
                    .append("\"serialNumber\":")
                    .append(quote(item.getSerialNumber())).append(",")
                    .append("\"receivedAt\":")
                    .append(quote(item.getReceivedAt())).append(",")
                    .append("\"readingValue\":")
                    .append(item.getReadingValue()).append(",")
                    .append("\"unit\":")
                    .append(quote(item.getUnit())).append(",")
                    .append("\"authenticationStatus\":")
                    .append(quote(item.getAuthenticationStatus())).append(",")
                    .append("\"processingStatus\":")
                    .append(quote(item.getProcessingStatus())).append(",")
                    .append("\"encryptionAlgorithm\":")
                    .append(quote(item.getEncryptionAlgorithm())).append(",")
                    .append("\"compressionAlgorithm\":")
                    .append(quote(item.getCompressionAlgorithm()))
                    .append("}");
        }

        json.append("]}");

        write(response, json.toString());
    }

    private String resultToJson(EnergyProcessResult result) {

        StringBuilder json = new StringBuilder("{");

        json.append("\"success\":")
                .append(result.isSuccess()).append(",");

        json.append("\"message\":")
                .append(quote(result.getMessage())).append(",");

        json.append("\"streamRecordId\":")
                .append(result.getStreamRecordId()).append(",");

        json.append("\"authenticationStatus\":")
                .append(quote(result.getAuthenticationStatus())).append(",");

        json.append("\"processingStatus\":")
                .append(quote(result.getProcessingStatus())).append(",");

        json.append("\"encryptionAlgorithm\":")
                .append(quote(result.getEncryptionAlgorithm())).append(",");

        json.append("\"compressionAlgorithm\":")
                .append(quote(result.getCompressionAlgorithm())).append(",");

        json.append("\"readingValue\":")
                .append(result.getReadingValue()).append(",");

        json.append("\"unit\":")
                .append(quote(result.getUnit())).append(",");

        json.append("\"originalSize\":")
                .append(result.getOriginalSize()).append(",");

        json.append("\"finalSize\":")
                .append(result.getFinalSize()).append(",");

        json.append("\"processedPayload\":")
                .append(quote(result.getProcessedPayload())).append(",");

        json.append("\"logs\":[");

        List<StreamProcessingLog> logs = result.getLogs();

        for (int i = 0; i < logs.size(); i++) {

            StreamProcessingLog entry = logs.get(i);

            if (i > 0) {
                json.append(",");
            }

            json.append("{")
                    .append("\"decoratorName\":")
                    .append(quote(entry.getDecoratorName())).append(",")
                    .append("\"operationName\":")
                    .append(quote(entry.getOperationName())).append(",")
                    .append("\"success\":")
                    .append(entry.isSuccess()).append(",")
                    .append("\"details\":")
                    .append(quote(entry.getDetails()))
                    .append("}");
        }

        json.append("]}");

        return json.toString();
    }

    private String requiredParameter(
            HttpServletRequest request,
            String name
    ) {

        String value = request.getParameter(name);

        if (value == null || value.isBlank()) {

            throw new IllegalArgumentException(
                    "Missing required field: " + name
            );
        }

        return value.trim();
    }

    private String quote(String value) {

        if (value == null) {
            return "null";
        }

        StringBuilder escaped = new StringBuilder("\"");

        for (int i = 0; i < value.length(); i++) {

            char ch = value.charAt(i);

            switch (ch) {

                case '"':
                    escaped.append("\\\"");
                    break;

                case '\\':
                    escaped.append("\\\\");
                    break;

                case '\b':
                    escaped.append("\\b");
                    break;

                case '\f':
                    escaped.append("\\f");
                    break;

                case '\n':
                    escaped.append("\\n");
                    break;

                case '\r':
                    escaped.append("\\r");
                    break;

                case '\t':
                    escaped.append("\\t");
                    break;

                default:

                    if (ch < 0x20) {
                        escaped.append("\\u");
                        escaped.append(
                                String.format("%04x", (int) ch)
                        );
                    } else {
                        escaped.append(ch);
                    }
            }
        }

        escaped.append("\"");

        return escaped.toString();
    }

    private void prepareResponse(HttpServletResponse response) {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
    }

    private void write(
            HttpServletResponse response,
            String content
    ) throws IOException {

        response.getWriter().write(content);
    }

    private void sendError(
            HttpServletResponse response,
            int status,
            String message
    ) throws IOException {

        response.setStatus(status);

        write(
                response,
                "{\"success\":false,\"message\":" + quote(message) + "}"
        );
    }
}