package lk.sasindi.smartcity360.model;

public class PowerOperationRecord {

    private final int id;
    private final String componentType;
    private final int componentId;
    private final String componentName;
    private final String operation;
    private final int affectedBuildings;
    private final String performedBy;
    private final String performedAt;

    public PowerOperationRecord(
            int id,
            String componentType,
            int componentId,
            String componentName,
            String operation,
            int affectedBuildings,
            String performedBy,
            String performedAt
    ) {
        this.id = id;
        this.componentType = componentType;
        this.componentId = componentId;
        this.componentName = componentName;
        this.operation = operation;
        this.affectedBuildings = affectedBuildings;
        this.performedBy = performedBy;
        this.performedAt = performedAt;
    }

    public int getId() {
        return id;
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

    public String getPerformedBy() {
        return performedBy;
    }

    public String getPerformedAt() {
        return performedAt;
    }
}
