package de.schmiereck.smkEasyNN.engineWorld;

import de.schmiereck.smkEasyNN.engineWorld.EngineWorldService.PositionType;

public class DemoRuleEngine extends RuleEngine {

    public DemoRuleEngine(final PositionType inputPositionType, final int inputTypePos, final int inputEnergyPos, final int inputImpulsePos) {
        super(inputPositionType, inputTypePos, inputEnergyPos, inputImpulsePos);
    }

    @Override
    RuleState calc(EngineWorldService engineWorldService, final RuleState positionRuleState, final EwState positionEwState) {
        final PositionType nextPositionType =
            switch (positionRuleState.positionType()) {
                case R -> PositionType.G;
                case G -> PositionType.B;
                case B -> PositionType.R;
            };
        final RuleState retPositionRuleState = new RuleState(
                nextPositionType,
                positionRuleState.typePos(),
                positionRuleState.energyPos(),
                positionRuleState.impulsePos(),
                positionRuleState.count() / 2);

        return retPositionRuleState;
    }
}
