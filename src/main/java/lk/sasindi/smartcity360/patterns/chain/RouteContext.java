package lk.sasindi.smartcity360.patterns.chain;

public class RouteContext {

    private final int complaintId;
    private final int citizenId;
    private final int priorityId;
    private final int requiredLevel;

    private Integer currentAssigneeId;
    private Integer previousAssigneeId;
    private int completedLevel;

    public RouteContext(
            int complaintId,
            int citizenId,
            int priorityId,
            int requiredLevel
    ) {
        this.complaintId = complaintId;
        this.citizenId = citizenId;
        this.priorityId = priorityId;
        this.requiredLevel = requiredLevel;
    }

    public int getComplaintId() {
        return complaintId;
    }

    public int getCitizenId() {
        return citizenId;
    }

    public int getPriorityId() {
        return priorityId;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public Integer getCurrentAssigneeId() {
        return currentAssigneeId;
    }

    public void setCurrentAssigneeId(Integer currentAssigneeId) {
        this.currentAssigneeId = currentAssigneeId;
    }

    public Integer getPreviousAssigneeId() {
        return previousAssigneeId;
    }

    public void setPreviousAssigneeId(Integer previousAssigneeId) {
        this.previousAssigneeId = previousAssigneeId;
    }

    public int getCompletedLevel() {
        return completedLevel;
    }

    public void setCompletedLevel(int completedLevel) {
        this.completedLevel = completedLevel;
    }
}
