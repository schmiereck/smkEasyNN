package de.schmiereck.smkEasyNN.engineWorld;

import de.schmiereck.smkEasyNN.engineWorld.EngineWorldService.PositionType;

public class GenericRuleEngine extends RuleEngine {

    PositionType outputPositionType;
    int outputTypePos;
    int outputEnergyPos;
    int outputImpulsePos;

    public GenericRuleEngine(final PositionType inputPositionType, final int inputTypePos, final int inputEnergyPos, final int inputImpulsePos,
                             final PositionType outputPositionType, final int outputTypePos, final int outputEnergyPos, final int outputImpulsePos) {
        super(inputPositionType, inputTypePos, inputEnergyPos, inputImpulsePos);

        this.outputPositionType = outputPositionType;
        this.outputTypePos = outputTypePos;
        this.outputEnergyPos = outputEnergyPos;
        this.outputImpulsePos = outputImpulsePos;
    }

    @Override
    RuleState calc(final RuleState positionRuleState, final EwState positionEwState) {
        final EwState inputTypeEwState = positionEwState.ewStateArr[this.inputTypePos];
        final EwState outputTypeEwState = positionEwState.ewStateArr[this.outputTypePos];

        final RuleState retPositionRuleState;

        if (this.inputPositionType == positionRuleState.positionType() &&
                this.inputEnergyPos == positionRuleState.energyPos() &&
                this.inputImpulsePos == positionRuleState.impulsePos()) {
            retPositionRuleState = new RuleState(
                    this.outputPositionType,
                    this.outputTypePos,
                    this.outputEnergyPos,
                    this.outputImpulsePos,
                    positionRuleState.count() / 2);
        } else {
            retPositionRuleState = new RuleState(
                    positionRuleState.positionType(),
                    positionRuleState.typePos(),
                    positionRuleState.energyPos(),
                    positionRuleState.impulsePos(),
                    positionRuleState.count());
        }

        return retPositionRuleState;
    }
}
