package lk.sasindi.smartcity360.patterns.decorator;

import lk.sasindi.smartcity360.model.EnergyStreamContext;

public interface EnergyStream {

    void process(EnergyStreamContext context) throws Exception;
}