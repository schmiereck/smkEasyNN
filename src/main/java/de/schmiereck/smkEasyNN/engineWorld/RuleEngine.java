package de.schmiereck.smkEasyNN.engineWorld;

import de.schmiereck.smkEasyNN.engineWorld.EngineWorldService.PositionType;

public abstract class RuleEngine {
    PositionType inputPositionType;
    int inputTypePos;
    int inputEnergyPos;
    int inputImpulsePos;

    public RuleEngine(final PositionType inputPositionType, final int inputTypePos, final int inputEnergyPos, final int inputImpulsePos) {
        this.inputPositionType = inputPositionType;
        this.inputTypePos = inputTypePos;
        this.inputEnergyPos = inputEnergyPos;
        this.inputImpulsePos = inputImpulsePos;
    }

    record RuleState(PositionType positionType,
                     int typePos,
                     int energyPos,
                     int impulsePos,
                     int count) {
    }

    abstract RuleState calc(final EngineWorldService engineWorldService, final RuleState positionRuleState, final EwState positionEwState);
}
