package de.schmiereck.smkEasyNN.engineWorld;

import java.util.List;

public class MatrixRuleEngine extends RuleEngine {
    record OutputState(EngineWorldService.PositionType positionType, int typePos, int energyPos, int impulsePos) {

        public int calcCount(final RuleState positionRuleState) {
            final int retCount = positionRuleState.count() / 2;
            return retCount;
        }
    }
    static class OutputStateList {
        List<MatrixRuleEngine.OutputState> outputStateList;
    }
    OutputStateList[][][][] outputStateListArr;

    public MatrixRuleEngine(final EngineWorldService.PositionType inputPositionType, final int inputTypePos, final int inputEnergyPos, final int inputImpulsePos,
                            final OutputStateList[][][][] outputStateListArr) {
        super(inputPositionType, inputTypePos, inputEnergyPos, inputImpulsePos);

        this.outputStateListArr = outputStateListArr;
    }

    @Override
    RuleState calc(final EngineWorldService engineWorldService, final RuleState positionRuleState, final EwState positionEwState) {
        final RuleState retPositionRuleState;

        final int[][][][] stateCountMatrixArr =
            MatrixRuleEngineService.createStateCountMatrix(engineWorldService, positionRuleState.positionType(), positionEwState);

        final OutputStateList outputStateList =
            MatrixRuleEngineService.searchMatchingOutputStateList(this.outputStateListArr, stateCountMatrixArr);

        if (!outputStateList.outputStateList.isEmpty()) {
            final OutputState outputState =
                    outputStateList.outputStateList.get(engineWorldService.rnd.nextInt(outputStateList.outputStateList.size()));

            retPositionRuleState = new RuleState(
                    outputState.positionType(),
                    outputState.typePos(),
                    outputState.energyPos(),
                    outputState.impulsePos(),
                    outputState.calcCount(positionRuleState));
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
