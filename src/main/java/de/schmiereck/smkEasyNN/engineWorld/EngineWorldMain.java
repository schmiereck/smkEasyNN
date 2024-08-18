package de.schmiereck.smkEasyNN.engineWorld;

import de.schmiereck.smkEasyNN.engineWorld.EngineWorldService.PositionType;
import de.schmiereck.smkEasyNN.engineWorld.GenericRuleEngine.InputStateNode;
import de.schmiereck.smkEasyNN.engineWorld.GenericRuleEngine.OutputState;
import de.schmiereck.smkEasyNN.engineWorld.GenericRuleEngine.OutputStateList;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static de.schmiereck.smkEasyNN.engineWorld.DemoRuleEngineService.addDemoRuleEngines;
import static de.schmiereck.smkEasyNN.engineWorld.GenericRuleEngineService.addGenericRuleEngines;
import static de.schmiereck.smkEasyNN.engineWorld.MatrixRuleEngineService.addMatrixRuleEngines;
import static de.schmiereck.smkEasyNN.engineWorld.SimpleRuleEngineService.addSimpleRuleEngines1;
import static de.schmiereck.smkEasyNN.engineWorld.SimpleRuleEngineService.addSimpleRuleEngines2;

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
        final EngineWorldService engineWorldService = new EngineWorldService(9);

        engineWorldService.
                locationEwStateArr[4].  // location (0:R, 1:G, 2:B, 3:R, 4:G, 5:B, 6:R, 7:G, 8:B)
                ewStateArr[1].          // 1 type
                ewStateArr[1].          // 2 energy
                ewStateArr[1].          // 1 impulse
                count = 40;

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
