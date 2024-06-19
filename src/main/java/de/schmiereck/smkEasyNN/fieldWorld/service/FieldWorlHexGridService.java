package de.schmiereck.smkEasyNN.fieldWorld.service;

import de.schmiereck.smkEasyNN.abstractWorld.service.BaseHexGridService;
import de.schmiereck.smkEasyNN.genEden.service.GridNode;
import de.schmiereck.smkEasyNN.genEden.service.HexGrid;

public class FieldWorlHexGridService extends BaseHexGridService<FieldsWorldServiceContext> {

    public FieldWorlHexGridService(final FieldsWorldServiceContext serviceContext) {
        super(serviceContext);
    }

    @Override
    public void calcNext() {
        
    }

    @Override
    public int retrieveStepCount() {
        return 0;
    }

    @Override
    public int retrievePartCount() {
        return 0;
    }
}
