
package lk.sasindi.smartcity360.dao;

import lk.sasindi.smartcity360.model.PowerOperationRecord;
import lk.sasindi.smartcity360.patterns.composite.Building;
import lk.sasindi.smartcity360.patterns.composite.City;
import lk.sasindi.smartcity360.patterns.composite.CityComponent;
import lk.sasindi.smartcity360.patterns.composite.District;
import lk.sasindi.smartcity360.patterns.composite.Street;
import lk.sasindi.smartcity360.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HierarchyDAO {

    public List<City> getAllCities() throws SQLException {

        Map<Integer, City> cities = new LinkedHashMap<>();
        Map<Integer, District> districts = new LinkedHashMap<>();
        Map<Integer, Street> streets = new LinkedHashMap<>();

        try (Connection connection = DBConnection.getConnection()) {

            String citySQL = """
                    SELECT id, city_name, description
                    FROM cities
                    ORDER BY id
                    """;

            try (PreparedStatement statement =
                         connection.prepareStatement(citySQL);
                 ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    int id = result.getInt("id");

                    City city = new City(
                            id,
                            result.getString("city_name"),
                            generateCode("CTY", id),
                            result.getString("description")
                    );

                    cities.put(id, city);
                }
            }

            String districtSQL = """
                    SELECT id, district_name, description, cities_id
                    FROM districts
                    ORDER BY id
                    """;

            try (PreparedStatement statement =
                         connection.prepareStatement(districtSQL);
                 ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    int id = result.getInt("id");
                    int cityId = result.getInt("cities_id");

                    District district = new District(
                            id,
                            result.getString("district_name"),
                            generateCode("DST", id),
                            result.getString("description")
                    );

                    City parent = cities.get(cityId);

                    if (parent != null) {
                        parent.addChild(district);
                        districts.put(id, district);
                    }
                }
            }

            String streetSQL = """
                    SELECT id, street_name, description, districts_id
                    FROM streets
                    ORDER BY id
                    """;

            try (PreparedStatement statement =
                         connection.prepareStatement(streetSQL);
                 ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    int id = result.getInt("id");
                    int districtId = result.getInt("districts_id");

                    Street street = new Street(
                            id,
                            result.getString("street_name"),
                            generateCode("STR", id),
                            result.getString("description")
                    );

                    District parent = districts.get(districtId);

                    if (parent != null) {
                        parent.addChild(street);
                        streets.put(id, street);
                    }
                }
            }

            String buildingSQL = """
                    SELECT b.id,
                           b.building_name,
                           b.building_code,
                           b.building_type,
                           b.address_line1,
                           b.address_line2,
                           b.streets_id,
                           b.power_on,
                           COALESCE(u.usage_kwh, 0) AS usage_kwh
                    FROM buildings b
                    LEFT JOIN (
                        SELECT em.buildings_id AS building_id,
                               SUM(
                                   CASE UPPER(er.unit)
                                       WHEN 'WH'  THEN er.reading_value / 1000
                                       WHEN 'MWH' THEN er.reading_value * 1000
                                       ELSE er.reading_value
                                   END
                               ) AS usage_kwh
                        FROM energy_meters em
                        INNER JOIN meter_stream_records msr
                            ON msr.energy_meters_id = em.id
                        INNER JOIN processing_status ps
                            ON msr.processing_status_id = ps.id
                           AND UPPER(ps.name) = 'SUCCESS'
                        INNER JOIN energy_readings er
                            ON er.meter_stream_records_id = msr.id
                        GROUP BY em.buildings_id
                    ) u ON u.building_id = b.id
                    ORDER BY b.id
                    """;

            try (PreparedStatement statement =
                         connection.prepareStatement(buildingSQL);
                 ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    int id = result.getInt("id");
                    int streetId = result.getInt("streets_id");

                    String buildingCode =
                            result.getString("building_code");

                    if (buildingCode == null ||
                            buildingCode.isBlank()) {

                        buildingCode = generateCode("BLD", id);
                    }

                    String addressLine1 =
                            result.getString("address_line1");

                    String addressLine2 =
                            result.getString("address_line2");

                    Building building = new Building(
                            id,
                            result.getString("building_name"),
                            buildingCode,
                            null,
                            result.getString("building_type"),
                            addressLine1,
                            addressLine2,
                            result.getBoolean("power_on"),
                            result.getDouble("usage_kwh")
                    );

                    Street parent = streets.get(streetId);

                    if (parent != null) {
                        parent.addChild(building);
                    }
                }
            }
        }

        return new ArrayList<>(cities.values());
    }

    public boolean parentExists(
            String parentType,
            int parentId
    ) throws SQLException {

        String sql = switch (parentType) {

            case "City" ->
                    "SELECT id FROM cities WHERE id = ?";

            case "District" ->
                    "SELECT id FROM districts WHERE id = ?";

            case "Street" ->
                    "SELECT id FROM streets WHERE id = ?";

            default -> null;
        };

        if (sql == null) {
            return false;
        }

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, parentId);

            try (ResultSet result = statement.executeQuery()) {

                return result.next();
            }
        }
    }

    public boolean componentNameExists(
            String parentType,
            int parentId,
            String componentType,
            String name
    ) throws SQLException {

        String sql = switch (componentType) {

            case "District" -> """
                    SELECT id
                    FROM districts
                    WHERE cities_id = ?
                      AND LOWER(TRIM(district_name)) = LOWER(TRIM(?))
                    """;

            case "Street" -> """
                    SELECT id
                    FROM streets
                    WHERE districts_id = ?
                      AND LOWER(TRIM(street_name)) = LOWER(TRIM(?))
                    """;

            case "Building" -> """
                    SELECT id
                    FROM buildings
                    WHERE streets_id = ?
                      AND LOWER(TRIM(building_name)) = LOWER(TRIM(?))
                    """;

            default -> null;
        };

        if (sql == null) {
            return false;
        }

        boolean validRelationship =
                ("City".equals(parentType) &&
                        "District".equals(componentType)) ||

                        ("District".equals(parentType) &&
                                "Street".equals(componentType)) ||

                        ("Street".equals(parentType) &&
                                "Building".equals(componentType));

        if (!validRelationship) {
            return false;
        }

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, parentId);
            statement.setString(2, name);

            try (ResultSet result = statement.executeQuery()) {

                return result.next();
            }
        }
    }

    public int addCity(
            String name,
            String description
    ) throws SQLException {

        String sql = """
                INSERT INTO cities (city_name, description)
                VALUES (?, ?)
                """;

        return insertRecord(sql, statement -> {
            statement.setString(1, name);
            statement.setString(2, description);
        });
    }

    public int addDistrict(
            int cityId,
            String name,
            String description
    ) throws SQLException {

        String sql = """
                INSERT INTO districts
                    (district_name, description, cities_id)
                VALUES (?, ?, ?)
                """;

        return insertRecord(sql, statement -> {
            statement.setString(1, name);
            statement.setString(2, description);
            statement.setInt(3, cityId);
        });
    }

    public int addStreet(
            int districtId,
            String name,
            String description
    ) throws SQLException {

        String sql = """
                INSERT INTO streets
                    (street_name, description, districts_id)
                VALUES (?, ?, ?)
                """;

        return insertRecord(sql, statement -> {
            statement.setString(1, name);
            statement.setString(2, description);
            statement.setInt(3, districtId);
        });
    }

    public int addBuilding(
            int streetId,
            String name,
            String buildingType,
            String addressLine1,
            String addressLine2
    ) throws SQLException {

        String insertSQL = """
            INSERT INTO buildings
                (building_name,
                 building_code,
                 building_type,
                 streets_id,
                 address_line1,
                 address_line2)
            VALUES (?, NULL, ?, ?, ?, ?)
            """;

        String updateSQL = """
            UPDATE buildings
            SET building_code = ?
            WHERE id = ?
            """;

        try (Connection connection = DBConnection.getConnection()) {

            boolean previousAutoCommit = connection.getAutoCommit();

            try {
                connection.setAutoCommit(false);

                int newId;

                try (PreparedStatement statement =
                             connection.prepareStatement(
                                     insertSQL,
                                     Statement.RETURN_GENERATED_KEYS
                             )) {

                    statement.setString(1, name);
                    statement.setString(2, buildingType);
                    statement.setInt(3, streetId);

                    if (addressLine1 == null || addressLine1.isBlank()) {
                        statement.setNull(4, java.sql.Types.VARCHAR);
                    } else {
                        statement.setString(4, addressLine1.trim());
                    }

                    if (addressLine2 == null || addressLine2.isBlank()) {
                        statement.setNull(5, java.sql.Types.VARCHAR);
                    } else {
                        statement.setString(5, addressLine2.trim());
                    }
                    int affectedRows = statement.executeUpdate();

                    if (affectedRows == 0) {
                        throw new SQLException(
                                "Creating the building failed."
                        );
                    }

                    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {

                        if (!generatedKeys.next()) {
                            throw new SQLException(
                                    "No building ID was returned."
                            );
                        }

                        newId = generatedKeys.getInt(1);
                    }
                }

                String buildingCode = generateCode("BLD", newId);

                try (PreparedStatement statement =
                             connection.prepareStatement(updateSQL)) {

                    statement.setString(1, buildingCode);
                    statement.setInt(2, newId);

                    int affectedRows = statement.executeUpdate();

                    if (affectedRows != 1) {
                        throw new SQLException(
                                "Unable to assign the building code."
                        );
                    }
                }

                connection.commit();

                return newId;

            } catch (SQLException | RuntimeException e) {

                try {
                    connection.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }

                throw e;

            } finally {
                connection.setAutoCommit(previousAutoCommit);
            }
        }
    }

    public void ensurePowerSchema() throws SQLException {

        try (Connection connection = DBConnection.getConnection()) {

            if (!columnExists(connection, "buildings", "power_on")) {

                try (Statement statement = connection.createStatement()) {

                    statement.executeUpdate(
                            "ALTER TABLE buildings " +
                                    "ADD COLUMN power_on TINYINT(1) " +
                                    "NOT NULL DEFAULT 1"
                    );
                }
            }

            try (Statement statement = connection.createStatement()) {

                statement.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS power_operation_log (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            component_type VARCHAR(20) NOT NULL,
                            component_id INT NOT NULL,
                            component_name VARCHAR(150) NOT NULL,
                            operation VARCHAR(20) NOT NULL,
                            affected_buildings INT NOT NULL,
                            performed_by INT NULL,
                            performed_at TIMESTAMP NOT NULL
                                DEFAULT CURRENT_TIMESTAMP
                        )
                        """);
            }
        }
    }

    private boolean columnExists(
            Connection connection,
            String table,
            String column
    ) throws SQLException {

        String sql = """
                SELECT 1
                FROM information_schema.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE()
                  AND TABLE_NAME = ?
                  AND COLUMN_NAME = ?
                LIMIT 1
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, table);
            statement.setString(2, column);

            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    public void applyPowerChange(
            List<Integer> buildingIds,
            boolean powerOn,
            String componentType,
            int componentId,
            String componentName,
            int affectedBuildings,
            Integer performedBy
    ) throws SQLException {

        String updateSQL = """
                UPDATE buildings
                SET power_on = ?
                WHERE id = ?
                """;

        String logSQL = """
                INSERT INTO power_operation_log
                    (component_type,
                     component_id,
                     component_name,
                     operation,
                     affected_buildings,
                     performed_by)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection()) {

            boolean previousAutoCommit = connection.getAutoCommit();

            try {
                connection.setAutoCommit(false);

                try (PreparedStatement statement =
                             connection.prepareStatement(updateSQL)) {

                    for (int buildingId : buildingIds) {

                        statement.setBoolean(1, powerOn);
                        statement.setInt(2, buildingId);
                        statement.addBatch();
                    }

                    statement.executeBatch();
                }

                try (PreparedStatement statement =
                             connection.prepareStatement(logSQL)) {

                    statement.setString(1, componentType);
                    statement.setInt(2, componentId);
                    statement.setString(3, componentName);
                    statement.setString(
                            4,
                            powerOn ? "RESTORE" : "SHUTDOWN"
                    );
                    statement.setInt(5, affectedBuildings);

                    if (performedBy == null) {
                        statement.setNull(6, Types.INTEGER);
                    } else {
                        statement.setInt(6, performedBy);
                    }

                    statement.executeUpdate();
                }

                connection.commit();

            } catch (SQLException | RuntimeException e) {

                try {
                    connection.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }

                throw e;

            } finally {
                connection.setAutoCommit(previousAutoCommit);
            }
        }
    }

    public List<PowerOperationRecord> getRecentPowerOperations(
            int limit
    ) throws SQLException {

        if (limit < 1 || limit > 50) {
            limit = 10;
        }

        List<PowerOperationRecord> history = new ArrayList<>();

        String sql = """
                SELECT l.id,
                       l.component_type,
                       l.component_id,
                       l.component_name,
                       l.operation,
                       l.affected_buildings,
                       COALESCE(u.full_name, 'Unknown user') AS performed_by,
                       DATE_FORMAT(l.performed_at, '%d %b, %H:%i')
                           AS performed_at
                FROM power_operation_log l
                LEFT JOIN users u
                    ON u.id = l.performed_by
                ORDER BY l.id DESC
                LIMIT ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, limit);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    history.add(new PowerOperationRecord(
                            result.getInt("id"),
                            result.getString("component_type"),
                            result.getInt("component_id"),
                            result.getString("component_name"),
                            result.getString("operation"),
                            result.getInt("affected_buildings"),
                            result.getString("performed_by"),
                            result.getString("performed_at")
                    ));
                }
            }
        }

        return history;
    }

    private int insertRecord(
            String sql,
            StatementSetter setter
    ) throws SQLException {

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS
             )) {

            setter.setValues(statement);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {

                throw new SQLException(
                        "Creating the hierarchy component failed."
                );
            }

            try (ResultSet generatedKeys =
                         statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }

        throw new SQLException(
                "Creating the component failed. No ID was returned."
        );
    }

    private String generateCode(String prefix, int id) {

        return prefix + "-" + String.format("%03d", id);
    }

    @FunctionalInterface
    private interface StatementSetter {

        void setValues(PreparedStatement statement)
                throws SQLException;
    }
    public List<String> getBuildingTypes() throws SQLException {
        List<String> buildingTypes = new ArrayList<>();

        String sql = """
        SELECT DISTINCT TRIM(building_type) AS building_type
        FROM buildings
        WHERE building_type IS NOT NULL
          AND TRIM(building_type) <> ''
        ORDER BY building_type
        """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                buildingTypes.add(resultSet.getString("building_type"));
            }
        }

        return buildingTypes;
    }

    public boolean buildingTypeExists(String buildingType) throws SQLException {
        if (buildingType == null || buildingType.isBlank()) {
            return false;
        }

        String sql = """
        SELECT 1
        FROM buildings
        WHERE LOWER(TRIM(building_type)) = LOWER(TRIM(?))
        LIMIT 1
        """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, buildingType.trim());

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
}
