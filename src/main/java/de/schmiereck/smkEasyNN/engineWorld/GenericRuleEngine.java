package de.schmiereck.smkEasyNN.engineWorld;

import de.schmiereck.smkEasyNN.engineWorld.EngineWorldService.PositionType;

import java.util.List;

public class GenericRuleEngine extends RuleEngine {

    record OutputState(PositionType positionType, int typePos, int energyPos, int impulsePos) {
    }

    static class InputStateNode {
        InputStateNode[] nextInputStateNodeArr;
        InputStateNode childInputStateNode;
        OutputStateList outputStateList = null;
    }

    static class OutputStateList {
        List<OutputState> outputStateList;
    }

    //OutputStateList[][][][] outputStateListArr;
    InputStateNode inputStateNode;

    public GenericRuleEngine(final PositionType inputPositionType, final int inputTypePos, final int inputEnergyPos, final int inputImpulsePos,
                             //final OutputStateList[][][][] outputStateListArr,
                             final InputStateNode inputStateNode) {
        super(inputPositionType, inputTypePos, inputEnergyPos, inputImpulsePos);

        //this.outputStateListArr = outputStateListArr;
        this.inputStateNode = inputStateNode;
    }

    @Override
    RuleState calc(EngineWorldService engineWorldService, final RuleState positionRuleState, final EwState positionEwState) {
        final EwState inputTypeEwState = positionEwState.ewStateArr[this.inputTypePos];
        //final EwState outputTypeEwState = positionEwState.ewStateArr[this.outputTypePos];

        final RuleState retPositionRuleState;

        final OutputStateList outputStateList =
                GenericRuleEngineService.searchOutputStateList(inputStateNode, positionEwState);

//        if (this.inputPositionType == positionRuleState.positionType() &&
//                this.inputEnergyPos == positionRuleState.energyPos() &&
//                this.inputImpulsePos == positionRuleState.impulsePos()) {
//            retPositionRuleState = new RuleState(
//                    this.outputPositionType,
//                    this.outputTypePos,
//                    this.outputEnergyPos,
//                    this.outputImpulsePos,
//                    positionRuleState.count() / 2);
//        } else {
            retPositionRuleState = new RuleState(
                    positionRuleState.positionType(),
                    positionRuleState.typePos(),
                    positionRuleState.energyPos(),
                    positionRuleState.impulsePos(),
                    positionRuleState.count());
//        }

        return retPositionRuleState;
    }
}
