package de.schmiereck.smkEasyNN.genEden.service;

public class LifeService {
    private HexGridService hexGridService;

    public LifeService(final HexGridService hexGridService) {
        this.hexGridService = hexGridService;
    }

    public void runLife() {
        this.calcNext();
    }

    public void calcNext() {
        this.hexGridService.calcNext();
    }
}
