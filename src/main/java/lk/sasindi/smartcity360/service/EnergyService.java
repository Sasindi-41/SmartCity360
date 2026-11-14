package lk.sasindi.smartcity360.service;

import lk.sasindi.smartcity360.dao.EnergyDAO;
import lk.sasindi.smartcity360.model.EnergyMeter;
import lk.sasindi.smartcity360.model.EnergyProcessResult;
import lk.sasindi.smartcity360.model.EnergyStreamContext;
import lk.sasindi.smartcity360.patterns.decorator.*;

import java.util.List;
import java.util.Locale;
import java.util.Base64;
import java.time.LocalDate;

public class EnergyService {

    private final EnergyDAO energyDAO;

    public EnergyService() {
        this.energyDAO = new EnergyDAO();
    }

    public List<EnergyMeter> getAllMeters() throws Exception {
        return energyDAO.getAllMeters();
    }

    public List<EnergyDAO.BuildingOption> getBuildingOptions() throws Exception {
        return energyDAO.getBuildingOptions();
    }

    public List<String> getMeterTypeOptions() throws Exception {
        return energyDAO.getMeterTypeOptions();
    }

    public List<EnergyDAO.LookupOption> getMeterStatusOptions() throws Exception {
        return energyDAO.getMeterStatusOptions();
    }

    public EnergyDAO.CreatedMeter registerMeter(String meterType, int buildingId,
                                                LocalDate installationDate) throws Exception {
        return energyDAO.createMeter(meterType, buildingId, installationDate);
    }

    public void updateMeter(int meterId, String meterType, int buildingId,
                            LocalDate installationDate, int statusId) throws Exception {
        energyDAO.updateMeter(meterId, meterType, buildingId, installationDate, statusId);
    }

    public EnergyDAO.MeterToken getOrIssueMeterToken(int meterId)
            throws Exception {
        return energyDAO.getOrIssueMeterToken(meterId);
    }

    public int getMeterCount() throws Exception {
        return energyDAO.getCount("energy_meters");
    }

    public int getActiveMeterCount() throws Exception {
        return energyDAO.getActiveMeterCount();
    }

    public int getStreamCount() throws Exception {
        return energyDAO.getCount("meter_stream_records");
    }

    public int getSuccessfulStreamCount() throws Exception {
        return energyDAO.getSuccessfulStreamCount();
    }

    public int getReadingCount() throws Exception {
        return energyDAO.getCount("energy_readings");
    }

    public double getTotalEnergyConsumption() throws Exception {
        return energyDAO.getTotalEnergyConsumption();
    }

    public List<EnergyProcessResult> getRecentHistory(int limit)
            throws Exception {
        return energyDAO.getRecentHistory(limit);
    }

    public EnergyProcessResult processReading(
            int meterId,
            String token,
            double readingValue,
            String unit,
            boolean compressionEnabled,
            boolean encryptionEnabled
    ) throws Exception {

        validateInput(meterId, token, readingValue, unit);

        String normalizedUnit = unit.trim();

        String payload = String.format(
                Locale.ROOT,
                "{\"meterId\":%d,\"readingValue\":%.4f,"
                        + "\"unit\":\"%s\",\"timestamp\":\"%s\"}",
                meterId,
                readingValue,
                normalizedUnit,
                java.time.Instant.now()
        );

        EnergyStreamContext context = new EnergyStreamContext(
                meterId,
                token.trim(),
                payload
        );

        EnergyProcessResult result = new EnergyProcessResult();

        result.setReadingValue(readingValue);
        result.setUnit(normalizedUnit);
        result.setOriginalSize(context.getPayload().length);

        boolean successful = false;

        try {

            EnergyStream stream = new BasicEnergyStream();

            stream = new AuthenticationDecorator(stream, energyDAO);

            if (compressionEnabled) {
                stream = new CompressionDecorator(stream);
            }

            if (encryptionEnabled) {
                stream = new EncryptionDecorator(
                        stream,
                        loadEncryptionKey()
                );
            }

            stream.process(context);

            successful = true;

            result.setMessage(
                    "Energy stream processed successfully."
            );

        } catch (SecurityException ex) {

            result.setMessage(
                    "Authentication failed. The meter token is invalid."
            );

        } catch (Exception ex) {

            result.setMessage(
                    "Stream processing failed: "
                            + (ex.getMessage() == null
                            ? "Unexpected processing error."
                            : ex.getMessage())
            );
        }

        result.setSuccess(successful);

        result.setAuthenticationStatus(
                context.getAuthenticationStatus()
        );

        result.setProcessingStatus(
                successful ? "SUCCESS" : "FAILED"
        );

        result.setEncryptionAlgorithm(
                context.getEncryptionAlgorithm()
        );

        result.setCompressionAlgorithm(
                context.getCompressionAlgorithm()
        );

        result.setFinalSize(context.getPayload().length);

        result.setProcessedPayload(
                Base64.getEncoder().encodeToString(context.getPayload())
        );

        result.addLogs(context.getLogs());

        int streamId = energyDAO.saveProcessingAttempt(
                context,
                result,
                successful
        );

        result.setStreamRecordId(streamId);

        return result;
    }

    private void validateInput(
            int meterId,
            String token,
            double readingValue,
            String unit
    ) {

        if (meterId <= 0) {
            throw new IllegalArgumentException(
                    "Please select a valid meter."
            );
        }

        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException(
                    "Meter authentication token is required."
            );
        }

        if (!Double.isFinite(readingValue)
                || readingValue < 0
                || readingValue > 999999999999.9999) {
            throw new IllegalArgumentException(
                    "Enter a valid non-negative reading."
            );
        }

        if (unit == null
                || !List.of("kWh", "Wh", "MWh").contains(unit.trim())) {
            throw new IllegalArgumentException(
                    "Select a valid energy unit."
            );
        }
    }

    private byte[] loadEncryptionKey() {

        String configuredKey = System.getenv("SMARTCITY_AES_KEY");

        if (configuredKey == null || configuredKey.isBlank()) {
            throw new IllegalStateException(
                    "Encryption key is not configured. Set SMARTCITY_AES_KEY."
            );
        }

        try {

            byte[] key = Base64.getDecoder().decode(configuredKey);

            if (key.length != 32) {
                throw new IllegalStateException(
                        "SMARTCITY_AES_KEY must decode to exactly 32 bytes."
                );
            }

            return key;

        } catch (IllegalArgumentException ex) {

            throw new IllegalStateException(
                    "SMARTCITY_AES_KEY must be a valid Base64 value.",
                    ex
            );
        }
    }
}