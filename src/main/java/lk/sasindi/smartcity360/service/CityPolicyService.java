
package lk.sasindi.smartcity360.service;

import lk.sasindi.smartcity360.dao.CityPolicyDAO;
import lk.sasindi.smartcity360.model.CityPolicy;
import lk.sasindi.smartcity360.model.PolicyContext;
import lk.sasindi.smartcity360.model.PolicyEvaluation;
import lk.sasindi.smartcity360.patterns.interpreter.ComparisonExpression;
import lk.sasindi.smartcity360.patterns.interpreter.Expression;
import lk.sasindi.smartcity360.patterns.interpreter.LogicalExpression;
import lk.sasindi.smartcity360.patterns.interpreter.NotExpression;
import lk.sasindi.smartcity360.patterns.interpreter.PolicyParser;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CityPolicyService {

    private final CityPolicyDAO cityPolicyDAO;

    public CityPolicyService() {
        this.cityPolicyDAO = new CityPolicyDAO();
    }

    public Map<String, Object> evaluatePolicy(
            int userId,
            String expression,
            double noiseLevel,
            double parkingHour,
            double airQuality,
            double trafficLevel
    ) throws SQLException {
        return evaluatePolicy(userId, 1, expression, noiseLevel, parkingHour,
                airQuality, trafficLevel);
    }

    public Map<String, Object> evaluatePolicy(
            int userId,
            int roleId,
            String expression,
            double noiseLevel,
            double parkingHour,
            double airQuality,
            double trafficLevel
    ) throws SQLException {

        boolean cityWide = roleId == 4 || roleId == 5;

        if (userId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid user session."
            );
        }

        if (expression == null || expression.isBlank()) {
            throw new IllegalArgumentException(
                    "Please enter a policy expression."
            );
        }

        expression = expression.trim();

        if (expression.length() > 1000) {
            throw new IllegalArgumentException(
                    "Policy expression cannot exceed 1000 characters."
            );
        }

        validateContext(
                noiseLevel,
                parkingHour,
                airQuality,
                trafficLevel
        );

        PolicyContext context = new PolicyContext(
                noiseLevel,
                parkingHour,
                airQuality,
                trafficLevel
        );

        Expression parsedExpression =
                new PolicyParser(expression).parse();

        boolean result = parsedExpression.interpret(context);

        String resultName = result ? "TRUE" : "FALSE";

        String message = result
                ? "The policy conditions are satisfied by the current city values."
                : "The policy conditions are not satisfied by the current city values.";

        CityPolicy policy = cityPolicyDAO.findByExpression(
                userId,
                expression,
                cityWide
        );

        if (policy == null) {
            policy = cityPolicyDAO.createPolicy(
                    userId,
                    expression
            );
        }

        if (policy == null) {
            throw new SQLException(
                    "Unable to identify the policy record."
            );
        }

        String contextJson = createContextJson(context);

        int evaluationId = cityPolicyDAO.saveEvaluation(
                userId,
                policy.getId(),
                resultName,
                contextJson,
                message,
                cityWide
        );

        List<Map<String, Object>> conditions =
                new ArrayList<>();

        collectConditions(
                parsedExpression,
                context,
                conditions
        );

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("success", true);
        response.put("evaluationId", evaluationId);
        response.put("policyId", policy.getId());
        response.put("policyName", policy.getPolicyName());
        response.put("expression", expression);
        response.put("result", resultName);
        response.put("passed", result);
        response.put("message", message);
        response.put("context", context.getValues());
        response.put("conditions", conditions);

        return response;
    }

    private void validateContext(
            double noiseLevel,
            double parkingHour,
            double airQuality,
            double trafficLevel
    ) {

        if (!Double.isFinite(noiseLevel)
                || !Double.isFinite(parkingHour)
                || !Double.isFinite(airQuality)
                || !Double.isFinite(trafficLevel)) {

            throw new IllegalArgumentException(
                    "All city environment values must be valid finite numbers."
            );
        }

        if (noiseLevel < 0 || noiseLevel > 200) {
            throw new IllegalArgumentException(
                    "Noise level must be between 0 and 200."
            );
        }

        if (parkingHour < 0
                || parkingHour > 23
                || parkingHour != Math.floor(parkingHour)) {

            throw new IllegalArgumentException(
                    "Parking hour must be a whole number between 0 and 23."
            );
        }

        if (airQuality < 0 || airQuality > 500) {
            throw new IllegalArgumentException(
                    "Air quality must be between 0 and 500."
            );
        }

        if (trafficLevel < 0 || trafficLevel > 100) {
            throw new IllegalArgumentException(
                    "Traffic level must be between 0 and 100."
            );
        }
    }

    private String createContextJson(PolicyContext context) {

        Map<String, Double> values = context.getValues();

        return "{"
                + "\"NOISE_LEVEL\":" + values.get("NOISE_LEVEL") + ","
                + "\"PARKING_HOUR\":" + values.get("PARKING_HOUR") + ","
                + "\"AIR_QUALITY\":" + values.get("AIR_QUALITY") + ","
                + "\"TRAFFIC_LEVEL\":" + values.get("TRAFFIC_LEVEL")
                + "}";
    }

    private void collectConditions(
            Expression expression,
            PolicyContext context,
            List<Map<String, Object>> conditions
    ) {

        if (expression instanceof ComparisonExpression comparison) {

            String variable = comparison.getVariable();
            String operator = comparison.getOperator();
            double expected = comparison.getExpectedValue();
            double actual = context.getValue(variable);

            boolean result = comparison.interpret(context);

            Map<String, Object> condition =
                    new LinkedHashMap<>();

            condition.put("variable", variable);
            condition.put("operator", operator);
            condition.put("expected", expected);
            condition.put("actual", actual);
            condition.put("result", result);
            condition.put(
                    "description",
                    variable + " " + operator + " " + expected
            );

            conditions.add(condition);

        } else if (expression instanceof LogicalExpression logical) {

            collectConditions(
                    logical.getLeftExpression(),
                    context,
                    conditions
            );

            collectConditions(
                    logical.getRightExpression(),
                    context,
                    conditions
            );

        } else if (expression instanceof NotExpression not) {

            collectConditions(
                    not.getExpression(),
                    context,
                    conditions
            );
        }
    }

    public List<CityPolicy> getAllPolicies(int userId)
            throws SQLException {
        return getAllPolicies(userId, 1);
    }

    public List<CityPolicy> getAllPolicies(int userId, int roleId)
            throws SQLException {
        return cityPolicyDAO.getAllPolicies(userId, roleId == 4 || roleId == 5);
    }

    public List<PolicyEvaluation> getRecentEvaluations(int userId)
            throws SQLException {
        return getRecentEvaluations(userId, 1);
    }

    public List<PolicyEvaluation> getRecentEvaluations(int userId, int roleId)
            throws SQLException {
        return cityPolicyDAO.getRecentEvaluations(userId, 5, roleId == 4 || roleId == 5);
    }

    public Map<String, Object> getSummary(int userId)
            throws SQLException {
        return getSummary(userId, 1);
    }

    public Map<String, Object> getSummary(int userId, int roleId)
            throws SQLException {

        boolean cityWide = roleId == 4 || roleId == 5;
        Map<String, Object> summary = new LinkedHashMap<>();

        summary.put(
                "evaluations",
                cityPolicyDAO.getEvaluationCount(userId, cityWide)
        );

        summary.put(
                "policies",
                cityPolicyDAO.getPolicyCount(userId, cityWide)
        );

        summary.put(
                "passed",
                cityPolicyDAO.getPassedCount(userId, cityWide)
        );

        return summary;
    }
}
