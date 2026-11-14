package lk.sasindi.smartcity360.patterns.chain;

public class LocalStaffHandler extends AbstractStaffHandler {

    public LocalStaffHandler(EscalationRepository repository) {
        super(EscalationTier.LOCAL_STAFF, repository);
    }
}
