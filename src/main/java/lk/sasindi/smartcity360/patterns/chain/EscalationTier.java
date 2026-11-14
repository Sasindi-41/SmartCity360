package lk.sasindi.smartcity360.patterns.chain;

public enum EscalationTier {

    LOCAL_STAFF(2, 1, "Local Staff"),
    SUPERVISOR(3, 2, "Supervisor"),
    CITY_MANAGER(4, 3, "City Manager");

    private final int roleId;
    private final int sequenceNumber;
    private final String displayName;

    EscalationTier(int roleId, int sequenceNumber, String displayName) {
        this.roleId = roleId;
        this.sequenceNumber = sequenceNumber;
        this.displayName = displayName;
    }

    public int getRoleId() {
        return roleId;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public String getDisplayName() {
        return displayName;
    }
}
