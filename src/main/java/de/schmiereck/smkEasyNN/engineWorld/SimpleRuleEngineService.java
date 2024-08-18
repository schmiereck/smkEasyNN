package de.schmiereck.smkEasyNN.engineWorld;

public class SimpleRuleEngineService {

    static EngineWorldService initSimpleWorld() {
        final var engineWorldService  = new EngineWorldService(9, 2, 2, 2);

        engineWorldService.
                locationEwStateArr[4].  // location (0:R, 1:G, 2:B, 3:R, 4:G, 5:B, 6:R, 7:G, 8:B)
                ewStateArr[1].          // 1 type
                ewStateArr[1].          // 2 energy
                ewStateArr[1].          // 1 impulse
                count = engineWorldService.calcStateCountPercentFromMax(60); // 60%

        return engineWorldService;
    }

    static void addSimpleRuleEngines2(final EngineWorldService engineWorldService) {
        {
            final EngineWorldService.PositionType inputPositionType = EngineWorldService.PositionType.G;
            final int inputTypePos = 1;
            final int inputEnergyPos = 1;
            final int inputImpulsePos = 1;

            final EngineWorldService.PositionType outputPositionType = EngineWorldService.PositionType.R;    // G to Left.
            final int outputTypePos = 1;
            final int outputEnergyPos = 1;
            final int outputImpulsePos = 1;

            final RuleEngine ruleEngine = new SimpleRuleEngine(
                    inputPositionType, inputTypePos, inputEnergyPos, inputImpulsePos,
                    outputPositionType, outputTypePos, outputEnergyPos, outputImpulsePos);

            engineWorldService.addRuleEngine(ruleEngine);
        }
        {
            final EngineWorldService.PositionType inputPositionType = EngineWorldService.PositionType.R;
            final int inputTypePos = 1;
            final int inputEnergyPos = 1;
            final int inputImpulsePos = 1;

            final EngineWorldService.PositionType outputPositionType = EngineWorldService.PositionType.G;    // R to Right.
            final int outputTypePos = 1;
            final int outputEnergyPos = 1;
            final int outputImpulsePos = 1;

            final RuleEngine ruleEngine = new SimpleRuleEngine(
                    inputPositionType, inputTypePos, inputEnergyPos, inputImpulsePos,
                    outputPositionType, outputTypePos, outputEnergyPos, outputImpulsePos);

            engineWorldService.addRuleEngine(ruleEngine);
        }
    }

    static void addSimpleRuleEngines1(final EngineWorldService engineWorldService) {
        {
            final EngineWorldService.PositionType inputPositionType = EngineWorldService.PositionType.G;
            final int inputTypePos = 1;
            final int inputEnergyPos = 1;
            final int inputImpulsePos = 1;

            final EngineWorldService.PositionType outputPositionType = EngineWorldService.PositionType.G;    // Stay on position.
            final int outputTypePos = 0;
            final int outputEnergyPos = 1;
            final int outputImpulsePos = 1;

            final RuleEngine ruleEngine = new SimpleRuleEngine(
                    inputPositionType, inputTypePos, inputEnergyPos, inputImpulsePos,
                    outputPositionType, outputTypePos, outputEnergyPos, outputImpulsePos);

            engineWorldService.addRuleEngine(ruleEngine);
        }
        {
            final EngineWorldService.PositionType inputPositionType = EngineWorldService.PositionType.G;
            final int inputTypePos = 0;
            final int inputEnergyPos = 1;
            final int inputImpulsePos = 1;

            final EngineWorldService.PositionType outputPositionType = EngineWorldService.PositionType.G;    // Stay on position.
            final int outputTypePos = 1;
            final int outputEnergyPos = 1;
            final int outputImpulsePos = 1;

            final RuleEngine ruleEngine = new SimpleRuleEngine(
                    inputPositionType, inputTypePos, inputEnergyPos, inputImpulsePos,
                    outputPositionType, outputTypePos, outputEnergyPos, outputImpulsePos);

            engineWorldService.addRuleEngine(ruleEngine);
        }
    }
}
