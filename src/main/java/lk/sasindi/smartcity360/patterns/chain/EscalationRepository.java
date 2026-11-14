package lk.sasindi.smartcity360.patterns.chain;

import java.sql.SQLException;

public interface EscalationRepository {

    int findActiveStaffId(int roleId) throws SQLException;

    int findEscalationLevelId(int roleId, int sequenceNumber)
            throws SQLException;

    int findActionTypeId(String actionName) throws SQLException;

    void recordEscalation(EscalationRecord record) throws SQLException;
}
