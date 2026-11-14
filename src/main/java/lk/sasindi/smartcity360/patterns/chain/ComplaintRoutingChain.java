package lk.sasindi.smartcity360.patterns.chain;

import java.sql.SQLException;

public class ComplaintRoutingChain {

    private final int requiredLevel;

    public ComplaintRoutingChain(int requiredLevel) {
        this.requiredLevel = requiredLevel;
    }

    public int process(
            EscalationRepository repository,
            int complaintId,
            int citizenId,
            int priorityId
    ) throws SQLException {

        RouteContext context = new RouteContext(
                complaintId,
                citizenId,
                priorityId,
                requiredLevel
        );

        buildChain(repository).handle(context);

        if (context.getCurrentAssigneeId() == null) {
            throw new SQLException(
                    "No active staff member was available for assignment."
            );
        }

        return context.getCurrentAssigneeId();
    }

    private ComplaintHandler buildChain(EscalationRepository repository) {

        ComplaintHandler localStaff = new LocalStaffHandler(repository);

        localStaff
                .setNext(new SupervisorHandler(repository))
                .setNext(new CityManagerHandler(repository));

        return localStaff;
    }
}
