package de.schmiereck.smkEasyNN.genEden.service;

public class ServiceContext {
    private PartServiceInterface partService;

    public void setPartService(final PartServiceInterface partService) {
        this.partService = partService;
    }
    public PartServiceInterface getPartService() {
        return this.partService;
    }
}
