package de.schmiereck.smkEasyNN.engineWorld;

import de.schmiereck.smkEasyNN.engineWorld.MatrixRuleEngine.OutputStateList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MatrixRuleEngineService {

    static void addMatrixRuleEngines(final EngineWorldService engineWorldService) {
        for (final EngineWorldService.PositionType inputPositionType : EngineWorldService.PositionType.values()) {
            for (int inputTypePos = 0; inputTypePos < engineWorldService.typeCount; inputTypePos++) {
                for (int inputEnergyPos = 0; inputEnergyPos < engineWorldService.energyCount; inputEnergyPos++) {
                    for (int inputImpulsePos = 0; inputImpulsePos < engineWorldService.impulseCount; inputImpulsePos++) {

                        final OutputStateList[][][][] outputStateListArr = new OutputStateList
                                [EngineWorldService.PositionType.values().length]
                                [engineWorldService.typeCount]
                                [engineWorldService.energyCount]
                                [engineWorldService.impulseCount];

                        for (final EngineWorldService.PositionType outputPositionType : EngineWorldService.PositionType.values()) {
                            for (int outputTypePos = 0; outputTypePos < engineWorldService.typeCount; outputTypePos++) {
                                for (int outputEnergyPos = 0; outputEnergyPos < engineWorldService.energyCount; outputEnergyPos++) {
                                    for (int outputImpulsePos = 0; outputImpulsePos < engineWorldService.impulseCount; outputImpulsePos++) {
                                        final MatrixRuleEngine.OutputState outputState =
                                                new MatrixRuleEngine.OutputState(outputPositionType, outputTypePos, outputEnergyPos, outputImpulsePos);

                                        final OutputStateList outputStateList = new OutputStateList();
                                        outputStateList.outputStateList = new ArrayList<>();

                                        outputStateListArr
                                                [outputPositionType.ordinal()]
                                                [outputTypePos]
                                                [outputEnergyPos]
                                                [outputImpulsePos] = outputStateList;

                                        outputStateList.outputStateList.add(outputState);
                                    }
                                }
                            }
                        }

                        final RuleEngine ruleEngine = new MatrixRuleEngine(
                                inputPositionType, inputTypePos, inputEnergyPos, inputImpulsePos,
                                outputStateListArr);

                        engineWorldService.addRuleEngine(ruleEngine);
                    }
                }
            }
        }
    }

    public static int[][][][] createStateCountMatrix(final EngineWorldService engineWorldService,
                                                     final EngineWorldService.PositionType positionType, final EwState positionEwState) {
        final int[][][][] stateCountMatrixArr = new int
                [EngineWorldService.PositionType.values().length]
                [engineWorldService.typeCount]
                [engineWorldService.energyCount]
                [engineWorldService.impulseCount];

        for (int inputTypePos = 0; inputTypePos < engineWorldService.typeCount; inputTypePos++) {
            final EwState inputTypeEwState = positionEwState.ewStateArr[inputTypePos];
            for (int inputEnergyPos = 0; inputEnergyPos < engineWorldService.energyCount; inputEnergyPos++) {
                final EwState inputEnergyEwState = inputTypeEwState.ewStateArr[inputEnergyPos];
                for (int inputImpulsePos = 0; inputImpulsePos < engineWorldService.impulseCount; inputImpulsePos++) {
                    final EwState inputImpulseEwState = inputEnergyEwState.ewStateArr[inputImpulsePos];

                    stateCountMatrixArr[positionType.ordinal()][inputTypePos][inputEnergyPos][inputImpulsePos] = inputImpulseEwState.count;
                }
            }
        }
        //for (final EngineWorldService.PositionType inputPositionType : EngineWorldService.PositionType.values()) {
        //    final EwState inputPositionEwState = positionEwState.ewStateArr[inputPositionType.ordinal()];
        //    for (int inputTypePos = 0; inputTypePos < engineWorldService.typeCount; inputTypePos++) {
        //        final EwState inputTypeEwState = inputPositionEwState.ewStateArr[inputTypePos];
        //        for (int inputEnergyPos = 0; inputEnergyPos < engineWorldService.energyCount; inputEnergyPos++) {
        //            final EwState inputEnergyEwState = inputTypeEwState.ewStateArr[inputEnergyPos];
        //            for (int inputImpulsePos = 0; inputImpulsePos < engineWorldService.impulseCount; inputImpulsePos++) {
        //                final EwState inputImpulseEwState = inputEnergyEwState.ewStateArr[inputImpulsePos];
        //
        //                stateCountMatrixArr[inputPositionType.ordinal()][inputTypePos][inputEnergyPos][inputImpulsePos] = inputImpulseEwState.count;
        //            }
        //        }
        //    }
        //}
        return stateCountMatrixArr;
    }

    public static OutputStateList searchMatchingOutputStateList(final OutputStateList[][][][] outputStateListArr, final int[][][][] stateCountMatrixArr) {
        OutputStateList retOutputStateList = null;

        final List<Integer> positionTypeList = new ArrayList<>();
        final List<Integer> typeList = new ArrayList<>();
        final List<Integer> energyList = new ArrayList<>();
        final List<Integer> impulseList = new ArrayList<>();

        for (final EngineWorldService.PositionType outputPositionType : EngineWorldService.PositionType.values()) {
            for (int outputTypePos = 0; outputTypePos < stateCountMatrixArr[outputPositionType.ordinal()].length; outputTypePos++) {
                for (int outputEnergyPos = 0; outputEnergyPos < stateCountMatrixArr[outputPositionType.ordinal()][outputTypePos].length; outputEnergyPos++) {
                    for (int outputImpulsePos = 0; outputImpulsePos < stateCountMatrixArr[outputPositionType.ordinal()][outputTypePos][outputEnergyPos].length; outputImpulsePos++) {
                        final int count =
                                stateCountMatrixArr
                                [outputPositionType.ordinal()]
                                [outputTypePos]
                                [outputEnergyPos]
                                [outputImpulsePos];

                        if (count > 0) {
                            positionTypeList.add(outputPositionType.ordinal());
                            typeList.add(outputTypePos);
                            energyList.add(outputEnergyPos);
                            impulseList.add(outputImpulsePos);
                        }
                    }
                }
            }
        }

        boolean foundFirst = true;

        for (final Integer outputPositionTypePos : positionTypeList) {
            for (final Integer outputTypePos : typeList) {
                for (final Integer outputEnergyPos : energyList) {
                    for (final Integer outputImpulsePos : impulseList) {

                        final OutputStateList outputStateList =
                                outputStateListArr
                                        [outputPositionTypePos]
                                        [outputTypePos]
                                        [outputEnergyPos]
                                        [outputImpulsePos];

                        if (Objects.isNull(retOutputStateList)) {
                            retOutputStateList = outputStateList;
                        } else {
                            if (foundFirst) {
                                foundFirst = false;
                                final OutputStateList newOutputStateList = new OutputStateList();
                                newOutputStateList.outputStateList = new ArrayList<>();
                                newOutputStateList.outputStateList.addAll(retOutputStateList.outputStateList);
                                retOutputStateList = newOutputStateList;
                            }

                            retOutputStateList.outputStateList.addAll(outputStateList.outputStateList);
                        }
                    }
                }
            }
        }

        return retOutputStateList;
    }
}
