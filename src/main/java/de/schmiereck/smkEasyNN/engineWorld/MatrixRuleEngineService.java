package de.schmiereck.smkEasyNN.engineWorld;

import java.util.ArrayList;
import java.util.List;

public class MatrixRuleEngineService {

    final static boolean UseStateCountvalues = false;

    static EngineWorldService initMatrixWorld() {
        //final var engineWorldService  = new EngineWorldService(3 * 3 * 3,
        final var engineWorldService  = new EngineWorldService(3 * 3 * 3 * 3*3*3,
                4, 3, 3);
                //3, 2, 3);

        engineWorldService.
                locationEwStateArr[1].  // location (0:R, 1:G, 2:B, 3:R, 4:G, 5:B, 6:R, 7:G, 8:B)
                ewStateArr[0].          // 1 type
                ewStateArr[1].          // 2 energy
                ewStateArr[1].          // 1 impulse
                count = engineWorldService.calcStateCountPercentFromMax(100); // 100%

        engineWorldService.
                locationEwStateArr[3].  // location (0:R, 1:G, 2:B, 3:R, 4:G, 5:B, 6:R, 7:G, 8:B)
                ewStateArr[1].          // 1 type
                ewStateArr[1].          // 2 energy
                ewStateArr[1].          // 1 impulse
                count = engineWorldService.calcStateCountPercentFromMax(100); // 100%

        engineWorldService.
                locationEwStateArr[5].  // location (0:R, 1:G, 2:B, 3:R, 4:G, 5:B, 6:R, 7:G, 8:B)
                ewStateArr[0].          // 1 type
                ewStateArr[1].          // 2 energy
                ewStateArr[0].          // 1 impulse
                count = engineWorldService.calcStateCountPercentFromMax(100); // 100%

        engineWorldService.
                locationEwStateArr[7].  // location (0:R, 1:G, 2:B, 3:R, 4:G, 5:B, 6:R, 7:G, 8:B)
                ewStateArr[2].          // 1 type
                ewStateArr[1].          // 2 energy
                ewStateArr[2].          // 1 impulse
                count = engineWorldService.calcStateCountPercentFromMax(100); // 100%

        //------------------------------------------
        engineWorldService.
                locationEwStateArr[16].  // location (0:R, 1:G, 2:B, 3:R, 4:G, 5:B, 6:R, 7:G, 8:B)
                ewStateArr[2].          // 1 type
                ewStateArr[0].          // 2 energy
                ewStateArr[0].          // 1 impulse
                count = engineWorldService.calcStateCountPercentFromMax(100); // 100%

        engineWorldService.
                locationEwStateArr[18].  // location (0:R, 1:G, 2:B, 3:R, 4:G, 5:B, 6:R, 7:G, 8:B)
                ewStateArr[2].          // 1 type
                ewStateArr[1].          // 2 energy
                ewStateArr[0].          // 1 impulse
                count = engineWorldService.calcStateCountPercentFromMax(100); // 100%

        engineWorldService.
                locationEwStateArr[20].  // location (0:R, 1:G, 2:B, 3:R, 4:G, 5:B, 6:R, 7:G, 8:B)
                ewStateArr[2].          // 1 type
                ewStateArr[0].          // 2 energy
                ewStateArr[1].          // 1 impulse
                count = engineWorldService.calcStateCountPercentFromMax(100); // 100%

        engineWorldService.
                locationEwStateArr[22].  // location (0:R, 1:G, 2:B, 3:R, 4:G, 5:B, 6:R, 7:G, 8:B)
                ewStateArr[2].          // 1 type
                ewStateArr[1].          // 2 energy
                ewStateArr[1].          // 1 impulse
                count = engineWorldService.calcStateCountPercentFromMax(100); // 100%

        engineWorldService.
                locationEwStateArr[24].  // location (0:R, 1:G, 2:B, 3:R, 4:G, 5:B, 6:R, 7:G, 8:B)
                ewStateArr[2].          // 1 type
                ewStateArr[1].          // 2 energy
                ewStateArr[2].          // 1 impulse
                count = engineWorldService.calcStateCountPercentFromMax(100); // 100%

        engineWorldService.
                locationEwStateArr[26].  // location (0:R, 1:G, 2:B, 3:R, 4:G, 5:B, 6:R, 7:G, 8:B)
                ewStateArr[2].          // 1 type
                ewStateArr[1].          // 2 energy
                ewStateArr[0].          // 1 impulse
                count = engineWorldService.calcStateCountPercentFromMax(100); // 100%

        engineWorldService.
                locationEwStateArr[42].  // location (0:R, 1:G, 2:B, 3:R, 4:G, 5:B, 6:R, 7:G, 8:B)
                ewStateArr[2].          // 1 type
                ewStateArr[1].          // 2 energy
                ewStateArr[1].          // 1 impulse
                count = engineWorldService.calcStateCountPercentFromMax(100); // 100%

        engineWorldService.
                locationEwStateArr[52].  // location (0:R, 1:G, 2:B, 3:R, 4:G, 5:B, 6:R, 7:G, 8:B)
                ewStateArr[2].          // 1 type
                ewStateArr[1].          // 2 energy
                ewStateArr[2].          // 1 impulse
                count = engineWorldService.calcStateCountPercentFromMax(100); // 100%

        engineWorldService.
                locationEwStateArr[62].  // location (0:R, 1:G, 2:B, 3:R, 4:G, 5:B, 6:R, 7:G, 8:B)
                ewStateArr[2].          // 1 type
                ewStateArr[0].          // 2 energy
                ewStateArr[2].          // 1 impulse
                count = engineWorldService.calcStateCountPercentFromMax(100); // 100%

        return engineWorldService;
    }

    static void addMatrixRuleEngines(final EngineWorldService engineWorldService) {
       for (final EngineWorldService.PositionType inputPositionType : EngineWorldService.PositionType.values()) {
            for (int inputTypePos = 0; inputTypePos < engineWorldService.typeCount; inputTypePos++) {
                for (int inputEnergyPos = 0; inputEnergyPos < engineWorldService.energyCount; inputEnergyPos++) {
                    for (int inputImpulsePos = 0; inputImpulsePos < engineWorldService.impulseCount; inputImpulsePos++) {

                        final List<MatrixOutputState> outputStateList = createOutputStateListArr(engineWorldService);

                        final RuleEngine ruleEngine = new MatrixRuleEngine(
                                inputPositionType, inputTypePos, inputEnergyPos, inputImpulsePos,
                                outputStateList);

                        engineWorldService.addRuleEngine(ruleEngine);
                    }
                }
            }
        }
    }

    private static List<MatrixOutputState> createOutputStateListArr(EngineWorldService engineWorldService) {
        final List<MatrixOutputState> outputStateList =new ArrayList<>();
        //outputStateList.outputStateList = new LinkedList<>();

        // For every possible Input-States create all possible Output-States.

        for (final EngineWorldService.PositionType outputPositionType : EngineWorldService.PositionType.values()) {
            for (int outputTypePos = 0; outputTypePos < engineWorldService.typeCount; outputTypePos++) {
                for (int outputEnergyPos = 0; outputEnergyPos < engineWorldService.energyCount; outputEnergyPos++) {
                    for (int outputImpulsePos = 0; outputImpulsePos < engineWorldService.impulseCount; outputImpulsePos++) {

                        // Every possible Matrix Operation.

                        for (int matrixValue = 0; matrixValue < 3; matrixValue++) {
                            for (MatrixOperation.Op matrixOp : MatrixOperation.Op.values()) {
                                for (MatrixOperation.Op matrixResultOp : MatrixOperation.Op.values()) {

                                    final MatrixOperation[][][][] matrixOperationArr = new MatrixOperation
                                            [EngineWorldService.PositionType.values().length]
                                            [engineWorldService.typeCount]
                                            [engineWorldService.energyCount]
                                            [engineWorldService.impulseCount];

                                    for (final EngineWorldService.PositionType inputPositionType : EngineWorldService.PositionType.values()) {
                                        for (int inputTypePos = 0; inputTypePos < engineWorldService.typeCount; inputTypePos++) {
                                            for (int inputEnergyPos = 0; inputEnergyPos < engineWorldService.energyCount; inputEnergyPos++) {
                                                for (int inputImpulsePos = 0; inputImpulsePos < engineWorldService.impulseCount; inputImpulsePos++) {

                                                    final MatrixOperation matrixOperation = new MatrixOperation();

                                                    matrixOperation.op = matrixOp;
                                                    matrixOperation.resultOp = matrixResultOp;
                                                    matrixOperation.value = matrixValue;

                                                    matrixOperationArr[inputPositionType.ordinal()][inputTypePos][inputEnergyPos][inputImpulsePos] =
                                                            matrixOperation;
                                                }
                                            }
                                        }
                                    }

                                    final MatrixOutputState outputState =
                                            new MatrixOutputState(outputPositionType, outputTypePos, outputEnergyPos, outputImpulsePos,
                                                    matrixOperationArr);

                                    outputStateList.add(outputState);
                                }
                            }
                        }

                    }
                }
            }
        }
        return outputStateList;
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
        //                stateCountMatrixArr[inputPositionType.ordinal()][inputTypePos][inputEnergyPos][inputImpulsePos] = inputImpulseEwState.countSum;
        //            }
        //        }
        //    }
        //}
        return stateCountMatrixArr;
    }

    /*
    static void removeOutputState(final EngineWorldService engineWorldService,
                                  final MatrixRuleEngine matrixRuleEngine, final MatrixRuleEngine.OutputState outputState,
                                  final boolean readd) {
        returnAll:
        for (final EngineWorldService.PositionType inputPositionType : EngineWorldService.PositionType.values()) {
            for (int inputTypePos = 0; inputTypePos < engineWorldService.typeCount; inputTypePos++) {
                for (int inputEnergyPos = 0; inputEnergyPos < engineWorldService.energyCount; inputEnergyPos++) {
                    for (int inputImpulsePos = 0; inputImpulsePos < engineWorldService.impulseCount; inputImpulsePos++) {
                        final OutputStateList outputStateList =
                                matrixRuleEngine.outputStateListArr
                                        [inputPositionType.ordinal()]
                                        [inputTypePos]
                                        [inputEnergyPos]
                                        [inputImpulsePos];

                        if (!readd) {
                            if (outputStateList.outputStateList.remove(outputState)) {
                                System.out.printf("MatrixRuleEngine(%s %d %d %d): removeOutputState: %s %d %d %d (from %d)\n",
                                        matrixRuleEngine.inputPositionType, matrixRuleEngine.inputTypePos, matrixRuleEngine.inputEnergyPos, matrixRuleEngine.inputImpulsePos,
                                        inputPositionType, inputTypePos, inputEnergyPos, inputImpulsePos, outputStateList.outputStateList.size());
                                break returnAll;
                            }
                        } else {
                            outputStateList.outputStateList.remove(outputState);
                            outputStateList.outputStateList.add(outputState);
                        }

                        //outputStateList.outputStateList.removeIf(outputState1 ->
                        //        outputState1.positionType() == outputState.positionType() &&
                        //        outputState1.typePos() == outputState.typePos() &&
                        //        outputState1.energyPos() == outputState.energyPos() &&
                        //        outputState1.impulsePos() == outputState.impulsePos());
                    }
                }
            }
        }
    }
    */
    static int calcCountResult(MatrixOutputState outputState, int calcedCount) {
        if (UseStateCountvalues) {
            return (outputState.typePos + 1) *
                    (outputState.energyPos + 1) *
                    (outputState.impulsePos + 1) *
                    calcedCount;
        } else {
            return calcedCount;
        }
    }

    static int calcCountResult(RuleEngine.RuleState positionRuleState) {
        return calcCountResult(positionRuleState, 0);
    }

    static int calcCountResult(RuleEngine.RuleState positionRuleState, int calcedCount) {
        if (UseStateCountvalues) {
            return (positionRuleState.typePos() + 1) *
                    (positionRuleState.energyPos() + 1) *
                    (positionRuleState.impulsePos() + 1) *
                    (positionRuleState.count() - calcedCount);
        } else {
            return positionRuleState.count() - calcedCount;
        }
    }
}
