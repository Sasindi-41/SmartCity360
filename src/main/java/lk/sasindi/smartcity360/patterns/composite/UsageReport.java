package lk.sasindi.smartcity360.patterns.composite;

import java.util.Collections;
import java.util.List;

public class UsageReport {

    private final String type;
    private final int id;
    private final String code;
    private final String name;
    private final double usageKwh;
    private final int totalBuildings;
    private final int poweredBuildings;
    private final PowerStatus powerStatus;
    private final List<UsageReport> children;

    public UsageReport(
            String type,
            int id,
            String code,
            String name,
            double usageKwh,
            int totalBuildings,
            int poweredBuildings,
            PowerStatus powerStatus,
            List<UsageReport> children
    ) {
        this.type = type;
        this.id = id;
        this.code = code;
        this.name = name;
        this.usageKwh = usageKwh;
        this.totalBuildings = totalBuildings;
        this.poweredBuildings = poweredBuildings;
        this.powerStatus = powerStatus;
        this.children = children == null
                ? List.of()
                : Collections.unmodifiableList(children);
    }

    public String getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public double getUsageKwh() {
        return usageKwh;
    }

    public int getTotalBuildings() {
        return totalBuildings;
    }

    public int getPoweredBuildings() {
        return poweredBuildings;
    }

    public PowerStatus getPowerStatus() {
        return powerStatus;
    }

    public List<UsageReport> getChildren() {
        return children;
    }
}
