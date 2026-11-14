package lk.sasindi.smartcity360.patterns.chain;

public class SupervisorHandler extends AbstractStaffHandler {

    public SupervisorHandler(EscalationRepository repository) {
        super(EscalationTier.SUPERVISOR, repository);
    }
}
