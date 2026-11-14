
package lk.sasindi.smartcity360.patterns.composite;

import java.util.List;

public interface CityComponent {

    int getId();

    String getName();

    String getCode();

    String getDescription();

    String getType();

    List<CityComponent> getChildren();

    void addChild(CityComponent component);

    void removeChild(CityComponent component);

    int getTotalComponents();

    int getTotalBuildings();

    List<Building> getBuildings();

    int getPoweredBuildings();

    PowerStatus getPowerStatus();

    int shutdownPower();

    int restorePower();

    double getEnergyUsage();

    UsageReport generateUsageReport();
}
