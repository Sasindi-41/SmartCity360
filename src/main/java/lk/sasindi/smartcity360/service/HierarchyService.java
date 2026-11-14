package lk.sasindi.smartcity360.service;

import lk.sasindi.smartcity360.dao.HierarchyDAO;
import lk.sasindi.smartcity360.model.PowerOperationRecord;
import lk.sasindi.smartcity360.model.PowerOperationResult;
import lk.sasindi.smartcity360.patterns.composite.Building;
import lk.sasindi.smartcity360.patterns.composite.City;
import lk.sasindi.smartcity360.patterns.composite.CityComponent;
import lk.sasindi.smartcity360.patterns.composite.UsageReport;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HierarchyService {

    private static final Set<String> COMPONENT_TYPES =
            Set.of("City", "District", "Street", "Building");

    private static volatile boolean powerSchemaReady = false;

    private final HierarchyDAO hierarchyDAO;

    public HierarchyService() {
        this.hierarchyDAO = new HierarchyDAO();
    }

    private void ensurePowerSchema() throws SQLException {

        if (powerSchemaReady) {
            return;
        }

        synchronized (HierarchyService.class) {

            if (!powerSchemaReady) {
                hierarchyDAO.ensurePowerSchema();
                powerSchemaReady = true;
            }
        }
    }

    public List<City> getAllCities() throws SQLException {
        ensurePowerSchema();
        return hierarchyDAO.getAllCities();
    }

    public List<String> getBuildingTypes() throws SQLException {
        return hierarchyDAO.getBuildingTypes();
    }

    public int addChildComponent(
            int parentId,
            String parentType,
            String componentType,
            String name,
            String description,
            String buildingType,
            String addressLine1,
            String addressLine2
    ) throws SQLException {

        validateName(name);

        if (parentId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid parent component ID."
            );
        }

        if (parentType == null || parentType.isBlank()) {
            throw new IllegalArgumentException(
                    "Parent component type is required."
            );
        }

        if (componentType == null || componentType.isBlank()) {
            throw new IllegalArgumentException(
                    "Please select a component type."
            );
        }

        String normalizedParentType = parentType.trim();
        String normalizedType = componentType.trim();
        String normalizedName = name.trim();

        String normalizedDescription = normalize(description);
        String normalizedBuildingType = normalize(buildingType);
        String normalizedAddressLine1 = normalize(addressLine1);
        String normalizedAddressLine2 = normalize(addressLine2);

        String expectedChildType = switch (normalizedParentType) {
            case "City" -> "District";
            case "District" -> "Street";
            case "Street" -> "Building";
            default -> null;
        };

        if (expectedChildType == null ||
                !expectedChildType.equals(normalizedType)) {

            throw new IllegalArgumentException(
                    "Invalid hierarchy relationship. A " +
                            normalizedParentType +
                            " cannot contain a " +
                            normalizedType + "."
            );
        }

        boolean parentExists = hierarchyDAO.parentExists(
                normalizedParentType,
                parentId
        );

        if (!parentExists) {
            throw new IllegalArgumentException(
                    "The selected " +
                            normalizedParentType.toLowerCase() +
                            " does not exist."
            );
        }

        boolean duplicate = hierarchyDAO.componentNameExists(
                normalizedParentType,
                parentId,
                normalizedType,
                normalizedName
        );

        if (duplicate) {
            throw new IllegalArgumentException(
                    "A component with this name already exists under the selected parent."
            );
        }

        if ("City".equals(normalizedParentType) &&
                "District".equals(normalizedType)) {

            validateDescription(normalizedDescription);

            return hierarchyDAO.addDistrict(
                    parentId,
                    normalizedName,
                    normalizedDescription
            );
        }

        if ("District".equals(normalizedParentType) &&
                "Street".equals(normalizedType)) {

            validateDescription(normalizedDescription);

            return hierarchyDAO.addStreet(
                    parentId,
                    normalizedName,
                    normalizedDescription
            );
        }

        if ("Street".equals(normalizedParentType) &&
                "Building".equals(normalizedType)) {


            validateBuildingType(normalizedBuildingType);

            if (!hierarchyDAO.buildingTypeExists(
                    normalizedBuildingType
            )) {
                throw new IllegalArgumentException(
                        "Please select a valid building type from the list."
                );
            }

            validateOptionalAddress(normalizedAddressLine1);
            validateOptionalAddress(normalizedAddressLine2);

            return hierarchyDAO.addBuilding(
                    parentId,
                    normalizedName,
                    normalizedBuildingType,
                    normalizedAddressLine1,
                    normalizedAddressLine2
            );
        }

        throw new IllegalArgumentException(
                "Unable to create this component."
        );
    }

    public PowerOperationResult shutdownPower(
            String componentType,
            int componentId,
            Integer performedBy
    ) throws SQLException {

        return changePower(
                componentType,
                componentId,
                false,
                performedBy
        );
    }

    public PowerOperationResult restorePower(
            String componentType,
            int componentId,
            Integer performedBy
    ) throws SQLException {

        return changePower(
                componentType,
                componentId,
                true,
                performedBy
        );
    }

    private PowerOperationResult changePower(
            String componentType,
            int componentId,
            boolean powerOn,
            Integer performedBy
    ) throws SQLException {

        CityComponent component =
                loadComponent(componentType, componentId);

        int affected = powerOn
                ? component.restorePower()
                : component.shutdownPower();

        List<Building> buildings = component.getBuildings();

        List<Integer> buildingIds = new ArrayList<>();

        for (Building building : buildings) {
            buildingIds.add(building.getId());
        }

        if (affected > 0) {

            hierarchyDAO.applyPowerChange(
                    buildingIds,
                    powerOn,
                    component.getType(),
                    component.getId(),
                    component.getName(),
                    affected,
                    performedBy
            );
        }

        return new PowerOperationResult(
                component.getType(),
                component.getId(),
                component.getName(),
                powerOn ? "RESTORE" : "SHUTDOWN",
                affected,
                buildings.size(),
                component.getPowerStatus()
        );
    }

    public UsageReport getUsageReport(
            String componentType,
            int componentId
    ) throws SQLException {

        return loadComponent(componentType, componentId)
                .generateUsageReport();
    }

    public List<PowerOperationRecord> getPowerHistory(int limit)
            throws SQLException {

        ensurePowerSchema();

        return hierarchyDAO.getRecentPowerOperations(limit);
    }

    private CityComponent loadComponent(
            String componentType,
            int componentId
    ) throws SQLException {

        if (componentType == null
                || !COMPONENT_TYPES.contains(componentType.trim())) {

            throw new IllegalArgumentException(
                    "Select a valid component type."
            );
        }

        if (componentId <= 0) {
            throw new IllegalArgumentException(
                    "Select a valid component."
            );
        }

        String type = componentType.trim();

        for (City city : getAllCities()) {

            CityComponent found = findComponent(city, type, componentId);

            if (found != null) {
                return found;
            }
        }

        throw new IllegalArgumentException(
                "The selected " + type.toLowerCase() + " does not exist."
        );
    }

    private CityComponent findComponent(
            CityComponent current,
            String type,
            int id
    ) {

        if (current.getId() == id && current.getType().equals(type)) {
            return current;
        }

        for (CityComponent child : current.getChildren()) {

            CityComponent found = findComponent(child, type, id);

            if (found != null) {
                return found;
            }
        }

        return null;
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private void validateName(String name) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Component name is required."
            );
        }

        if (name.trim().length() > 150) {
            throw new IllegalArgumentException(
                    "Component name cannot exceed 150 characters."
            );
        }
    }

    private void validateDescription(String description) {

        if (description != null && description.length() > 500) {
            throw new IllegalArgumentException(
                    "Description cannot exceed 500 characters."
            );
        }
    }

    private void validateBuildingType(String buildingType) {

        if (buildingType == null || buildingType.isBlank()) {
            throw new IllegalArgumentException(
                    "Building type is required."
            );
        }

        if (buildingType.length() > 80) {
            throw new IllegalArgumentException(
                    "Building type cannot exceed 80 characters."
            );
        }
    }

    private void validateOptionalAddress(String address) {

        if (address != null && address.length() > 255) {
            throw new IllegalArgumentException(
                    "Address fields cannot exceed 255 characters."
            );
        }
    }
}
