package lk.sasindi.smartcity360.patterns.chain;

import java.sql.SQLException;

public abstract class AbstractStaffHandler extends ComplaintHandler {

    static final String ACTION_SUBMITTED = "Submitted";
    static final String ACTION_ESCALATED = "Escalated";

    static final String REMARK_SUBMITTED =
            "Complaint submitted and assigned to Local Staff.";
    static final String REMARK_ESCALATED =
            "Complaint escalated to the next handling level.";

    private final EscalationTier tier;
    private final EscalationRepository repository;

    protected AbstractStaffHandler(
            EscalationTier tier,
            EscalationRepository repository
    ) {
        this.tier = tier;
        this.repository = repository;
    }

    public EscalationTier getTier() {
        return tier;
    }

    @Override
    public void handle(RouteContext context) throws SQLException {

        if (tier.getSequenceNumber() > context.getRequiredLevel()) {
            return;
        }

        int staffId = repository.findActiveStaffId(tier.getRoleId());

        int escalationLevelId = repository.findEscalationLevelId(
                tier.getRoleId(),
                tier.getSequenceNumber()
        );

        if (staffId <= 0 || escalationLevelId <= 0) {
            throw new SQLException(
                    "Missing active staff account or escalation level "
                            + "for role ID " + tier.getRoleId()
            );
        }

        boolean firstLevel = tier.getSequenceNumber() == 1;

        int actionTypeId = repository.findActionTypeId(
                firstLevel ? ACTION_SUBMITTED : ACTION_ESCALATED
        );

        int assignedFrom = context.getPreviousAssigneeId() == null
                ? context.getCitizenId()
                : context.getPreviousAssigneeId();

        repository.recordEscalation(new EscalationRecord(
                firstLevel ? REMARK_SUBMITTED : REMARK_ESCALATED,
                staffId,
                assignedFrom,
                context.getComplaintId(),
                actionTypeId,
                escalationLevelId
        ));

        context.setCurrentAssigneeId(staffId);
        context.setPreviousAssigneeId(staffId);
        context.setCompletedLevel(tier.getSequenceNumber());

        passToNext(context);
    }
}
