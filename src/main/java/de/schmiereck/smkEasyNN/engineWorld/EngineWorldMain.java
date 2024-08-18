package de.schmiereck.smkEasyNN.engineWorld;

import javax.swing.*;

import static de.schmiereck.smkEasyNN.engineWorld.DemoRuleEngineService.addDemoRuleEngines;
import static de.schmiereck.smkEasyNN.engineWorld.DemoRuleEngineService.initDemoWorld;
import static de.schmiereck.smkEasyNN.engineWorld.GenericRuleEngineService.addGenericRuleEngines;
import static de.schmiereck.smkEasyNN.engineWorld.GenericRuleEngineService.initGenericWorld;
import static de.schmiereck.smkEasyNN.engineWorld.MatrixRuleEngineService.addMatrixRuleEngines;
import static de.schmiereck.smkEasyNN.engineWorld.MatrixRuleEngineService.initMatrixWorld;
import static de.schmiereck.smkEasyNN.engineWorld.SimpleRuleEngineService.*;

public class EngineWorldMain {
    public static final int VIEW_WIDTH = 1400;
    public static final int VIEW_HEIGHT = 600;
    public static final int VIEW_EXTRA_HEIGHT = VIEW_HEIGHT / 8;

    enum InitialRuleType {
        Demo,
        Simple1,
        Simple2,
        Generic,
        Matrix
    }
    //public static final InitialRuleType initialRuleType = InitialRuleType.Demo;
    //public static final InitialRuleType initialRuleType = InitialRuleType.Simple1;
    //public static final InitialRuleType initialRuleType = InitialRuleType.Simple2;
    //public static final InitialRuleType initialRuleType = InitialRuleType.Generic;
    public static final InitialRuleType initialRuleType = InitialRuleType.Matrix;

    public static void main(String[] args) {
        final EngineWorldService engineWorldService = //new EngineWorldService(9, 2, 2, 2);
            switch (initialRuleType) {
                case Demo -> initDemoWorld();
                case Simple1 -> initSimpleWorld();
                case Simple2 -> initSimpleWorld();
                case Generic -> initGenericWorld();
                case Matrix -> initMatrixWorld();
            };

        switch (initialRuleType) {
            case Demo -> addDemoRuleEngines(engineWorldService);
            case Simple1 -> addSimpleRuleEngines1(engineWorldService);
            case Simple2 -> addSimpleRuleEngines2(engineWorldService);
            case Generic -> addGenericRuleEngines(engineWorldService);
            case Matrix -> addMatrixRuleEngines(engineWorldService);
        }

        final JFrame frame = new JFrame("Engine-World");

        final EngineWorldPanel engineWorldPanel = new EngineWorldPanel(engineWorldService);

        frame.add(engineWorldPanel);
        frame.setSize(VIEW_WIDTH, VIEW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        new Thread(engineWorldService::runSimulation).start();
        new Thread(engineWorldPanel::view).start();
    }
}
