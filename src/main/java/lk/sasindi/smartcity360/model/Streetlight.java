package lk.sasindi.smartcity360.model;

import java.time.LocalDate;

public class Streetlight {

    private final int id;
    private final String poleCode;
    private final LocalDate installationDate;
    private final String streetName;
    private final String districtName;
    private final String statusName;
    private final int streetId;
    private final StreetlightConfiguration configuration;

    private final Object latitude;
    private final Object longitude;
    private final int districtId;
    private final int statusId;

    public Streetlight(
            int id,
            String poleCode,
            LocalDate installationDate,
            String streetName,
            String districtName,
            String statusName,
            int streetId,
            StreetlightConfiguration configuration) {

        this(id, poleCode, installationDate, streetName, districtName,
                statusName, streetId, configuration, null, null, 0);
    }

    public Streetlight(
            int id,
            String poleCode,
            LocalDate installationDate,
            String streetName,
            String districtName,
            String statusName,
            int streetId,
            StreetlightConfiguration configuration,
            Object latitude,
            Object longitude,
            int districtId) {

        this(id, poleCode, installationDate, streetName, districtName,
                statusName, streetId, configuration, latitude, longitude,
                districtId, 0);
    }

    public Streetlight(
            int id,
            String poleCode,
            LocalDate installationDate,
            String streetName,
            String districtName,
            String statusName,
            int streetId,
            StreetlightConfiguration configuration,
            Object latitude,
            Object longitude,
            int districtId,
            int statusId) {

        this.id = id;
        this.poleCode = poleCode;
        this.installationDate = installationDate;
        this.streetName = streetName;
        this.districtName = districtName;
        this.statusName = statusName;
        this.streetId = streetId;
        this.configuration = configuration;
        this.latitude = latitude;
        this.longitude = longitude;
        this.districtId = districtId;
        this.statusId = statusId;
    }

    public int getId() {
        return id;
    }

    public String getPoleCode() {
        return poleCode;
    }

    public LocalDate getInstallationDate() {
        return installationDate;
    }

    public String getStreetName() {
        return streetName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public String getStatusName() {
        return statusName;
    }

    public int getStreetId() {
        return streetId;
    }

    public StreetlightConfiguration getConfiguration() {
        return configuration;
    }

    public Object getLatitude() {
        return latitude;
    }

    public Object getLongitude() {
        return longitude;
    }

    public int getDistrictId() {
        return districtId;
    }

    public int getStatusId() {
        return statusId;
    }
}
