package de.schmiereck.smkEasyNN.engineWorld;

import de.schmiereck.smkEasyNN.engineWorld.EngineWorldService.PositionType;

import javax.swing.*;

public class EngineWorldMain {
    public static final int VIEW_WIDTH = 1400;
    public static final int VIEW_HEIGHT = 600;
    public static final int VIEW_EXTRA_HEIGHT = VIEW_HEIGHT / 8;

    //public static final boolean UseDemoRuleEngine = true;
    public static final boolean UseDemoRuleEngine = false;

    public static void main(String[] args) {
        final EngineWorldService engineWorldService = new EngineWorldService(9);

        engineWorldService.
                locationEwStateArr[4].  // location (0:R, 1:G, 2:B, 3:R, 4:G, 5:B, 6:R, 7:G, 8:B)
                ewStateArr[1].          // type
                ewStateArr[2].          // energy
                ewStateArr[1].          // impulse
                count = 40;

        if (UseDemoRuleEngine)
        {
            addDemoRuleEngines(engineWorldService);
        }
        else
        {
            addGenericRuleEngines(engineWorldService);
            //addGenericRuleEngines1(engineWorldService);
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

    private static void addDemoRuleEngines(EngineWorldService engineWorldService) {
        for (final PositionType inputPositionType : PositionType.values()) {
            final int inputTypePos = 1;
            final int inputEnergyPos = 2;
            final int inputImpulsePos = 1;
            final RuleEngine ruleEngine = new DemoRuleEngine(inputPositionType, inputTypePos, inputEnergyPos, inputImpulsePos);

            engineWorldService.addRuleEngine(ruleEngine);
        }
    }

    private static void addGenericRuleEngines(EngineWorldService engineWorldService) {
        for (final PositionType inputPositionType : PositionType.values()) {
            for (int inputTypePos = 0; inputTypePos < engineWorldService.typeCount; inputTypePos++) {
                for (int inputEnergyPos = 0; inputEnergyPos < engineWorldService.energyCount; inputEnergyPos++) {
                    for (int inputImpulsePos = 0; inputImpulsePos < engineWorldService.impulseCount; inputImpulsePos++) {

                        Quatsch, es kann für einen Input nur einen Output geben...

                        for (final PositionType outputPositionType : PositionType.values()) {
                            for (int outputTypePos = 0; outputTypePos < engineWorldService.typeCount; outputTypePos++) {
                                for (int outputEnergyPos = 0; outputEnergyPos < engineWorldService.energyCount; outputEnergyPos++) {
                                    for (int outputImpulsePos = 0; outputImpulsePos < engineWorldService.impulseCount; outputImpulsePos++) {

                                        final RuleEngine ruleEngine = new GenericRuleEngine(
                                                inputPositionType, inputTypePos, inputEnergyPos, inputImpulsePos,
                                                outputPositionType, outputTypePos, outputEnergyPos, outputImpulsePos);

                                        engineWorldService.addRuleEngine(ruleEngine);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void addGenericRuleEngines2(EngineWorldService engineWorldService) {
        {
            final PositionType inputPositionType = PositionType.G;
            final int inputTypePos = 1;
            final int inputEnergyPos = 2;
            final int inputImpulsePos = 1;

            final PositionType outputPositionType = PositionType.R;    // G to Left.
            final int outputTypePos = 1;
            final int outputEnergyPos = 2;
            final int outputImpulsePos = 1;

            final RuleEngine ruleEngine = new GenericRuleEngine(
                    inputPositionType, inputTypePos, inputEnergyPos, inputImpulsePos,
                    outputPositionType, outputTypePos, outputEnergyPos, outputImpulsePos);

            engineWorldService.addRuleEngine(ruleEngine);
        }
        {
            final PositionType inputPositionType = PositionType.R;
            final int inputTypePos = 1;
            final int inputEnergyPos = 2;
            final int inputImpulsePos = 1;

            final PositionType outputPositionType = PositionType.G;    // R to Right.
            final int outputTypePos = 1;
            final int outputEnergyPos = 2;
            final int outputImpulsePos = 1;

            final RuleEngine ruleEngine = new GenericRuleEngine(
                    inputPositionType, inputTypePos, inputEnergyPos, inputImpulsePos,
                    outputPositionType, outputTypePos, outputEnergyPos, outputImpulsePos);

            engineWorldService.addRuleEngine(ruleEngine);
        }
    }

    private static void addGenericRuleEngines1(EngineWorldService engineWorldService) {
        {
            final PositionType inputPositionType = PositionType.G;
            final int inputTypePos = 1;
            final int inputEnergyPos = 2;
            final int inputImpulsePos = 1;

            final PositionType outputPositionType = PositionType.G;    // Stay on position.
            final int outputTypePos = 2;
            final int outputEnergyPos = 2;
            final int outputImpulsePos = 1;

            final RuleEngine ruleEngine = new GenericRuleEngine(
                    inputPositionType, inputTypePos, inputEnergyPos, inputImpulsePos,
                    outputPositionType, outputTypePos, outputEnergyPos, outputImpulsePos);

            engineWorldService.addRuleEngine(ruleEngine);
        }
        {
            final PositionType inputPositionType = PositionType.G;
            final int inputTypePos = 2;
            final int inputEnergyPos = 2;
            final int inputImpulsePos = 1;

            final PositionType outputPositionType = PositionType.G;    // Stay on position.
            final int outputTypePos = 1;
            final int outputEnergyPos = 2;
            final int outputImpulsePos = 1;

            final RuleEngine ruleEngine = new GenericRuleEngine(
                    inputPositionType, inputTypePos, inputEnergyPos, inputImpulsePos,
                    outputPositionType, outputTypePos, outputEnergyPos, outputImpulsePos);

            engineWorldService.addRuleEngine(ruleEngine);
        }
    }
}
