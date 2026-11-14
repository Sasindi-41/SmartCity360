package lk.sasindi.smartcity360.patterns.chain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComplaintChainTest {

    private static final int COMPLAINT_ID = 500;
    private static final int CITIZEN_ID = 7;
    private static final int PRIORITY_ID = 1;

    private static final int LOCAL_STAFF_ID = 201;
    private static final int SUPERVISOR_ID = 301;
    private static final int CITY_MANAGER_ID = 401;

    private FakeRepository repository;

    @BeforeEach
    void setUp() {

        repository = new FakeRepository();

        repository.staff.put(2, LOCAL_STAFF_ID);
        repository.staff.put(3, SUPERVISOR_ID);
        repository.staff.put(4, CITY_MANAGER_ID);

        repository.levels.put("2-1", 11);
        repository.levels.put("3-2", 12);
        repository.levels.put("4-3", 13);

        repository.actionTypes.put("submitted", 1);
        repository.actionTypes.put("escalated", 2);
    }

    private int route(String priority) throws SQLException {

        return new ComplaintRoutingChain(
                EscalationPolicy.requiredLevelFor(priority)
        ).process(repository, COMPLAINT_ID, CITIZEN_ID, PRIORITY_ID);
    }


    @Test
    @DisplayName("Priority maps to the required escalation level")
    void priorityMapping() {

        assertEquals(1, EscalationPolicy.requiredLevelFor("Low"));
        assertEquals(2, EscalationPolicy.requiredLevelFor("Medium"));
        assertEquals(3, EscalationPolicy.requiredLevelFor("High"));
        assertEquals(3, EscalationPolicy.requiredLevelFor("Critical"));
        assertEquals(2, EscalationPolicy.requiredLevelFor("  MEDIUM "));
    }

    @Test
    @DisplayName("Priority matching is independent of the default locale")
    void priorityMappingUsesLocaleIndependentCaseConversion() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertEquals(1, EscalationPolicy.requiredLevelFor("LOW"));
            assertEquals(2, EscalationPolicy.requiredLevelFor("MEDIUM"));
            assertEquals(3, EscalationPolicy.requiredLevelFor("HIGH"));
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    @DisplayName("Unknown or missing priority is rejected")
    void unsupportedPriority() {

        assertThrows(IllegalArgumentException.class,
                () -> EscalationPolicy.requiredLevelFor("urgent"));
        assertThrows(IllegalArgumentException.class,
                () -> EscalationPolicy.requiredLevelFor(null));
    }

    @Test
    @DisplayName("Low severity stays with Local Staff")
    void lowSeverity() throws SQLException {

        int assignee = route("Low");

        assertEquals(LOCAL_STAFF_ID, assignee);
        assertEquals(1, repository.records.size());

        EscalationRecord first = repository.records.get(0);

        assertEquals(LOCAL_STAFF_ID, first.assignedToId());
        assertEquals(CITIZEN_ID, first.assignedFromId());
        assertEquals(1, first.actionTypeId());
        assertEquals(11, first.escalationLevelId());
        assertEquals(COMPLAINT_ID, first.complaintId());
    }

    @Test
    @DisplayName("Medium severity goes Local Staff -> Supervisor")
    void mediumSeverity() throws SQLException {

        int assignee = route("Medium");

        assertEquals(SUPERVISOR_ID, assignee);
        assertEquals(2, repository.records.size());

        EscalationRecord second = repository.records.get(1);

        assertEquals(SUPERVISOR_ID, second.assignedToId());
        assertEquals(LOCAL_STAFF_ID, second.assignedFromId());
        assertEquals(2, second.actionTypeId());
        assertEquals(12, second.escalationLevelId());
    }

    @Test
    @DisplayName("High severity goes Local Staff -> Supervisor -> City Manager")
    void highSeverity() throws SQLException {

        int assignee = route("High");

        assertEquals(CITY_MANAGER_ID, assignee);
        assertEquals(3, repository.records.size());

        assertEquals(List.of(LOCAL_STAFF_ID, SUPERVISOR_ID, CITY_MANAGER_ID),
                repository.assignedToIds());

        EscalationRecord third = repository.records.get(2);

        assertEquals(SUPERVISOR_ID, third.assignedFromId());
        assertEquals(13, third.escalationLevelId());
    }

    @Test
    @DisplayName("Critical severity follows the same path as High")
    void criticalSeverity() throws SQLException {

        assertEquals(CITY_MANAGER_ID, route("Critical"));
        assertEquals(3, repository.records.size());
    }

    @Test
    @DisplayName("Every escalation step is recorded for the same complaint")
    void everyStepIsRecorded() throws SQLException {

        route("High");

        for (EscalationRecord record : repository.records) {
            assertEquals(COMPLAINT_ID, record.complaintId());
            assertTrue(record.remarks() != null && !record.remarks().isBlank());
        }

        assertEquals(AbstractStaffHandler.REMARK_SUBMITTED,
                repository.records.get(0).remarks());
        assertEquals(AbstractStaffHandler.REMARK_ESCALATED,
                repository.records.get(1).remarks());
    }

    @Test
    @DisplayName("A handler does not pass the complaint on when its level is enough")
    void handlerStopsWhenLevelIsEnough() throws SQLException {

        ComplaintHandler local = new LocalStaffHandler(repository);
        local.setNext(new SupervisorHandler(repository));

        RouteContext context = new RouteContext(
                COMPLAINT_ID, CITIZEN_ID, PRIORITY_ID, 1
        );

        local.handle(context);

        assertEquals(1, repository.records.size());
        assertEquals(1, context.getCompletedLevel());
    }

    @Test
    @DisplayName("A handler ignores a complaint that does not need its level")
    void handlerIgnoresHigherLevelOnly() throws SQLException {

        RouteContext context = new RouteContext(
                COMPLAINT_ID, CITIZEN_ID, PRIORITY_ID, 1
        );

        new CityManagerHandler(repository).handle(context);

        assertTrue(repository.records.isEmpty());
        assertEquals(0, context.getCompletedLevel());
    }

    @Test
    @DisplayName("A missing staff account is reported")
    void missingStaffIsReported() {

        repository.staff.remove(3);

        SQLException error = assertThrows(SQLException.class,
                () -> route("Medium"));

        assertTrue(error.getMessage().contains("role ID 3"));
    }

    @Test
    @DisplayName("A missing escalation level is reported")
    void missingLevelIsReported() {

        repository.levels.remove("2-1");

        assertThrows(SQLException.class, () -> route("Low"));
    }

    @Test
    @DisplayName("A missing action type is reported")
    void missingActionTypeIsReported() {

        repository.actionTypes.remove("escalated");

        assertThrows(SQLException.class, () -> route("Medium"));
    }

    private static class FakeRepository implements EscalationRepository {

        final Map<Integer, Integer> staff = new HashMap<>();
        final Map<String, Integer> levels = new HashMap<>();
        final Map<String, Integer> actionTypes = new HashMap<>();
        final List<EscalationRecord> records = new ArrayList<>();

        @Override
        public int findActiveStaffId(int roleId) {
            return staff.getOrDefault(roleId, -1);
        }

        @Override
        public int findEscalationLevelId(int roleId, int sequenceNumber) {
            return levels.getOrDefault(roleId + "-" + sequenceNumber, -1);
        }

        @Override
        public int findActionTypeId(String actionName) throws SQLException {

            Integer id = actionTypes.get(actionName.toLowerCase());

            if (id == null) {
                throw new SQLException("Action type not found: " + actionName);
            }

            return id;
        }

        @Override
        public void recordEscalation(EscalationRecord record) {
            records.add(record);
        }

        List<Integer> assignedToIds() {

            List<Integer> ids = new ArrayList<>();

            for (EscalationRecord record : records) {
                ids.add(record.assignedToId());
            }

            return ids;
        }
    }
}
