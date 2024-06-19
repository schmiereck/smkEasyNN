package de.schmiereck.smkEasyNN.genEden.service;

import de.schmiereck.smkEasyNN.abstractWorld.service.BaseServiceContext;

public class GenEdenServiceContext extends BaseServiceContext {
    private PartServiceInterface partService;

    public void setPartService(final PartServiceInterface partService) {
        this.partService = partService;
    }
    @Override
    public PartServiceInterface getPartService() {
        return this.partService;
    }
}
