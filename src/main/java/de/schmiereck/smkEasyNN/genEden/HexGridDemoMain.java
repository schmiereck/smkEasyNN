package de.schmiereck.smkEasyNN.genEden;

import de.schmiereck.smkEasyNN.genEden.service.HexGridService;

public class HexGridDemoMain {
    public static void main(String[] args) {
        HexGridService.demoMode = true;
        HexGridApp.main(args);
    }
}
