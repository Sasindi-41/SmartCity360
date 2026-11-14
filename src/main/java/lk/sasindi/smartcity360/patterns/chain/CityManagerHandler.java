package lk.sasindi.smartcity360.patterns.chain;


public class CityManagerHandler extends AbstractStaffHandler {

    public CityManagerHandler(EscalationRepository repository) {
        super(EscalationTier.CITY_MANAGER, repository);
    }
}
