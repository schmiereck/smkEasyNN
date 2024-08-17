package de.schmiereck.smkEasyNN.engineWorld;

public class RuleEngine {
    int inputTypePos;
    int outputTypePos;

    public RuleEngine(final int inputTypePos, final int outputTypePos) {
        this.inputTypePos = inputTypePos;
        this.outputTypePos = outputTypePos;
    }

    record RuleState(int positionTypePos,
                     int typePos,
                     int energyPos,
                     int impulsePos,
                     int count) {
    }

    RuleState calc(final RuleState positionRuleState, final EwState positionEwState) {
        final EwState inputTypeEwState = positionEwState.ewStateArr[this.inputTypePos];
        final EwState outputTypeEwState = positionEwState.ewStateArr[this.outputTypePos];

        final RuleState retPositionRuleState = new RuleState(
                2, //positionRuleState.positionTypePos,
                positionRuleState.typePos,
                positionRuleState.energyPos,
                positionRuleState.impulsePos,
                positionRuleState.count);

        return retPositionRuleState;
    }
}
