package lk.sasindi.smartcity360.service;

import lk.sasindi.smartcity360.dao.StreetlightDAO;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;

public class StreetlightService {

    private final StreetlightDAO streetlightDAO =
            new StreetlightDAO();

    public Map<String, Object> getDashboardData()
            throws SQLException {

        return streetlightDAO.getDashboardData();
    }

    public void createStreetlight(
            String poleCode,
            int streetId,
            int configurationId) throws SQLException {

        createStreetlight(
                poleCode, streetId, configurationId,
                null, null, null,
                streetlightDAO.findStatusIdByName("Active")
        );
    }

    public int createStreetlight(
            String poleCode,
            int streetId,
            int configurationId,
            LocalDate installationDate,
            BigDecimal latitude,
            BigDecimal longitude,
            int statusId) throws SQLException {

        validateReferences(streetId, configurationId, statusId);
        validateCoordinates(latitude, longitude);

        return streetlightDAO.createStreetlight(
                null,
                streetId,
                configurationId,
                installationDate,
                normalizeCoordinate(latitude),
                normalizeCoordinate(longitude),
                statusId
        );
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

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "A valid streetlight record must be selected."
            );
        }

        validateReferences(streetId, configurationId, statusId);
        validateCoordinates(latitude, longitude);

        streetlightDAO.updateStreetlight(
                id,
                null,
                streetId,
                configurationId,
                installationDate,
                normalizeCoordinate(latitude),
                normalizeCoordinate(longitude),
                statusId
        );
    }

    public void deleteStreetlight(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    "A valid streetlight record must be selected."
            );
        }

        streetlightDAO.deleteStreetlight(id);
    }

    private void validateReferences(
            int streetId,
            int configurationId,
            int statusId) {

        if (streetId <= 0) {
            throw new IllegalArgumentException(
                    "Please select a valid street."
            );
        }

        if (configurationId <= 0) {
            throw new IllegalArgumentException(
                    "Please select a valid lighting configuration."
            );
        }

        if (statusId <= 0) {
            throw new IllegalArgumentException(
                    "Please select a valid streetlight status."
            );
        }
    }

    private void validateCoordinates(
            BigDecimal latitude,
            BigDecimal longitude) {

        if ((latitude == null) != (longitude == null)) {
            throw new IllegalArgumentException(
                    "Enter both latitude and longitude, or leave both empty."
            );
        }

        if (latitude != null &&
                (latitude.compareTo(new BigDecimal("-90")) < 0 ||
                        latitude.compareTo(new BigDecimal("90")) > 0)) {
            throw new IllegalArgumentException(
                    "Latitude must be between -90 and 90."
            );
        }

        if (longitude != null &&
                (longitude.compareTo(new BigDecimal("-180")) < 0 ||
                        longitude.compareTo(new BigDecimal("180")) > 0)) {
            throw new IllegalArgumentException(
                    "Longitude must be between -180 and 180."
            );
        }
    }

    private BigDecimal normalizeCoordinate(BigDecimal value) {
        return value == null ? null : value.setScale(7, java.math.RoundingMode.HALF_UP);
    }

    public static class DuplicateStreetlightException
            extends RuntimeException {

        public DuplicateStreetlightException(String message) {
            super(message);
        }
    }
}
