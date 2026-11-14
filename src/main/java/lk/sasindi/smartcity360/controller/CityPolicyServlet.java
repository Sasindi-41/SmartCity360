
package lk.sasindi.smartcity360.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import lk.sasindi.smartcity360.model.CityPolicy;
import lk.sasindi.smartcity360.model.PolicyEvaluation;
import lk.sasindi.smartcity360.service.CityPolicyService;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/city-policies/*")
public class CityPolicyServlet extends HttpServlet {

    private CityPolicyService cityPolicyService;

    @Override
    public void init() throws ServletException {
        cityPolicyService = new CityPolicyService();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        try {
            int userId = getAuthenticatedUserId(request);
            int roleId = getAuthenticatedRoleId(request);
            String path = request.getPathInfo();
            if (path == null || path.equals("/")) {
                sendError(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        "A valid City Policies endpoint is required."
                );
                return;
            }
            switch (path) {

                case "/summary" -> {

                    Map<String, Object> summary =
                            cityPolicyService.getSummary(userId, roleId);

                    sendJson(response, summary);
                }

                case "/policies" -> {

                    List<CityPolicy> policies =
                            cityPolicyService.getAllPolicies(userId, roleId);

                    sendJson(response, policiesToJson(policies));
                }

                case "/history" -> {

                    List<PolicyEvaluation> history =
                            cityPolicyService.getRecentEvaluations(userId, roleId);

                    sendJson(response, historyToJson(history));
                }

                default -> sendError(
                        response,
                        HttpServletResponse.SC_NOT_FOUND,
                        "City Policies endpoint not found."
                );
            }

        } catch (SecurityException ex) {

            sendError(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    ex.getMessage()
            );

        } catch (Exception ex) {

            log("City Policies GET request failed.", ex);

            sendError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load City Policies data."
            );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        try {

            int userId = getAuthenticatedUserId(request);
            int roleId = getAuthenticatedRoleId(request);

            String path = request.getPathInfo();

            if ("/evaluate".equals(path)) {

                String expression = requiredParameter(
                        request,
                        "expression"
                );

                double noiseLevel = requiredNumber(
                        request,
                        "noiseLevel"
                );

                double parkingHour = requiredNumber(
                        request,
                        "parkingHour"
                );

                double airQuality = requiredNumber(
                        request,
                        "airQuality"
                );

                double trafficLevel = requiredNumber(
                        request,
                        "trafficLevel"
                );

                Map<String, Object> result =
                        cityPolicyService.evaluatePolicy(
                                userId,
                                roleId,
                                expression,
                                noiseLevel,
                                parkingHour,
                                airQuality,
                                trafficLevel
                        );

                sendJson(response, result);

                return;
            }

            sendError(
                    response,
                    HttpServletResponse.SC_NOT_FOUND,
                    "City Policies endpoint not found."
            );

        } catch (SecurityException ex) {

            sendError(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    ex.getMessage()
            );

        } catch (IllegalArgumentException ex) {

            sendError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    ex.getMessage()
            );

        } catch (Exception ex) {

            log("City Policies POST request failed.", ex);

            sendError(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to process the policy evaluation."
            );
        }
    }

    private int getAuthenticatedUserId(
            HttpServletRequest request
    ) {

        HttpSession session = request.getSession(false);

        if (session == null
                || session.getAttribute("userId") == null) {

            throw new SecurityException(
                    "Your session has expired. Please sign in again."
            );
        }

        Object value = session.getAttribute("userId");

        try {

            int userId = Integer.parseInt(value.toString());

            if (userId <= 0) {
                throw new NumberFormatException();
            }

            return userId;

        } catch (NumberFormatException ex) {

            throw new SecurityException(
                    "Invalid user session."
            );
        }
    }

    private int getAuthenticatedRoleId(
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        if (session == null || !(session.getAttribute("roleId") instanceof Number value)) {
            throw new SecurityException("Invalid user session.");
        }

        int roleId = value.intValue();
        if (roleId < 1 || roleId > 5) {
            throw new SecurityException("Invalid user role.");
        }
        return roleId;
    }

    private String requiredParameter(
            HttpServletRequest request,
            String name
    ) {

        String value = request.getParameter(name);

        if (value == null || value.isBlank()) {

            throw new IllegalArgumentException(
                    "Missing required parameter: " + name
            );
        }

        return value.trim();
    }

    private double requiredNumber(
            HttpServletRequest request,
            String name
    ) {

        String value = requiredParameter(request, name);

        try {

            double number = Double.parseDouble(value);

            if (!Double.isFinite(number)) {
                throw new NumberFormatException();
            }

            return number;

        } catch (NumberFormatException ex) {

            throw new IllegalArgumentException(
                    "Invalid numeric value for " + name + "."
            );
        }
    }

    private List<Map<String, Object>> policiesToJson(
            List<CityPolicy> policies
    ) {

        return policies.stream().map(policy -> {

            Map<String, Object> item = new LinkedHashMap<>();

            item.put("id", policy.getId());
            item.put("policyName", policy.getPolicyName());
            item.put(
                    "policyDescription",
                    policy.getPolicyDescription()
            );
            item.put(
                    "policyExpression",
                    policy.getPolicyExpression()
            );
            item.put("active", policy.isActive());
            item.put(
                    "createdAt",
                    policy.getCreatedAt() == null
                            ? null
                            : policy.getCreatedAt().toString()
            );

            return item;

        }).toList();
    }

    private List<Map<String, Object>> historyToJson(
            List<PolicyEvaluation> history
    ) {

        return history.stream().map(evaluation -> {

            Map<String, Object> item = new LinkedHashMap<>();

            item.put("id", evaluation.getId());
            item.put("policyId", evaluation.getPolicyId());
            item.put("policyName", evaluation.getPolicyName());
            item.put(
                    "expression",
                    evaluation.getPolicyExpression()
            );
            item.put(
                    "result",
                    evaluation.getEvaluationResult()
            );
            item.put(
                    "message",
                    evaluation.getEvaluationMessage()
            );
            item.put(
                    "evaluatedAt",
                    evaluation.getEvaluatedAt() == null
                            ? null
                            : evaluation.getEvaluatedAt().toString()
            );

            return item;

        }).toList();
    }

    private void sendJson(
            HttpServletResponse response,
            Object data
    ) throws IOException {

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(toJson(data));
    }

    private void sendError(
            HttpServletResponse response,
            int status,
            String message
    ) throws IOException {

        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> error = new LinkedHashMap<>();

        error.put("success", false);
        error.put("message", message);

        response.getWriter().write(toJson(error));
    }

    private String toJson(Object value) {

        if (value == null) {
            return "null";
        }

        if (value instanceof String string) {
            return quote(string);
        }

        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }

        if (value instanceof Map<?, ?> map) {

            StringBuilder json = new StringBuilder("{");

            boolean first = true;

            for (Map.Entry<?, ?> entry : map.entrySet()) {

                if (!first) {
                    json.append(",");
                }

                json.append(quote(String.valueOf(entry.getKey())));
                json.append(":");
                json.append(toJson(entry.getValue()));

                first = false;
            }

            return json.append("}").toString();
        }

        if (value instanceof Iterable<?> iterable) {

            StringBuilder json = new StringBuilder("[");

            boolean first = true;

            for (Object item : iterable) {

                if (!first) {
                    json.append(",");
                }

                json.append(toJson(item));

                first = false;
            }

            return json.append("]").toString();
        }

        return quote(value.toString());
    }

    private String quote(String value) {

        if (value == null) {
            return "null";
        }

        StringBuilder result = new StringBuilder("\"");

        for (int i = 0; i < value.length(); i++) {

            char character = value.charAt(i);

            switch (character) {

                case '"' -> result.append("\\\"");
                case '\\' -> result.append("\\\\");
                case '\b' -> result.append("\\b");
                case '\f' -> result.append("\\f");
                case '\n' -> result.append("\\n");
                case '\r' -> result.append("\\r");
                case '\t' -> result.append("\\t");

                default -> {

                    if (character < 0x20) {

                        result.append(String.format(
                                "\\u%04x",
                                (int) character
                        ));

                    } else {
                        result.append(character);
                    }
                }
            }
        }

        return result.append("\"").toString();
    }
}
