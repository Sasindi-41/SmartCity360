package lk.sasindi.smartcity360.patterns.composite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompositePatternTest {

    private City city;
    private District district;
    private Street street1;
    private Street street2;
    private Building b1;
    private Building b2;
    private Building b3;

    @BeforeEach
    void buildCity() {

        city = new City(1, "Kandy", "CTY-001", "");
        district = new District(1, "Central", "DST-001", "");
        street1 = new Street(1, "Main Street", "STR-001", "");
        street2 = new Street(2, "Lake Road", "STR-002", "");

        b1 = new Building(1, "Library", "BLD-001", null, "Public",
                null, null, true, 10.5);
        b2 = new Building(2, "School", "BLD-002", null, "Education",
                null, null, true, 4.5);
        b3 = new Building(3, "Clinic", "BLD-003", null, "Health",
                null, null, true, 100.0);

        city.addChild(district);
        district.addChild(street1);
        district.addChild(street2);
        street1.addChild(b1);
        street1.addChild(b2);
        street2.addChild(b3);
    }

    @Test
    @DisplayName("Hierarchy is built and counted")
    void hierarchyIsBuilt() {

        assertEquals(3, city.getTotalBuildings());
        assertEquals(7, city.getTotalComponents());
        assertEquals(1, city.getChildren().size());
        assertEquals(3, city.getBuildings().size());
    }

    @Test
    @DisplayName("Invalid parent-child relationships are rejected")
    void invalidRelationshipsAreRejected() {

        assertThrows(IllegalArgumentException.class,
                () -> city.addChild(street1));
        assertThrows(IllegalArgumentException.class,
                () -> district.addChild(b1));
        assertThrows(IllegalArgumentException.class,
                () -> street1.addChild(district));
        assertThrows(UnsupportedOperationException.class,
                () -> b1.addChild(b2));
    }

    @Test
    @DisplayName("The same child cannot be added twice")
    void duplicateChildIsRejected() {

        assertThrows(IllegalArgumentException.class,
                () -> street1.addChild(b1));
    }

    @Test
    @DisplayName("Removing a component that is not a child fails")
    void removingMissingChildFails() {

        assertThrows(IllegalArgumentException.class,
                () -> street2.removeChild(b1));

        street1.removeChild(b2);

        assertEquals(1, street1.getBuildings().size());
    }

    @Test
    @DisplayName("Shutdown at city level switches off every building")
    void shutdownAtCityLevel() {

        int changed = city.shutdownPower();

        assertEquals(3, changed);
        assertEquals(PowerStatus.OFF, city.getPowerStatus());
        assertEquals(PowerStatus.OFF, street1.getPowerStatus());
        assertEquals(0, city.getPoweredBuildings());
    }

    @Test
    @DisplayName("Shutting down twice changes nothing the second time")
    void shutdownIsRepeatable() {

        city.shutdownPower();

        assertEquals(0, city.shutdownPower());
    }

    @Test
    @DisplayName("Restore at street level leaves other streets off (partial state)")
    void restoreAtStreetLevel() {

        city.shutdownPower();

        int changed = street1.restorePower();

        assertEquals(2, changed);
        assertEquals(PowerStatus.ACTIVE, street1.getPowerStatus());
        assertEquals(PowerStatus.OFF, street2.getPowerStatus());
        assertEquals(PowerStatus.PARTIAL, district.getPowerStatus());
        assertEquals(PowerStatus.PARTIAL, city.getPowerStatus());
    }

    @Test
    @DisplayName("Restore at district level switches everything below back on")
    void restoreAtDistrictLevel() {

        city.shutdownPower();

        assertEquals(3, district.restorePower());
        assertEquals(PowerStatus.ACTIVE, city.getPowerStatus());
    }

    @Test
    @DisplayName("A street with no buildings has nothing switched off")
    void emptyStreetIsActive() {

        Street empty = new Street(9, "Empty", "STR-009", "");

        assertEquals(PowerStatus.ACTIVE, empty.getPowerStatus());
        assertEquals(0, empty.shutdownPower());
        assertEquals(0.0, empty.getEnergyUsage(), 0.0001);
    }

    @Test
    @DisplayName("Usage is added up at every level")
    void usageIsAggregated() {

        assertEquals(15.0, street1.getEnergyUsage(), 0.0001);
        assertEquals(100.0, street2.getEnergyUsage(), 0.0001);
        assertEquals(115.0, district.getEnergyUsage(), 0.0001);
        assertEquals(115.0, city.getEnergyUsage(), 0.0001);
    }

    @Test
    @DisplayName("Usage report has the same tree shape as the hierarchy")
    void usageReportMatchesHierarchy() {

        UsageReport city = this.city.generateUsageReport();
        UsageReport district = city.getChildren().get(0);
        UsageReport street = district.getChildren().get(0);
        UsageReport building = street.getChildren().get(0);

        assertEquals("City", city.getType());
        assertEquals("District", district.getType());
        assertEquals("Street", street.getType());
        assertEquals("Building", building.getType());

        assertEquals(115.0, city.getUsageKwh(), 0.0001);
        assertEquals(3, city.getTotalBuildings());
        assertEquals(2, district.getChildren().size());
        assertTrue(building.getChildren().isEmpty());
        assertEquals(10.5, building.getUsageKwh(), 0.0001);
    }

    @Test
    @DisplayName("Usage report at street level only covers that street")
    void usageReportAtStreetLevel() {

        UsageReport report = street1.generateUsageReport();

        assertEquals(15.0, report.getUsageKwh(), 0.0001);
        assertEquals(2, report.getTotalBuildings());
        assertEquals(2, report.getChildren().size());
    }

    @Test
    @DisplayName("Old 7-argument Building constructor still works (powered on, no usage)")
    void legacyConstructorDefaults() {

        Building legacy = new Building(9, "Old", "BLD-009", null,
                "Public", null, null);

        assertTrue(legacy.isPowerOn());
        assertEquals(0.0, legacy.getEnergyUsage(), 0.0001);
    }
}
