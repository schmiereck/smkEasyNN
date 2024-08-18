package de.schmiereck.smkEasyNN.engineWorld;

import de.schmiereck.smkEasyNN.engineWorld.GenericRuleEngine.InputStateNode;
import de.schmiereck.smkEasyNN.engineWorld.GenericRuleEngine.OutputStateList;

import java.util.ArrayList;

public class GenericRuleEngineService {

    @FunctionalInterface
    interface CreateOutputStateListInterface {
        OutputStateList createOutputStateList(final InputStateNode inputStateNode);
    }

    static EngineWorldService initGenericWorld() {
        final var engineWorldService  = new EngineWorldService(9, 2, 2, 2);

        engineWorldService.
                locationEwStateArr[4].  // location (0:R, 1:G, 2:B, 3:R, 4:G, 5:B, 6:R, 7:G, 8:B)
                ewStateArr[1].          // 1 type
                ewStateArr[1].          // 2 energy
                ewStateArr[1].          // 1 impulse
                count = engineWorldService.calcStateCountPercentFromMax(60); // 60%

        return engineWorldService;
    }

    static void addGenericRuleEngines(final EngineWorldService engineWorldService) {
        for (final EngineWorldService.PositionType inputPositionType : EngineWorldService.PositionType.values()) {
            for (int inputTypePos = 0; inputTypePos < engineWorldService.typeCount; inputTypePos++) {
                for (int inputEnergyPos = 0; inputEnergyPos < engineWorldService.energyCount; inputEnergyPos++) {
                    for (int inputImpulsePos = 0; inputImpulsePos < engineWorldService.impulseCount; inputImpulsePos++) {


                        //OutputStateList[][][][] outputStateArr =
                        //        new OutputStateList
                        //                [PositionType.values().length]
                        //                [engineWorldService.typeCount]
                        //                [engineWorldService.energyCount]
                        //                [engineWorldService.impulseCount];

                        // Add the Output-States depending on
                        // all the other Inner-States on this Position.

                        final InputStateNode rootInputStateNode = new InputStateNode();
                        final int[] stateCountArr = new int[4];
                        stateCountArr[0] = EngineWorldService.PositionType.values().length;
                        stateCountArr[1] = engineWorldService.typeCount;
                        stateCountArr[2] = engineWorldService.energyCount;
                        stateCountArr[3] = engineWorldService.impulseCount;

                        GenericRuleEngineService.create(rootInputStateNode, stateCountArr, 0, 0,
                                (final InputStateNode inputStateNode) -> {
                                    final OutputStateList outputStateList = new OutputStateList();
                                    outputStateList.outputStateList = new ArrayList<>();

                                    for (final EngineWorldService.PositionType outputPositionType : EngineWorldService.PositionType.values()) {
                                        for (int outputTypePos = 0; outputTypePos < engineWorldService.typeCount; outputTypePos++) {
                                            for (int outputEnergyPos = 0; outputEnergyPos < engineWorldService.energyCount; outputEnergyPos++) {
                                                for (int outputImpulsePos = 0; outputImpulsePos < engineWorldService.impulseCount; outputImpulsePos++) {
                                                    final GenericRuleEngine.OutputState outputState = new GenericRuleEngine.OutputState(outputPositionType, outputTypePos, outputEnergyPos, outputImpulsePos);

                                                    outputStateList.outputStateList.add(outputState);
                                                }
                                            }
                                        }
                                    }
                                    return outputStateList;
                                });

                        GenericRuleEngineService.print(rootInputStateNode, "", 0);

                        //for (final PositionType stateInputPositionType : PositionType.values()) {
                        //    for (int stateInputTypePos = 0; stateInputTypePos < engineWorldService.typeCount; stateInputTypePos++) {
                        //        for (int stateInputEnergyPos = 0; stateInputEnergyPos < engineWorldService.energyCount; stateInputEnergyPos++) {
                        //            for (int stateInputImpulsePos = 0; stateInputImpulsePos < engineWorldService.impulseCount; stateInputImpulsePos++) {
                        //
                        //                outputStateArr[stateInputPositionType.ordinal()][stateInputTypePos][stateInputEnergyPos][stateInputImpulsePos] =
                        //                        outputStateList;
                        //            }
                        //        }
                        //    }
                        //}

                        final RuleEngine ruleEngine = new GenericRuleEngine(
                                inputPositionType, inputTypePos, inputEnergyPos, inputImpulsePos,
                                //outputStateArr,
                                rootInputStateNode);

                        engineWorldService.addRuleEngine(ruleEngine);
                    }
                }
            }
        }
    }

    static void create(final InputStateNode inputStateNode, final int[] stateCountArr, final int stateCountPos, final int nextStatePos,
                       final CreateOutputStateListInterface createOutputStateListInterface) {
        if (stateCountPos < stateCountArr.length) {
            final int stateCount = stateCountArr[stateCountPos];

            if (nextStatePos < stateCount) {
                inputStateNode.nextInputStateNodeArr = new InputStateNode[stateCount];

                for (int statePos = 0; statePos < stateCount; statePos++) {
                    final InputStateNode nextInputStateNode = new InputStateNode();
                    inputStateNode.nextInputStateNodeArr[statePos] = nextInputStateNode;

                    create(nextInputStateNode, stateCountArr, stateCountPos, nextStatePos + 1, createOutputStateListInterface);
                }
            } else {
                final InputStateNode childInputStateNode = new InputStateNode();
                inputStateNode.childInputStateNode = childInputStateNode;
                create(childInputStateNode, stateCountArr, stateCountPos + 1, 0, createOutputStateListInterface);
            }
        } else {
            // End of recursion.
            // Create Output-State.
            inputStateNode.outputStateList = createOutputStateListInterface.createOutputStateList(inputStateNode);
        }
    }

    public static OutputStateList searchOutputStateList(final InputStateNode inputStateNode, final EwState positionEwState) {
        OutputStateList outputStateList = null;
        if (inputStateNode != null) {
            if (inputStateNode.outputStateList != null) {
                outputStateList = inputStateNode.outputStateList;
            } else {
                if (inputStateNode.nextInputStateNodeArr != null) {
                    final int statePos = 0;
                    final InputStateNode nextInputStateNode = inputStateNode.nextInputStateNodeArr[statePos];
                    outputStateList = searchOutputStateList(nextInputStateNode, positionEwState);
                }
            }
        }
        return outputStateList;
    }

    static int print(final InputStateNode inputStateNode, final String prefix, int linePos) {
        if (inputStateNode.nextInputStateNodeArr != null) {
            for (int statePos = 0; statePos < inputStateNode.nextInputStateNodeArr.length; statePos++) {
                final InputStateNode nextInputStateNode = inputStateNode.nextInputStateNodeArr[statePos];
                if (nextInputStateNode != null) {
                    System.out.printf("%8d %sStatePos: %d\n".formatted(linePos, prefix, statePos));
                    linePos = print(nextInputStateNode, prefix + "  ", linePos + 1);
                }
            }
        }
        if (inputStateNode.childInputStateNode != null) {
            //System.out.println(prefix + "Child");
            linePos = print(inputStateNode.childInputStateNode, prefix + "C ", linePos);
        }
        return linePos;
    }
}
