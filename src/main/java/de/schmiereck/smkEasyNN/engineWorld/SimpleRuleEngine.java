package de.schmiereck.smkEasyNN.engineWorld;

/**
 * Simple Rule-Engine: One Input-State has one Output-State.
 */
public class SimpleRuleEngine extends RuleEngine {

    EngineWorldService.PositionType outputPositionType;
    int outputTypePos;
    int outputEnergyPos;
    int outputImpulsePos;

    public SimpleRuleEngine(final EngineWorldService.PositionType inputPositionType, final int inputTypePos, final int inputEnergyPos, final int inputImpulsePos,
                             final EngineWorldService.PositionType outputPositionType, final int outputTypePos, final int outputEnergyPos, final int outputImpulsePos) {
        super(inputPositionType, inputTypePos, inputEnergyPos, inputImpulsePos);

        this.outputPositionType = outputPositionType;
        this.outputTypePos = outputTypePos;
        this.outputEnergyPos = outputEnergyPos;
        this.outputImpulsePos = outputImpulsePos;
    }

    @Override
    RuleEngine.RuleState calc(EngineWorldService engineWorldService, final RuleState positionRuleState, final EwState positionEwState) {
        final RuleEngine.RuleState retPositionRuleState;

        if (this.inputPositionType == positionRuleState.positionType() &&
                this.inputEnergyPos == positionRuleState.energyPos() &&
                this.inputImpulsePos == positionRuleState.impulsePos()) {
            retPositionRuleState = new RuleEngine.RuleState(
                    this.outputPositionType,
                    this.outputTypePos,
                    this.outputEnergyPos,
                    this.outputImpulsePos,
                    positionRuleState.count() / 2);
        } else {
            retPositionRuleState = new RuleEngine.RuleState(
                    positionRuleState.positionType(),
                    positionRuleState.typePos(),
                    positionRuleState.energyPos(),
                    positionRuleState.impulsePos(),
                    positionRuleState.count());
        }

        return retPositionRuleState;
    }
}
