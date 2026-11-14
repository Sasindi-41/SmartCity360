package lk.sasindi.smartcity360.model;

public class EnergyMeter {

    private int id;
    private String serialNumber;
    private String meterType;
    private String installationDate;
    private String status;
    private String buildingName;
    private String streetName;
    private String districtName;
    private String cityName;
    private int buildingId;
    private int statusId;

    public EnergyMeter() {
    }

    public EnergyMeter(int id, String serialNumber, String meterType,
                       String installationDate, String status,
                       String buildingName, String streetName,
                       String districtName, String cityName) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.meterType = meterType;
        this.installationDate = installationDate;
        this.status = status;
        this.buildingName = buildingName;
        this.streetName = streetName;
        this.districtName = districtName;
        this.cityName = cityName;
    }

    public EnergyMeter(int id, String serialNumber, String meterType,
                       String installationDate, String status,
                       String buildingName, String streetName,
                       String districtName, String cityName,
                       int buildingId, int statusId) {
        this(id, serialNumber, meterType, installationDate, status,
                buildingName, streetName, districtName, cityName);
        this.buildingId = buildingId;
        this.statusId = statusId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getMeterType() {
        return meterType;
    }

    public void setMeterType(String meterType) {
        this.meterType = meterType;
    }

    public String getInstallationDate() {
        return installationDate;
    }

    public void setInstallationDate(String installationDate) {
        this.installationDate = installationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public int getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(int buildingId) {
        this.buildingId = buildingId;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getLocation() {
        StringBuilder location = new StringBuilder();

        append(location, buildingName);
        append(location, streetName);
        append(location, districtName);
        append(location, cityName);

        return location.length() == 0 ? "Location unavailable" : location.toString();
    }

    private void append(StringBuilder builder, String value) {
        if (value != null && !value.isBlank()) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(value);
        }
    }
}