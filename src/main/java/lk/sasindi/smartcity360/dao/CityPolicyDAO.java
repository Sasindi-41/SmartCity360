
package lk.sasindi.smartcity360.dao;

import lk.sasindi.smartcity360.model.CityPolicy;
import lk.sasindi.smartcity360.model.PolicyEvaluation;
import lk.sasindi.smartcity360.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CityPolicyDAO {

    public List<CityPolicy> getAllPolicies(int userId)
            throws SQLException {
        return getAllPolicies(userId, false);
    }

    public List<CityPolicy> getAllPolicies(int userId, boolean cityWide)
            throws SQLException {

        String sql = """
                SELECT
                    id,
                    policy_name,
                    policy_description,
                    policy_expression,
                    is_active,
                    created_at
                FROM city_policies
                """ + (cityWide ? "" : " WHERE users_id = ? ") + """
                ORDER BY created_at DESC
                """;

        List<CityPolicy> policies = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            if (!cityWide) {
                statement.setInt(1, userId);
            }

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    policies.add(mapPolicy(rs));
                }
            }
        }

        return policies;
    }

    public CityPolicy findByExpression(
            int userId,
            String expression
    ) throws SQLException {
        return findByExpression(userId, expression, false);
    }

    public CityPolicy findByExpression(
            int userId,
            String expression,
            boolean cityWide
    ) throws SQLException {

        String sql = """
                SELECT
                    id,
                    policy_name,
                    policy_description,
                    policy_expression,
                    is_active,
                    created_at
                FROM city_policies
                WHERE """ + (cityWide ? "" : "users_id = ? AND ") + """
                    policy_expression = ?
                ORDER BY id
                LIMIT 1
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            int parameter = 1;
            if (!cityWide) {
                statement.setInt(parameter++, userId);
            }
            statement.setString(parameter, expression);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapPolicy(rs);
                }
            }
        }

        return null;
    }

    public CityPolicy createPolicy(
            int userId,
            String expression
    ) throws SQLException {

        String policyName = createPolicyName(expression);

        String description =
                "Automatically created policy from the City Policies workspace.";

        String sql = """
                INSERT INTO city_policies
                (
                    policy_name,
                    policy_description,
                    policy_expression,
                    is_active,
                    users_id
                )
                VALUES (?, ?, ?, 1, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            statement.setString(1, policyName);
            statement.setString(2, description);
            statement.setString(3, expression);
            statement.setInt(4, userId);

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {

                if (keys.next()) {

                    int generatedId = keys.getInt(1);

                    return findById(connection, generatedId, userId);
                }
            }
        }

        throw new SQLException(
                "Unable to create the city policy."
        );
    }

    private CityPolicy findById(
            Connection connection,
            int policyId,
            int userId
    ) throws SQLException {

        String sql = """
                SELECT
                    id,
                    policy_name,
                    policy_description,
                    policy_expression,
                    is_active,
                    created_at
                FROM city_policies
                WHERE id = ?
                  AND users_id = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, policyId);
            statement.setInt(2, userId);

            try (ResultSet rs = statement.executeQuery()) {

                if (rs.next()) {
                    return mapPolicy(rs);
                }
            }
        }

        return null;
    }

    public int saveEvaluation(
            int userId,
            int policyId,
            String resultName,
            String contextJson,
            String message
    ) throws SQLException {
        return saveEvaluation(userId, policyId, resultName, contextJson, message, false);
    }

    public int saveEvaluation(
            int userId,
            int policyId,
            String resultName,
            String contextJson,
            String message,
            boolean cityWide
    ) throws SQLException {

        String lookupSql = """
                SELECT id
                FROM evaluation_result
                WHERE name = ?
                LIMIT 1
                """;

        String insertSql = """
                INSERT INTO policy_evaluations
                (
                    context_data,
                    evaluation_message,
                    evaluated_at,
                    city_policies_id,
                    evaluation_result_id,
                    users_id
                )
                VALUES (?, ?, NOW(), ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {

                int resultId;

                try (PreparedStatement lookup =
                             connection.prepareStatement(lookupSql)) {

                    lookup.setString(1, resultName);

                    try (ResultSet rs = lookup.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException(
                                    "Evaluation result lookup not found: "
                                            + resultName
                            );
                        }

                        resultId = rs.getInt("id");
                    }
                }

                String ownershipSql = """
                        SELECT id
                        FROM city_policies
                        WHERE id = ?
                        """ + (cityWide ? "" : " AND users_id = ? ");

                try (PreparedStatement ownership =
                             connection.prepareStatement(ownershipSql)) {

                    ownership.setInt(1, policyId);
                    if (!cityWide) {
                        ownership.setInt(2, userId);
                    }

                    try (ResultSet rs = ownership.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException(
                                    "Policy not found for this user."
                            );
                        }
                    }
                }

                int evaluationId;

                try (PreparedStatement insert =
                             connection.prepareStatement(
                                     insertSql,
                                     Statement.RETURN_GENERATED_KEYS
                             )) {

                    insert.setString(1, contextJson);
                    insert.setString(2, message);
                    insert.setInt(3, policyId);
                    insert.setInt(4, resultId);
                    insert.setInt(5, userId);

                    insert.executeUpdate();

                    try (ResultSet keys = insert.getGeneratedKeys()) {

                        if (!keys.next()) {
                            throw new SQLException(
                                    "Evaluation record was not created."
                            );
                        }

                        evaluationId = keys.getInt(1);
                    }
                }

                connection.commit();

                return evaluationId;

            } catch (SQLException ex) {

                connection.rollback();
                throw ex;

            } finally {

                connection.setAutoCommit(true);
            }
        }
    }

    public List<PolicyEvaluation> getRecentEvaluations(
            int userId,
            int limit
    ) throws SQLException {
        return getRecentEvaluations(userId, limit, false);
    }

    public List<PolicyEvaluation> getRecentEvaluations(
            int userId,
            int limit,
            boolean cityWide
    ) throws SQLException {

        String sql = """
                SELECT
                    pe.id,
                    pe.city_policies_id,
                    cp.policy_name,
                    cp.policy_expression,
                    pe.evaluation_message,
                    pe.evaluated_at,
                    er.name AS result_name
                FROM policy_evaluations pe
                INNER JOIN city_policies cp
                    ON pe.city_policies_id = cp.id
                INNER JOIN evaluation_result er
                    ON pe.evaluation_result_id = er.id
                """ + (cityWide ? "" : " WHERE pe.users_id = ? ") + """
                ORDER BY pe.evaluated_at DESC
                LIMIT ?
                """;

        List<PolicyEvaluation> evaluations = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            if (!cityWide) {
                statement.setInt(1, userId);
            }
            statement.setInt(cityWide ? 1 : 2, Math.max(1, Math.min(limit, 50)));

            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {

                    PolicyEvaluation evaluation =
                            new PolicyEvaluation();

                    evaluation.setId(rs.getInt("id"));

                    evaluation.setPolicyId(
                            rs.getInt("city_policies_id")
                    );

                    evaluation.setPolicyName(
                            rs.getString("policy_name")
                    );

                    evaluation.setPolicyExpression(
                            rs.getString("policy_expression")
                    );

                    evaluation.setEvaluationMessage(
                            rs.getString("evaluation_message")
                    );

                    evaluation.setEvaluationResult(
                            rs.getString("result_name")
                    );

                    Timestamp timestamp =
                            rs.getTimestamp("evaluated_at");

                    if (timestamp != null) {
                        evaluation.setEvaluatedAt(
                                timestamp.toLocalDateTime()
                        );
                    }

                    evaluations.add(evaluation);
                }
            }
        }

        return evaluations;
    }

    public int getPolicyCount(int userId) throws SQLException {
        return getPolicyCount(userId, false);
    }

    public int getPolicyCount(int userId, boolean cityWide) throws SQLException {
        String sql = "SELECT COUNT(*) FROM city_policies"
                + (cityWide ? "" : " WHERE users_id = ?");
        return getCount(sql, userId, cityWide);
    }

    public int getEvaluationCount(int userId) throws SQLException {
        return getEvaluationCount(userId, false);
    }

    public int getEvaluationCount(int userId, boolean cityWide) throws SQLException {
        String sql = "SELECT COUNT(*) FROM policy_evaluations"
                + (cityWide ? "" : " WHERE users_id = ?");
        return getCount(sql, userId, cityWide);
    }

    public int getPassedCount(int userId) throws SQLException {
        return getPassedCount(userId, false);
    }

    public int getPassedCount(int userId, boolean cityWide) throws SQLException {
        String sql = """
                SELECT COUNT(*)
                FROM policy_evaluations pe
                INNER JOIN evaluation_result er
                    ON pe.evaluation_result_id = er.id
                WHERE er.name = 'TRUE'
                """ + (cityWide ? "" : " AND pe.users_id = ?");
        return getCount(sql, userId, cityWide);
    }

    private int getCount(String sql, int userId, boolean cityWide)
            throws SQLException {

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            if (!cityWide) {
                statement.setInt(1, userId);
            }

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    private CityPolicy mapPolicy(ResultSet rs)
            throws SQLException {

        CityPolicy policy = new CityPolicy();

        policy.setId(rs.getInt("id"));
        policy.setPolicyName(rs.getString("policy_name"));
        policy.setPolicyDescription(
                rs.getString("policy_description")
        );
        policy.setPolicyExpression(
                rs.getString("policy_expression")
        );
        policy.setActive(rs.getBoolean("is_active"));

        Timestamp timestamp = rs.getTimestamp("created_at");

        if (timestamp != null) {
            policy.setCreatedAt(timestamp.toLocalDateTime());
        }

        return policy;
    }

    private String createPolicyName(String expression) {

        String name = expression
                .replaceAll("\\s+", " ")
                .trim();

        if (name.length() > 70) {
            name = name.substring(0, 67) + "...";
        }

        return "Custom Policy: " + name;
    }
}
