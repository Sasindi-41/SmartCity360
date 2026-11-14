package lk.sasindi.smartcity360.patterns.composite;

import java.util.List;

public class Building extends AbstractCityComponent {

    private final String buildingType;
    private final String addressLine1;
    private final String addressLine2;

    private boolean powerOn;
    private final double energyUsageKwh;

    public Building(
            int id,
            String name,
            String code,
            String description,
            String buildingType,
            String addressLine1,
            String addressLine2
    ) {
        this(
                id,
                name,
                code,
                description,
                buildingType,
                addressLine1,
                addressLine2,
                true,
                0
        );
    }

    public Building(
            int id,
            String name,
            String code,
            String description,
            String buildingType,
            String addressLine1,
            String addressLine2,
            boolean powerOn,
            double energyUsageKwh
    ) {
        super(id, name, code, description);

        this.buildingType = buildingType;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.powerOn = powerOn;
        this.energyUsageKwh = energyUsageKwh;
    }

    @Override
    public String getType() {
        return "Building";
    }

    public String getBuildingType() {
        return buildingType;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    @Override
    public int getTotalBuildings() {
        return 1;
    }

    public boolean isPowerOn() {
        return powerOn;
    }

    @Override
    public List<Building> getBuildings() {
        return List.of(this);
    }

    @Override
    public int shutdownPower() {

        if (!powerOn) {
            return 0;
        }

        powerOn = false;
        return 1;
    }

    @Override
    public int restorePower() {

        if (powerOn) {
            return 0;
        }

        powerOn = true;
        return 1;
    }

    @Override
    public double getEnergyUsage() {
        return energyUsageKwh;
    }

    @Override
    public UsageReport generateUsageReport() {

        return new UsageReport(
                getType(),
                getId(),
                getCode(),
                getName(),
                energyUsageKwh,
                1,
                powerOn ? 1 : 0,
                getPowerStatus(),
                List.of()
        );
    }
}
