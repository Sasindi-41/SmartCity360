package lk.sasindi.smartcity360.patterns.decorator;

import lk.sasindi.smartcity360.model.EnergyStreamContext;

public abstract class EnergyStreamDecorator implements EnergyStream {

    protected final EnergyStream wrappedStream;

    protected EnergyStreamDecorator(EnergyStream wrappedStream) {
        this.wrappedStream = wrappedStream;
    }

    @Override
    public void process(EnergyStreamContext context) throws Exception {
        wrappedStream.process(context);
    }
}