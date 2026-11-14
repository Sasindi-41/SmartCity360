package lk.sasindi.smartcity360.patterns.composite;

import java.util.List;

public abstract class AbstractCityComponent implements CityComponent {

    private final int id;
    private final String name;
    private final String code;
    private final String description;

    protected AbstractCityComponent(
            int id,
            String name,
            String code,
            String description
    ) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<CityComponent> getChildren() {
        return List.of();
    }

    @Override
    public void addChild(CityComponent component) {
        throw new UnsupportedOperationException(
                getType() + " cannot contain child components."
        );
    }

    @Override
    public void removeChild(CityComponent component) {
        throw new UnsupportedOperationException(
                getType() + " cannot contain child components."
        );
    }

    @Override
    public int getTotalComponents() {
        return 1;
    }

    @Override
    public int getTotalBuildings() {
        return 0;
    }

    @Override
    public int getPoweredBuildings() {

        int powered = 0;

        for (Building building : getBuildings()) {
            if (building.isPowerOn()) {
                powered++;
            }
        }

        return powered;
    }

    @Override
    public PowerStatus getPowerStatus() {

        int total = getBuildings().size();

        if (total == 0) {
            return PowerStatus.ACTIVE;
        }

        int powered = getPoweredBuildings();

        if (powered == total) {
            return PowerStatus.ACTIVE;
        }

        if (powered == 0) {
            return PowerStatus.OFF;
        }

        return PowerStatus.PARTIAL;
    }
}
