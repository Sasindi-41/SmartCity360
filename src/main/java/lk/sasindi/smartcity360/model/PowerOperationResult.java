package lk.sasindi.smartcity360.model;

import lk.sasindi.smartcity360.patterns.composite.PowerStatus;

public class PowerOperationResult {

    private final String componentType;
    private final int componentId;
    private final String componentName;
    private final String operation;
    private final int affectedBuildings;
    private final int totalBuildings;
    private final PowerStatus resultingStatus;

    public PowerOperationResult(
            String componentType,
            int componentId,
            String componentName,
            String operation,
            int affectedBuildings,
            int totalBuildings,
            PowerStatus resultingStatus
    ) {
        this.componentType = componentType;
        this.componentId = componentId;
        this.componentName = componentName;
        this.operation = operation;
        this.affectedBuildings = affectedBuildings;
        this.totalBuildings = totalBuildings;
        this.resultingStatus = resultingStatus;
    }

    public String getComponentType() {
        return componentType;
    }

    public int getComponentId() {
        return componentId;
    }

    public String getComponentName() {
        return componentName;
    }

    public String getOperation() {
        return operation;
    }

    public int getAffectedBuildings() {
        return affectedBuildings;
    }

    public int getTotalBuildings() {
        return totalBuildings;
    }

    public PowerStatus getResultingStatus() {
        return resultingStatus;
    }
}
