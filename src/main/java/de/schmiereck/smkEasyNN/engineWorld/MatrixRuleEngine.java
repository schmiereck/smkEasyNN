package de.schmiereck.smkEasyNN.engineWorld;

import java.util.List;
import java.util.Objects;

import static de.schmiereck.smkEasyNN.engineWorld.MatrixRuleEngineService.calcCountResult;
import static de.schmiereck.smkEasyNN.engineWorld.MatrixRuleEngineService.removeOutputState;

public class MatrixRuleEngine extends RuleEngine {
    record OutputState(EngineWorldService.PositionType positionType, int typePos, int energyPos, int impulsePos) {

        public int calcCount(final RuleState positionRuleState) {
            //final int retCount = calcCountResult(positionRuleState, 0) / 2;
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

        RuleState foundPositionRuleState = null;
        while (!outputStateList.outputStateList.isEmpty()) {
            final int outputStatePos = engineWorldService.rnd.nextInt(outputStateList.outputStateList.size());

            final OutputState outputState =
                    outputStateList.outputStateList.get(outputStatePos);

            final int calcedCount = outputState.calcCount(positionRuleState);

            final int inputResult =
                    calcCountResult(positionRuleState);

            final int newInputResult =
                    calcCountResult(positionRuleState, calcedCount);

            final int newOutputResult =
                    calcCountResult(outputState, calcedCount);

            if (inputResult == (newInputResult + newOutputResult)) {
                foundPositionRuleState = new RuleState(
                    outputState.positionType(),
                    outputState.typePos(),
                    outputState.energyPos(),
                    outputState.impulsePos(),
                    calcedCount);
                break;
            } else {
                outputStateList.outputStateList.remove(outputStatePos);
                removeOutputState(engineWorldService, this, outputState);
            }
        }

        if (Objects.nonNull(foundPositionRuleState)) {
            retPositionRuleState = foundPositionRuleState;
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
