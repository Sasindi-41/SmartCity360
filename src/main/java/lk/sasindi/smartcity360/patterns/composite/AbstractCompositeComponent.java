package lk.sasindi.smartcity360.patterns.composite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class AbstractCompositeComponent
        extends AbstractCityComponent {

    private final List<CityComponent> children = new ArrayList<>();

    private final String allowedChildType;

    protected AbstractCompositeComponent(
            int id,
            String name,
            String code,
            String description,
            String allowedChildType
    ) {
        super(id, name, code, description);
        this.allowedChildType = allowedChildType;
    }

    @Override
    public List<CityComponent> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public void addChild(CityComponent component) {

        Objects.requireNonNull(component, "Component cannot be null.");

        if (!allowedChildType.equals(component.getType())) {
            throw new IllegalArgumentException(
                    "A " + getType() + " can only contain "
                            + allowedChildType + " components."
            );
        }

        if (children.stream().anyMatch(
                child -> child.getId() == component.getId()
                        && child.getType().equals(component.getType())
        )) {
            throw new IllegalArgumentException(
                    "This component has already been added."
            );
        }

        children.add(component);
    }

    @Override
    public void removeChild(CityComponent component) {

        Objects.requireNonNull(component, "Component cannot be null.");

        boolean removed = children.removeIf(
                child -> child.getId() == component.getId()
                        && child.getType().equals(component.getType())
        );

        if (!removed) {
            throw new IllegalArgumentException(
                    "The specified component does not exist."
            );
        }
    }

    @Override
    public int getTotalComponents() {

        int total = 1;

        for (CityComponent child : children) {
            total += child.getTotalComponents();
        }

        return total;
    }

    @Override
    public int getTotalBuildings() {

        int total = 0;

        for (CityComponent child : children) {
            total += child.getTotalBuildings();
        }

        return total;
    }

    @Override
    public List<Building> getBuildings() {

        List<Building> buildings = new ArrayList<>();

        for (CityComponent child : children) {
            buildings.addAll(child.getBuildings());
        }

        return buildings;
    }

    @Override
    public int shutdownPower() {

        int changed = 0;

        for (CityComponent child : children) {
            changed += child.shutdownPower();
        }

        return changed;
    }

    @Override
    public int restorePower() {

        int changed = 0;

        for (CityComponent child : children) {
            changed += child.restorePower();
        }

        return changed;
    }

    @Override
    public double getEnergyUsage() {

        double total = 0;

        for (CityComponent child : children) {
            total += child.getEnergyUsage();
        }

        return total;
    }

    @Override
    public UsageReport generateUsageReport() {

        List<UsageReport> childReports = new ArrayList<>();

        for (CityComponent child : children) {
            childReports.add(child.generateUsageReport());
        }

        return new UsageReport(
                getType(),
                getId(),
                getCode(),
                getName(),
                getEnergyUsage(),
                getBuildings().size(),
                getPoweredBuildings(),
                getPowerStatus(),
                childReports
        );
    }
}
