package de.schmiereck.smkEasyNN.engineWorld;

public class DemoRuleEngineService {

    static EngineWorldService initDemoWorld() {
        final var engineWorldService  = new EngineWorldService(9, 2, 2, 2);

        engineWorldService.
                locationEwStateArr[4].  // location (0:R, 1:G, 2:B, 3:R, 4:G, 5:B, 6:R, 7:G, 8:B)
                ewStateArr[1].          // 1 type
                ewStateArr[1].          // 2 energy
                ewStateArr[1].          // 1 impulse
                count = engineWorldService.calcStateCountPercentFromMax(60); // 60%

        return engineWorldService;
    }

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
