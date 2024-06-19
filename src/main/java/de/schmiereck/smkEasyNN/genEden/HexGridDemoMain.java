package de.schmiereck.smkEasyNN.genEden;

import javafx.application.Application;

public class HexGridDemoMain {
    public static void main(String[] args) {
        GenEdenController.demoMode = true;
        //HexGridApp.main(args);
        Application.launch(HexGridApp.class, args);
    }
}
