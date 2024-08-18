package de.schmiereck.smkEasyNN.engineWorld;

public class DemoRuleEngineService {

    static void addDemoRuleEngines(final EngineWorldService engineWorldService) {
        for (final EngineWorldService.PositionType inputPositionType : EngineWorldService.PositionType.values()) {
            final int inputTypePos = 1;
            final int inputEnergyPos = 1;
            final int inputImpulsePos = 1;
            final RuleEngine ruleEngine = new DemoRuleEngine(inputPositionType, inputTypePos, inputEnergyPos, inputImpulsePos);

            engineWorldService.addRuleEngine(ruleEngine);
        }
    }
}
