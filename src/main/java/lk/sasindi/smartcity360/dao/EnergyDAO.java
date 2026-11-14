package lk.sasindi.smartcity360.dao;

import lk.sasindi.smartcity360.model.EnergyMeter;
import lk.sasindi.smartcity360.model.EnergyProcessResult;
import lk.sasindi.smartcity360.model.EnergyStreamContext;
import lk.sasindi.smartcity360.model.StreamProcessingLog;
import lk.sasindi.smartcity360.util.DBConnection;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.time.LocalDate;

public class EnergyDAO {

    public List<EnergyMeter> getAllMeters() throws SQLException {

        List<EnergyMeter> meters = new ArrayList<>();

        String sql = """
            SELECT
                em.id,
                em.serial_number,
                em.meter_type,
                em.installation_date,
                s.name AS meter_status,
                s.id AS status_id,
                b.id AS building_id,
                b.building_name,
                st.street_name,
                d.district_name,
                c.city_name
            FROM energy_meters em
            INNER JOIN status s ON em.status_id = s.id
            INNER JOIN buildings b ON em.buildings_id = b.id
            INNER JOIN streets st ON b.streets_id = st.id
            INNER JOIN districts d ON st.districts_id = d.id
            INNER JOIN cities c ON d.cities_id = c.id
            ORDER BY em.id DESC
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                meters.add(new EnergyMeter(
                        rs.getInt("id"),
                        rs.getString("serial_number"),
                        rs.getString("meter_type"),
                        rs.getString("installation_date"),
                        rs.getString("meter_status"),
                        rs.getString("building_name"),
                        rs.getString("street_name"),
                        rs.getString("district_name"),
                        rs.getString("city_name"),
                        rs.getInt("building_id"),
                        rs.getInt("status_id")
                ));
            }
        }

        return meters;
    }



    public record BuildingOption(int id, String label) { }
    public record LookupOption(int id, String name) { }
    public record CreatedMeter(int meterId, String serialNumber) { }

    public List<BuildingOption> getBuildingOptions() throws SQLException {
        String sql = """
                SELECT b.id,
                       CONCAT_WS(', ', b.building_name, st.street_name,
                                 d.district_name, c.city_name) AS building_label
                FROM buildings b
                INNER JOIN streets st ON b.streets_id = st.id
                INNER JOIN districts d ON st.districts_id = d.id
                INNER JOIN cities c ON d.cities_id = c.id
                ORDER BY c.city_name, d.district_name, st.street_name, b.building_name
                """;
        List<BuildingOption> options = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                options.add(new BuildingOption(rs.getInt("id"),
                        rs.getString("building_label")));
            }
        }
        return options;
    }

    public List<String> getMeterTypeOptions() throws SQLException {
        Map<String, String> types = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT DISTINCT meter_type FROM energy_meters " +
                             "WHERE meter_type IS NOT NULL AND TRIM(meter_type) <> '' ORDER BY meter_type");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String value = rs.getString(1);
                if (value != null && !value.isBlank()) types.put(value.trim(), value.trim());
            }
        }
        for (String value : List.of(
                "Smart Electricity Meter", "Single Phase Smart Meter",
                "Three Phase Smart Meter", "Prepaid Electricity Meter",
                "Industrial Energy Meter", "Bidirectional Energy Meter")) {
            types.putIfAbsent(value, value);
        }
        return new ArrayList<>(types.values());
    }

    public List<LookupOption> getMeterStatusOptions() throws SQLException {
        List<LookupOption> options = new ArrayList<>();
        String sql = "SELECT id, name FROM status ORDER BY name";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) options.add(new LookupOption(rs.getInt("id"), rs.getString("name")));
        }
        return options;
    }

    public CreatedMeter createMeter(String meterType, int buildingId, LocalDate installationDate)
            throws SQLException {
        if (!isAllowedMeterType(meterType)) {
            throw new IllegalArgumentException("Select a valid meter type from the list.");
        }
        if (buildingId <= 0) throw new IllegalArgumentException("Select an assigned building.");

        final String lockName = "smartcity360_energy_meter_serial";
        try (Connection con = DBConnection.getConnection()) {
            boolean lockAcquired = false;
            boolean originalAutoCommit = con.getAutoCommit();
            try {
                try (PreparedStatement lock = con.prepareStatement("SELECT GET_LOCK(?, 10)")) {
                    lock.setString(1, lockName);
                    try (ResultSet rs = lock.executeQuery()) {
                        lockAcquired = rs.next() && rs.getInt(1) == 1;
                    }
                }
                if (!lockAcquired) throw new SQLException("Meter serial generation is busy. Please try again.");

                con.setAutoCommit(false);
                try (PreparedStatement check = con.prepareStatement("SELECT 1 FROM buildings WHERE id = ?")) {
                    check.setInt(1, buildingId);
                    try (ResultSet rs = check.executeQuery()) {
                        if (!rs.next()) throw new IllegalArgumentException("The selected building does not exist.");
                    }
                }

                int activeStatusId = findStatusId(con, "ACTIVE");
                int nextNumber = 1;
                String serialSql = """
                        SELECT GREATEST(
                            COALESCE(MAX(CASE
                                WHEN serial_number LIKE 'EM-SC360-%'
                                 AND SUBSTRING(serial_number, 10) REGEXP '^[0-9]+$'
                                THEN CAST(SUBSTRING(serial_number, 10) AS UNSIGNED)
                            END), 0),
                            COUNT(*)
                        ) + 1
                        FROM energy_meters
                        """;
                try (PreparedStatement ps = con.prepareStatement(serialSql);
                     ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) nextNumber = rs.getInt(1);
                }
                String serialNumber = String.format(Locale.ROOT, "EM-SC360-%03d", nextNumber);

                String token = generateMeterToken();
                String tokenHash = sha256(token);
                String encryptedToken;
                try {
                    encryptedToken = encryptMeterToken(token);
                } catch (Exception ex) {
                    throw new SQLException("Unable to protect the new meter token. Check SMARTCITY_AES_KEY.", ex);
                }

                String insertSql = """
                        INSERT INTO energy_meters
                            (serial_number, meter_type, authentication_token_hash,
                             authentication_token_encrypted, installation_date,
                             status_id, buildings_id)
                        VALUES (?, ?, ?, ?, ?, ?, ?)
                        """;
                int meterId;
                try (PreparedStatement ps = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, serialNumber);
                    ps.setString(2, meterType.trim());
                    ps.setString(3, tokenHash);
                    ps.setString(4, encryptedToken);
                    if (installationDate == null) ps.setNull(5, Types.DATE);
                    else ps.setDate(5, Date.valueOf(installationDate));
                    ps.setInt(6, activeStatusId);
                    ps.setInt(7, buildingId);
                    if (ps.executeUpdate() != 1) throw new SQLException("The energy meter was not created.");
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("Could not retrieve the new meter ID.");
                        meterId = keys.getInt(1);
                    }
                }
                con.commit();
                return new CreatedMeter(meterId, serialNumber);
            } catch (Exception ex) {
                try { con.rollback(); } catch (SQLException rollbackError) { ex.addSuppressed(rollbackError); }
                if (ex instanceof SQLException sql) throw sql;
                if (ex instanceof IllegalArgumentException invalid) throw invalid;
                throw new SQLException("Unable to register the energy meter.", ex);
            } finally {
                try { con.setAutoCommit(originalAutoCommit); } catch (SQLException ignored) { }
                if (lockAcquired) {
                    try (PreparedStatement release = con.prepareStatement("SELECT RELEASE_LOCK(?)")) {
                        release.setString(1, lockName);
                        release.executeQuery().close();
                    } catch (SQLException ignored) { }
                }
            }
        }
    }

    public void updateMeter(int meterId, String meterType, int buildingId,
                            LocalDate installationDate, int statusId) throws SQLException {
        if (meterId <= 0) throw new IllegalArgumentException("Select a valid energy meter.");
        if (!isAllowedMeterType(meterType))
            throw new IllegalArgumentException("Select a valid meter type from the list.");
        if (buildingId <= 0 || statusId <= 0)
            throw new IllegalArgumentException("Select a valid building and meter status.");

        String sql = """
                UPDATE energy_meters
                SET meter_type = ?, buildings_id = ?, installation_date = ?, status_id = ?
                WHERE id = ?
                """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, meterType.trim());
            ps.setInt(2, buildingId);
            if (installationDate == null) ps.setNull(3, Types.DATE);
            else ps.setDate(3, Date.valueOf(installationDate));
            ps.setInt(4, statusId);
            ps.setInt(5, meterId);
            if (ps.executeUpdate() != 1)
                throw new IllegalArgumentException("The selected energy meter was not found.");
        }
    }

    private boolean isAllowedMeterType(String meterType) throws SQLException {
        if (meterType == null || meterType.isBlank() || meterType.trim().length() > 80) return false;
        return getMeterTypeOptions().stream().anyMatch(
                option -> option.equalsIgnoreCase(meterType.trim()));
    }

    private int findStatusId(Connection con, String statusName) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT id FROM status WHERE UPPER(name) = UPPER(?) LIMIT 1")) {
            ps.setString(1, statusName);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("The required '" + statusName + "' status is missing from the status table.");
                return rs.getInt(1);
            }
        }
    }

    public record MeterToken(String token, boolean newlyIssued) { }

    public MeterToken getOrIssueMeterToken(int meterId) throws SQLException {
        if (meterId <= 0) {
            throw new IllegalArgumentException("Please select a valid energy meter.");
        }

        String selectSql = """
                SELECT em.authentication_token_encrypted,
                       em.authentication_token_hash,
                       s.name AS meter_status
                FROM energy_meters em
                INNER JOIN status s ON em.status_id = s.id
                WHERE em.id = ?
                FOR UPDATE
                """;

        try (Connection con = DBConnection.getConnection()) {
            boolean originalAutoCommit = con.getAutoCommit();
            con.setAutoCommit(false);

            try {
                String encryptedToken;
                String storedHash;
                String meterStatus;
                try (PreparedStatement ps = con.prepareStatement(selectSql)) {
                    ps.setInt(1, meterId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new IllegalArgumentException(
                                    "The selected energy meter was not found.");
                        }
                        encryptedToken = rs.getString("authentication_token_encrypted");
                        storedHash = rs.getString("authentication_token_hash");
                        meterStatus = rs.getString("meter_status");
                    }
                }

                if (!"ACTIVE".equalsIgnoreCase(meterStatus)) {
                    throw new IllegalArgumentException("This meter is not active. Activate it before processing readings.");
                }

                if (encryptedToken != null && !encryptedToken.isBlank()) {
                    String token = decryptMeterToken(encryptedToken);
                    String calculatedHash = sha256(token);
                    if (storedHash == null || storedHash.isBlank()) {
                        try (PreparedStatement repair = con.prepareStatement(
                                "UPDATE energy_meters SET authentication_token_hash = ? WHERE id = ?")) {
                            repair.setString(1, calculatedHash);
                            repair.setInt(2, meterId);
                            repair.executeUpdate();
                        }
                    } else if (!MessageDigest.isEqual(
                            storedHash.trim().toLowerCase(Locale.ROOT).getBytes(StandardCharsets.US_ASCII),
                            calculatedHash.getBytes(StandardCharsets.US_ASCII))) {
                        throw new SQLException("The stored meter token hash does not match its encrypted value.");
                    }
                    con.commit();
                    return new MeterToken(token, false);
                }

                String token = generateMeterToken();
                String encrypted = encryptMeterToken(token);
                String hash = sha256(token);

                try (PreparedStatement ps = con.prepareStatement("""
                        UPDATE energy_meters
                        SET authentication_token_hash = ?,
                            authentication_token_encrypted = ?
                        WHERE id = ?
                        """)) {
                    ps.setString(1, hash);
                    ps.setString(2, encrypted);
                    ps.setInt(3, meterId);
                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("Unable to issue an authentication token for this meter.");
                    }
                }

                con.commit();
                return new MeterToken(token, true);

            } catch (Exception ex) {
                try {
                    con.rollback();
                } catch (SQLException rollbackError) {
                    ex.addSuppressed(rollbackError);
                }

                if (ex instanceof SQLException sqlException) {
                    throw sqlException;
                }
                if (ex instanceof IllegalArgumentException illegalArgumentException) {
                    throw illegalArgumentException;
                }
                throw new SQLException("Unable to retrieve or issue the meter authentication token.", ex);

            } finally {
                try {
                    con.setAutoCommit(originalAutoCommit);
                } catch (SQLException ignored) {

                }
            }
        }
    }

    private String generateMeterToken() {
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        return "SC360-" + Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private String encryptMeterToken(String token) throws Exception {
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(deriveTokenKey(), "AES"),
                new GCMParameterSpec(128, iv));
        byte[] encrypted = cipher.doFinal(token.getBytes(StandardCharsets.UTF_8));

        byte[] packed = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, packed, 0, iv.length);
        System.arraycopy(encrypted, 0, packed, iv.length, encrypted.length);
        return Base64.getEncoder().encodeToString(packed);
    }

    private String decryptMeterToken(String encoded) throws Exception {
        byte[] packed = Base64.getDecoder().decode(encoded);
        if (packed.length < 12 + 16) {
            throw new SQLException("The stored encrypted meter token is invalid.");
        }

        byte[] iv = java.util.Arrays.copyOfRange(packed, 0, 12);
        byte[] ciphertext = java.util.Arrays.copyOfRange(packed, 12, packed.length);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(deriveTokenKey(), "AES"),
                new GCMParameterSpec(128, iv));
        return new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8);
    }

    private byte[] deriveTokenKey() throws Exception {
        String configuredKey = System.getenv("SMARTCITY_AES_KEY");
        if (configuredKey == null || configuredKey.isBlank()) {
            throw new IllegalStateException(
                    "Token encryption requires the existing SMARTCITY_AES_KEY environment variable.");
        }

        byte[] masterKey;
        try {
            masterKey = Base64.getDecoder().decode(configuredKey.trim());
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("SMARTCITY_AES_KEY must be valid Base64.", ex);
        }
        if (masterKey.length != 32) {
            throw new IllegalStateException("SMARTCITY_AES_KEY must decode to exactly 32 bytes.");
        }

        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(masterKey, "HmacSHA256"));
        return mac.doFinal("SmartCity360:meter-authentication-token:v1".getBytes(StandardCharsets.UTF_8));
    }

    public boolean verifyMeterToken(int meterId, String token)
            throws SQLException {

        if (meterId <= 0 || token == null || token.isBlank()) {
            return false;
        }

        String sql = """
            SELECT em.authentication_token_hash, s.name AS meter_status
            FROM energy_meters em
            INNER JOIN status s ON em.status_id = s.id
            WHERE em.id = ?
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, meterId);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next() || !"ACTIVE".equalsIgnoreCase(rs.getString("meter_status"))) {
                    return false;
                }

                String storedHash = rs.getString(
                        "authentication_token_hash"
                );

                if (storedHash == null || storedHash.isBlank()) {
                    return false;
                }

                storedHash = storedHash.trim().toLowerCase(Locale.ROOT);

                if (!storedHash.matches("[0-9a-f]{64}")) {
                    return false;
                }

                String submittedHash = sha256(token.trim());

                return MessageDigest.isEqual(
                        storedHash.getBytes(StandardCharsets.US_ASCII),
                        submittedHash.getBytes(StandardCharsets.US_ASCII)
                );
            }
        }
    }

    private String sha256(String value) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    value.getBytes(StandardCharsets.UTF_8)
            );

            StringBuilder output = new StringBuilder(64);

            for (byte b : hash) {
                output.append(String.format("%02x", b & 0xff));
            }

            return output.toString();

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Unable to calculate SHA-256 hash.", ex
            );
        }
    }

    private int getLookupId(
            Connection con,
            String table,
            String statusName
    ) throws SQLException {

        if (!"authentication_status".equals(table)
                && !"processing_status".equals(table)) {
            throw new IllegalArgumentException("Invalid lookup table.");
        }

        String sql = "SELECT id FROM " + table + " WHERE name = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, statusName);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    throw new SQLException(
                            "Required status '" + statusName
                                    + "' was not found in " + table + "."
                    );
                }

                return rs.getInt("id");
            }
        }
    }

    public int getCount(String tableName) throws SQLException {

        List<String> allowed = List.of(
                "energy_meters",
                "meter_stream_records",
                "energy_readings"
        );

        if (!allowed.contains(tableName)) {
            throw new IllegalArgumentException("Invalid table name.");
        }

        String sql = "SELECT COUNT(*) FROM " + tableName;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public int getActiveMeterCount() throws SQLException {

        String sql = """
            SELECT COUNT(*)
            FROM energy_meters em
            INNER JOIN status s ON em.status_id = s.id
            WHERE UPPER(s.name) = 'ACTIVE'
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public int getSuccessfulStreamCount() throws SQLException {

        String sql = """
            SELECT COUNT(*)
            FROM meter_stream_records msr
            INNER JOIN processing_status ps
                ON msr.processing_status_id = ps.id
            WHERE UPPER(ps.name) = 'SUCCESS'
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public double getTotalEnergyConsumption() throws SQLException {

        String sql = """
            SELECT COALESCE(SUM(
                CASE UPPER(er.unit)
                    WHEN 'WH' THEN er.reading_value / 1000
                    WHEN 'MWH' THEN er.reading_value * 1000
                    ELSE er.reading_value
                END
            ), 0)
            FROM energy_readings er
            INNER JOIN meter_stream_records msr
                ON er.meter_stream_records_id = msr.id
            INNER JOIN processing_status ps
                ON msr.processing_status_id = ps.id
            WHERE UPPER(ps.name) = 'SUCCESS'
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getDouble(1) : 0;
        }
    }

    public List<EnergyProcessResult> getRecentHistory(int limit)
            throws SQLException {

        if (limit < 1 || limit > 100) {
            limit = 20;
        }

        List<EnergyProcessResult> history = new ArrayList<>();

        String sql = """
            SELECT
                msr.id,
                em.serial_number,
                er.reading_value,
                er.unit,
                msr.encryption_algorithm,
                msr.compression_algorithm,
                auth.name AS authentication_status,
                ps.name AS processing_status,
                msr.received_at
            FROM meter_stream_records msr
            INNER JOIN energy_meters em
                ON msr.energy_meters_id = em.id
            INNER JOIN authentication_status auth
                ON msr.authentication_status_id = auth.id
            INNER JOIN processing_status ps
                ON msr.processing_status_id = ps.id
            LEFT JOIN energy_readings er
                ON er.meter_stream_records_id = msr.id
            ORDER BY msr.received_at DESC, msr.id DESC
            LIMIT ?
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    EnergyProcessResult item = new EnergyProcessResult();

                    item.setStreamRecordId(rs.getInt("id"));
                    item.setSerialNumber(rs.getString("serial_number"));

                    Timestamp timestamp = rs.getTimestamp("received_at");

                    item.setReceivedAt(
                            timestamp == null ? null : timestamp.toString()
                    );

                    double reading = rs.getDouble("reading_value");

                    item.setReadingValue(
                            rs.wasNull() ? 0 : reading
                    );

                    item.setUnit(rs.getString("unit"));
                    item.setEncryptionAlgorithm(
                            rs.getString("encryption_algorithm")
                    );
                    item.setCompressionAlgorithm(
                            rs.getString("compression_algorithm")
                    );
                    item.setAuthenticationStatus(
                            rs.getString("authentication_status")
                    );
                    item.setProcessingStatus(
                            rs.getString("processing_status")
                    );

                    history.add(item);
                }
            }
        }

        return history;
    }

    public int saveProcessingAttempt(
            EnergyStreamContext context,
            EnergyProcessResult result,
            boolean saveReading
    ) throws SQLException {

        String authenticationStatus =
                context.getAuthenticationStatus();

        String processingStatus =
                result.getProcessingStatus();

        if (authenticationStatus == null
                || processingStatus == null) {
            throw new SQLException("Processing status is missing.");
        }

        if (!List.of(
                "AUTHENTICATED",
                "FAILED",
                "NOT_PERFORMED"
        ).contains(authenticationStatus)) {
            throw new SQLException(
                    "Invalid authentication status: " + authenticationStatus
            );
        }

        if (!List.of("SUCCESS", "FAILED").contains(processingStatus)) {
            throw new SQLException(
                    "Invalid processing status: " + processingStatus
            );
        }

        boolean shouldSaveReading =
                saveReading
                        && result.isSuccess()
                        && "AUTHENTICATED".equals(authenticationStatus)
                        && "SUCCESS".equals(processingStatus);

        String encryption = result.getEncryptionAlgorithm() == null
                ? "NONE" : result.getEncryptionAlgorithm();

        String compression = result.getCompressionAlgorithm() == null
                ? "NONE" : result.getCompressionAlgorithm();

        String storedPayload = Base64.getEncoder().encodeToString(
                context.getPayload()
        );

        String insertStream = """
            INSERT INTO meter_stream_records
            (
                encrypted_payload,
                encryption_algorithm,
                compression_algorithm,
                energy_meters_id,
                authentication_status_id,
                processing_status_id
            )
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        String insertReading = """
            INSERT INTO energy_readings
            (
                reading_value,
                unit,
                meter_stream_records_id
            )
            VALUES (?, ?, ?)
            """;

        String insertLog = """
            INSERT INTO stream_processing_logs
            (
                decorator_name,
                operation_name,
                success,
                details,
                meter_stream_records_id
            )
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {

                int authenticationStatusId = getLookupId(
                        con,
                        "authentication_status",
                        authenticationStatus
                );

                int processingStatusId = getLookupId(
                        con,
                        "processing_status",
                        processingStatus
                );

                int streamId;

                try (PreparedStatement ps = con.prepareStatement(
                        insertStream,
                        Statement.RETURN_GENERATED_KEYS
                )) {

                    ps.setString(1, storedPayload);
                    ps.setString(2, encryption);
                    ps.setString(3, compression);
                    ps.setInt(4, context.getMeterId());
                    ps.setInt(5, authenticationStatusId);
                    ps.setInt(6, processingStatusId);

                    int affected = ps.executeUpdate();

                    if (affected != 1) {
                        throw new SQLException(
                                "Stream record was not inserted."
                        );
                    }

                    try (ResultSet keys = ps.getGeneratedKeys()) {

                        if (!keys.next()) {
                            throw new SQLException(
                                    "Could not retrieve stream record ID."
                            );
                        }

                        streamId = keys.getInt(1);
                    }
                }

                if (shouldSaveReading) {

                    try (PreparedStatement ps =
                                 con.prepareStatement(insertReading)) {

                        ps.setBigDecimal(
                                1,
                                java.math.BigDecimal.valueOf(
                                        result.getReadingValue()
                                )
                        );

                        ps.setString(2, result.getUnit());
                        ps.setInt(3, streamId);

                        ps.executeUpdate();
                    }
                }


                try (PreparedStatement ps = con.prepareStatement(insertLog)) {

                    for (StreamProcessingLog entry : context.getLogs()) {

                        ps.setString(1, entry.getDecoratorName());
                        ps.setString(2, entry.getOperationName());
                        ps.setBoolean(3, entry.isSuccess());
                        ps.setString(4, entry.getDetails());
                        ps.setInt(5, streamId);

                        ps.addBatch();
                    }

                    if (!context.getLogs().isEmpty()) {
                        ps.executeBatch();
                    }
                }

                con.commit();

                return streamId;

            } catch (Exception ex) {

                try {
                    con.rollback();
                } catch (SQLException rollbackEx) {
                    ex.addSuppressed(rollbackEx);
                }

                if (ex instanceof SQLException sqlEx) {
                    throw sqlEx;
                }

                throw new SQLException(
                        "Unable to save energy processing attempt.",
                        ex
                );

            } finally {
                try {
                    con.setAutoCommit(true);
                } catch (SQLException ignored) {

                }
            }
        }
    }
}