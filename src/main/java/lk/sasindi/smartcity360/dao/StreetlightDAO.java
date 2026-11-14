
package lk.sasindi.smartcity360.dao;

import lk.sasindi.smartcity360.model.Streetlight;
import lk.sasindi.smartcity360.model.StreetlightConfiguration;
import lk.sasindi.smartcity360.patterns.flyweight.StreetlightFlyweightFactory;
import lk.sasindi.smartcity360.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Locale;

public class StreetlightDAO {

    private final StreetlightFlyweightFactory factory =
            StreetlightFlyweightFactory.getInstance();

    public synchronized Map<String, Object> getDashboardData()
            throws SQLException {

        List<Map<String, Object>> configurations =
                loadConfigurations();

        Set<String> validHashes = new HashSet<>();

        for (Map<String, Object> configuration : configurations) {
            validHashes.add(String.valueOf(configuration.get("configHash")));
        }

        factory.retainOnly(validHashes);

        List<Map<String, Object>> streetlights =
                loadStreetlights();

        List<Map<String, Object>> districts =
                loadDistricts();

        List<Map<String, Object>> streets =
                loadStreets();

        List<Map<String, Object>> statuses =
                loadStatuses();

        Set<Integer> usedConfigurationIds = new HashSet<>();

        int activeLights = 0;

        for (Map<String, Object> light : streetlights) {

            Object configurationId =
                    light.get("configurationId");

            if (configurationId instanceof Number number) {
                usedConfigurationIds.add(number.intValue());
            }

            if ("Active".equalsIgnoreCase(
                    String.valueOf(light.get("statusName")))) {

                activeLights++;
            }
        }

        int totalLights = streetlights.size();

        int usedConfigurations =
                usedConfigurationIds.size();

        double averageReuse = usedConfigurations == 0
                ? 0.0
                : (double) totalLights / usedConfigurations;

        averageReuse = Math.round(averageReuse * 10.0) / 10.0;

        Map<String, Object> summary = new LinkedHashMap<>();

        summary.put("totalLights", totalLights);
        summary.put("totalConfigurations", configurations.size());
        summary.put("activeLights", activeLights);
        summary.put("usedConfigurations", usedConfigurations);
        summary.put("averageReuse", averageReuse);
        summary.put("sharedObjects", factory.getCreatedCount());

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("success", true);
        response.put("summary", summary);
        response.put("streetlights", streetlights);
        response.put("configurations", configurations);
        response.put("districts", districts);
        response.put("streets", streets);
        response.put("statuses", statuses);

        return response;
    }

    private List<Map<String, Object>> loadConfigurations()
            throws SQLException {

        List<Map<String, Object>> result = new ArrayList<>();

        String sql = """
                SELECT
                    id,
                    config_name,
                    brightness_percent,
                    color_temperature,
                    operating_mode,
                    motion_sensor_enabled,
                    power_rating_watts,
                    config_hash
                FROM streetlight_configurations
                ORDER BY config_name, id
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {

                BigDecimal power =
                        rs.getBigDecimal("power_rating_watts");

                Double wattage = power == null
                        ? null
                        : power.doubleValue();

                StreetlightConfiguration databaseConfiguration =
                        new StreetlightConfiguration(
                                rs.getInt("id"),
                                rs.getString("config_name"),
                                rs.getInt("brightness_percent"),
                                rs.getInt("color_temperature"),
                                rs.getString("operating_mode"),
                                rs.getBoolean("motion_sensor_enabled"),
                                wattage,
                                rs.getString("config_hash")
                        );

                StreetlightConfiguration sharedConfiguration =
                        factory.getFlyweight(databaseConfiguration);

                Map<String, Object> item = new LinkedHashMap<>();

                item.put("id", sharedConfiguration.getId());
                item.put("configName",
                        sharedConfiguration.getConfigName());
                item.put("brightnessPercent",
                        sharedConfiguration.getBrightnessPercent());
                item.put("colorTemperature",
                        sharedConfiguration.getColorTemperature());
                item.put("operatingMode",
                        sharedConfiguration.getOperatingMode());
                item.put("motionSensorEnabled",
                        sharedConfiguration.isMotionSensorEnabled());
                item.put("powerRatingWatts",
                        sharedConfiguration.getPowerRatingWatts());
                item.put("configHash",
                        sharedConfiguration.getConfigHash());

                result.add(item);
            }
        }

        return result;
    }

    private List<Map<String, Object>> loadStreetlights()
            throws SQLException {

        List<Map<String, Object>> result = new ArrayList<>();

        for (Streetlight light : loadStreetlightObjects()) {

            StreetlightConfiguration sharedConfiguration =
                    light.getConfiguration();

            Map<String, Object> item = new LinkedHashMap<>();

            item.put("id", light.getId());

            item.put("displayId", light.getPoleCode());
            item.put("poleCode", light.getPoleCode());

            item.put(
                    "installationDate",
                    light.getInstallationDate() == null
                            ? null
                            : light.getInstallationDate().toString()
            );

            item.put("latitude", light.getLatitude());
            item.put("longitude", light.getLongitude());

            item.put("streetId", light.getStreetId());
            item.put("streetName", light.getStreetName());

            item.put("districtId", light.getDistrictId());
            item.put("districtName", light.getDistrictName());

            item.put("configurationId", sharedConfiguration.getId());
            item.put("configName", sharedConfiguration.getConfigName());
            item.put("wattage", sharedConfiguration.getPowerRatingWatts());
            item.put("powerRatingWatts", sharedConfiguration.getPowerRatingWatts());
            item.put("brightnessPercent", sharedConfiguration.getBrightnessPercent());
            item.put("colorTemperature", sharedConfiguration.getColorTemperature());
            item.put("operatingMode", sharedConfiguration.getOperatingMode());
            item.put("motionSensorEnabled", sharedConfiguration.isMotionSensorEnabled());

            item.put("statusName", light.getStatusName());
            item.put("statusId", light.getStatusId());

            result.add(item);
        }

        return result;
    }

    public List<Streetlight> loadStreetlightObjects()
            throws SQLException {

        List<Streetlight> result = new ArrayList<>();

        String sql = """
                SELECT
                    sl.id,
                    sl.pole_code,
                    sl.installation_date,
                    sl.latitude,
                    sl.longitude,
                    sl.streets_id,

                    s.street_name,

                    d.id AS district_id,
                    d.district_name,

                    sl.streetlight_configurations_id,

                    sc.config_name,
                    sc.brightness_percent,
                    sc.color_temperature,
                    sc.operating_mode,
                    sc.motion_sensor_enabled,
                    sc.power_rating_watts,
                    sc.config_hash,

                    st.id AS status_id,
                    st.name AS status_name

                FROM streetlights sl

                INNER JOIN streets s
                    ON s.id = sl.streets_id

                INNER JOIN districts d
                    ON d.id = s.districts_id

                INNER JOIN streetlight_configurations sc
                    ON sc.id = sl.streetlight_configurations_id

                INNER JOIN status st
                    ON st.id = sl.status_id

                ORDER BY sl.id DESC
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {

                StreetlightConfiguration sharedConfiguration =
                        factory.find(rs.getString("config_hash"));

                if (sharedConfiguration == null) {

                    BigDecimal power =
                            rs.getBigDecimal("power_rating_watts");

                    Double wattage = power == null
                            ? null
                            : power.doubleValue();

                    sharedConfiguration = factory.getFlyweight(
                            new StreetlightConfiguration(
                                    rs.getInt("streetlight_configurations_id"),
                                    rs.getString("config_name"),
                                    rs.getInt("brightness_percent"),
                                    rs.getInt("color_temperature"),
                                    rs.getString("operating_mode"),
                                    rs.getBoolean("motion_sensor_enabled"),
                                    wattage,
                                    rs.getString("config_hash")
                            )
                    );
                }

                java.sql.Date installationDate =
                        rs.getDate("installation_date");

                result.add(new Streetlight(
                        rs.getInt("id"),
                        rs.getString("pole_code"),
                        installationDate == null
                                ? null
                                : installationDate.toLocalDate(),
                        rs.getString("street_name"),
                        rs.getString("district_name"),
                        rs.getString("status_name"),
                        rs.getInt("streets_id"),
                        sharedConfiguration,
                        rs.getObject("latitude"),
                        rs.getObject("longitude"),
                        rs.getInt("district_id"),
                        rs.getInt("status_id")
                ));
            }
        }

        return result;
    }

    private List<Map<String, Object>> loadDistricts()
            throws SQLException {

        List<Map<String, Object>> result = new ArrayList<>();

        String sql = """
                SELECT id, district_name
                FROM districts
                ORDER BY district_name
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {

                Map<String, Object> item = new LinkedHashMap<>();

                item.put("id", rs.getInt("id"));
                item.put("districtName",
                        rs.getString("district_name"));

                result.add(item);
            }
        }

        return result;
    }

    private List<Map<String, Object>> loadStreets()
            throws SQLException {

        List<Map<String, Object>> result = new ArrayList<>();

        String sql = """
                SELECT
                    s.id,
                    s.street_name,
                    s.districts_id,
                    d.district_name

                FROM streets s

                INNER JOIN districts d
                    ON d.id = s.districts_id

                ORDER BY d.district_name, s.street_name
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {

                Map<String, Object> item = new LinkedHashMap<>();

                item.put("id", rs.getInt("id"));
                item.put("streetName",
                        rs.getString("street_name"));
                item.put("districtId",
                        rs.getInt("districts_id"));
                item.put("districtName",
                        rs.getString("district_name"));

                result.add(item);
            }
        }

        return result;
    }

    public void createStreetlight(
            String poleCode,
            int streetId,
            int configurationId) throws SQLException {

        createStreetlight(
                poleCode,
                streetId,
                configurationId,
                null,
                null,
                null,
                findStatusIdByName("Active")
        );
    }


    public int createStreetlight(
            String ignoredPoleCode,
            int streetId,
            int configurationId,
            LocalDate installationDate,
            BigDecimal latitude,
            BigDecimal longitude,
            int statusId) throws SQLException {

        String sql = """
                INSERT INTO streetlights (
                    pole_code,
                    installation_date,
                    latitude,
                    longitude,
                    streets_id,
                    streetlight_configurations_id,
                    status_id
                )
                SELECT ?, ?, ?, ?, ?, ?, ?
                WHERE EXISTS (SELECT 1 FROM streets WHERE id = ?)
                  AND EXISTS (SELECT 1 FROM streetlight_configurations WHERE id = ?)
                  AND EXISTS (SELECT 1 FROM status WHERE id = ?)
                """;

        try (Connection connection = DBConnection.getConnection()) {
            boolean previousAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                int generatedId;
                String temporaryCode = "PENDING-" + java.util.UUID.randomUUID();

                try (PreparedStatement statement = connection.prepareStatement(
                        sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                    statement.setString(1, temporaryCode);
                    if (installationDate == null) {
                        statement.setNull(2, java.sql.Types.DATE);
                    } else {
                        statement.setDate(2, Date.valueOf(installationDate));
                    }
                    statement.setBigDecimal(3, latitude);
                    statement.setBigDecimal(4, longitude);
                    statement.setInt(5, streetId);
                    statement.setInt(6, configurationId);
                    statement.setInt(7, statusId);
                    statement.setInt(8, streetId);
                    statement.setInt(9, configurationId);
                    statement.setInt(10, statusId);

                    if (statement.executeUpdate() != 1) {
                        throw new SQLException(
                                "Unable to create streetlight. Check the selected "
                                        + "street, configuration and status."
                        );
                    }

                    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                        if (!generatedKeys.next()) {
                            throw new SQLException(
                                    "Streetlight was created but its generated ID could not be retrieved."
                            );
                        }
                        generatedId = generatedKeys.getInt(1);
                    }
                }

                String generatedStreetlightId = formatDisplayId(generatedId);
                try (PreparedStatement update = connection.prepareStatement(
                        "UPDATE streetlights SET pole_code = ? WHERE id = ?")) {
                    update.setString(1, generatedStreetlightId);
                    update.setInt(2, generatedId);
                    if (update.executeUpdate() != 1) {
                        throw new SQLException(
                                "Unable to save the generated Streetlight ID."
                        );
                    }
                }

                connection.commit();
                return generatedId;
            } catch (SQLException | RuntimeException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(previousAutoCommit);
            }
        }
    }

    private String formatDisplayId(int databaseId) {
        return String.format(Locale.ROOT, "SL-DEMO-%03d", databaseId);
    }

    public void updateStreetlight(
            int id,
            String poleCode,
            int streetId,
            int configurationId,
            LocalDate installationDate,
            BigDecimal latitude,
            BigDecimal longitude,
            int statusId) throws SQLException {

        String sql = """
                UPDATE streetlights
                SET installation_date = ?,
                    latitude = ?,
                    longitude = ?,
                    streets_id = ?,
                    streetlight_configurations_id = ?,
                    status_id = ?
                WHERE id = ?
                  AND EXISTS (SELECT 1 FROM streets WHERE id = ?)
                  AND EXISTS (SELECT 1 FROM streetlight_configurations WHERE id = ?)
                  AND EXISTS (SELECT 1 FROM status WHERE id = ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            if (installationDate == null) {
                statement.setNull(1, java.sql.Types.DATE);
            } else {
                statement.setDate(1, Date.valueOf(installationDate));
            }
            statement.setBigDecimal(2, latitude);
            statement.setBigDecimal(3, longitude);
            statement.setInt(4, streetId);
            statement.setInt(5, configurationId);
            statement.setInt(6, statusId);
            statement.setInt(7, id);
            statement.setInt(8, streetId);
            statement.setInt(9, configurationId);
            statement.setInt(10, statusId);

            int updated = statement.executeUpdate();

            if (updated != 1) {
                throw new SQLException(
                        "Streetlight was not updated. It may no longer exist "
                                + "or one of the selected records is invalid."
                );
            }
        }
    }

    public void deleteStreetlight(int id) throws SQLException {

        String sql = """
                DELETE FROM streetlights
                WHERE id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            if (statement.executeUpdate() != 1) {
                throw new SQLException(
                        "Streetlight was not found or has already been deleted."
                );
            }
        }
    }


    public int findStatusIdByName(String name) throws SQLException {
        String sql = "SELECT id FROM status WHERE LOWER(name) = LOWER(?) LIMIT 1";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        throw new SQLException("Required status '" + name + "' was not found.");
    }

    private List<Map<String, Object>> loadStatuses()
            throws SQLException {

        List<Map<String, Object>> result = new ArrayList<>();

        String sql = """
                SELECT id, name
                FROM status
                ORDER BY id
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("id", rs.getInt("id"));
                item.put("name", rs.getString("name"));
                result.add(item);
            }
        }

        return result;
    }

    public boolean poleCodeExists(String poleCode)
            throws SQLException {
        return poleCodeExists(poleCode, 0);
    }

    public boolean poleCodeExists(String poleCode, int excludedId)
            throws SQLException {

        String sql = """
                SELECT 1
                FROM streetlights
                WHERE pole_code = ?
                  AND id <> ?
                LIMIT 1
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, poleCode);
            statement.setInt(2, excludedId);

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }
}
