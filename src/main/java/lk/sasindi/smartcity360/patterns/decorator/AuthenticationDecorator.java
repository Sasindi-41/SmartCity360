package lk.sasindi.smartcity360.patterns.decorator;

import lk.sasindi.smartcity360.dao.EnergyDAO;
import lk.sasindi.smartcity360.model.EnergyStreamContext;

public class AuthenticationDecorator extends EnergyStreamDecorator {

    private final EnergyDAO energyDAO;

    public AuthenticationDecorator(
            EnergyStream wrappedStream,
            EnergyDAO energyDAO
    ) {
        super(wrappedStream);
        this.energyDAO = energyDAO;
    }

    @Override
    public void process(EnergyStreamContext context) throws Exception {

        boolean valid = energyDAO.verifyMeterToken(
                context.getMeterId(),
                context.getToken()
        );

        context.setAuthenticated(valid);

        if (!valid) {

            context.addLog(
                    "AuthenticationDecorator",
                    "AUTHENTICATE",
                    false,
                    "Meter authentication failed. Invalid token or meter credential."
            );

            throw new SecurityException(
                    "Meter authentication failed."
            );
        }

        context.addLog(
                "AuthenticationDecorator",
                "AUTHENTICATE",
                true,
                "Meter token successfully verified using SHA-256 hash comparison."
        );

        super.process(context);
    }
}