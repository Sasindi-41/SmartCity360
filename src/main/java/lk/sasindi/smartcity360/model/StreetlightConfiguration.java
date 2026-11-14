package lk.sasindi.smartcity360.model;

public class StreetlightConfiguration {

    private final int id;
    private final String configName;
    private final int brightnessPercent;
    private final int colorTemperature;
    private final String operatingMode;
    private final boolean motionSensorEnabled;
    private final Double powerRatingWatts;
    private final String configHash;

    public StreetlightConfiguration(
            int id,
            String configName,
            int brightnessPercent,
            int colorTemperature,
            String operatingMode,
            boolean motionSensorEnabled,
            Double powerRatingWatts,
            String configHash) {

        this.id = id;
        this.configName = configName;
        this.brightnessPercent = brightnessPercent;
        this.colorTemperature = colorTemperature;
        this.operatingMode = operatingMode;
        this.motionSensorEnabled = motionSensorEnabled;
        this.powerRatingWatts = powerRatingWatts;
        this.configHash = configHash;
    }

    public int getId() {
        return id;
    }

    public String getConfigName() {
        return configName;
    }

    public int getBrightnessPercent() {
        return brightnessPercent;
    }

    public int getColorTemperature() {
        return colorTemperature;
    }

    public String getOperatingMode() {
        return operatingMode;
    }

    public boolean isMotionSensorEnabled() {
        return motionSensorEnabled;
    }

    public Double getPowerRatingWatts() {
        return powerRatingWatts;
    }

    public String getConfigHash() {
        return configHash;
    }
}