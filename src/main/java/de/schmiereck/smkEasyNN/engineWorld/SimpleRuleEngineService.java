package de.schmiereck.smkEasyNN.engineWorld;

public class SimpleRuleEngineService {

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
