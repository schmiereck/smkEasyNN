package de.schmiereck.smkEasyNN.engineWorld;

import javax.swing.*;

public class EngineWorldMain {
    public static final int VIEW_WIDTH = 1400;
    public static final int VIEW_HEIGHT = 600;
    public static final int VIEW_EXTRA_HEIGHT = VIEW_HEIGHT / 8;

    public static void main(String[] args) {
        final EngineWorldService engineWorldService = new EngineWorldService();
        engineWorldService.init();

        engineWorldService.locationEwStateArr[4].ewStateArr[1].ewStateArr[2].ewStateArr[1].count = 40;

        final RuleEngine ruleEngine = new RuleEngine(1, 1);
        engineWorldService.addRuleEngine(ruleEngine);

        final JFrame frame = new JFrame("Engine-World");

        final EngineWorldPanel engineWorldPanel = new EngineWorldPanel(engineWorldService);

        frame.add(engineWorldPanel);
        frame.setSize(VIEW_WIDTH, VIEW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        new Thread(engineWorldPanel::simulate).start();
    }
}
