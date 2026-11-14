package lk.sasindi.smartcity360.patterns.chain;

public record EscalationRecord(
        String remarks,
        int assignedToId,
        int assignedFromId,
        int complaintId,
        int actionTypeId,
        int escalationLevelId
) {
}
