package de.schmiereck.smkEasyNN.geniNet;

import org.junit.jupiter.api.Assertions;

import static de.schmiereck.smkEasyNN.geniNet.GeniNetPrintUtils.formatResultLine;

public class GeniNetTestUtils {

//    public static void runTrainWithGrowingTrainSize(final GeniNet net,
//                                                    final int[][][] expectedOutputArrArrArr, final int[][][] trainInputArrArrArr,
//                                                    final int epochMax, final int successfulCounterMax, final boolean printFullResult,
//                                                    final float learningRate, final float momentum,
//                                                    final Random rnd) {
//        int successfulCounter = 0;
//        int expectedOutputTrainSize = 1;
//        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {
//
//            final int mainOutputMseErrorValue = GeniNetTrainService.runTrainRandomOrder(net, expectedOutputArrArrArr, trainInputArrArrArr,
//                    expectedOutputTrainSize, learningRate, momentum, rnd);
//
//            if ((epochPos + 1) % 100 == 0) {
//                if (printFullResult) {
//                    printFullResultForEpochWithTrainSize(net, trainInputArrArrArr, expectedOutputArrArrArr, epochPos, mainOutputMseErrorValue, expectedOutputTrainSize);
//                } else {
//                    printResultForEpochWithTrainSize(epochPos, mainOutputMseErrorValue, expectedOutputTrainSize);
//                }
//            }
//            if (mainOutputMseErrorValue < 0.001F) {
//                successfulCounter++;
//                if (successfulCounter > successfulCounterMax) {
//                    if (expectedOutputTrainSize < 3) {
//                        expectedOutputTrainSize++;
//                    } else {
//                        printFullResultForEpochWithTrainSize(net, trainInputArrArrArr, expectedOutputArrArrArr, epochPos, mainOutputMseErrorValue, expectedOutputTrainSize);
//                        break;
//                    }
//                    successfulCounter = 0;
//                }
//            } else {
//                successfulCounter = 0;
//            }
//        }
//    }

    static void actAssertExpectedOutput(final de.schmiereck.smkEasyNN.geniNet.GeniNet geniNet, final int[][][] inputArrArrArr, final int[][][] expectedOutputArrArrArr, final int delta) {
        actAssertExpectedOutput(geniNet, inputArrArrArr, expectedOutputArrArrArr, delta, false);
    }

    static void actAssertExpectedOutput(final de.schmiereck.smkEasyNN.geniNet.GeniNet geniNet,
                                        final int[][][] inputArrArrArr, final int[][][] expectedOutputArrArrArr,
                                        final int delta, final boolean withResetOutputs) {
        int offLinePos = 0;
        for (int pos = 0; pos < inputArrArrArr.length; pos++) {
            actAssertExpectedOutput(geniNet, offLinePos, inputArrArrArr[pos], expectedOutputArrArrArr[pos], delta, withResetOutputs);
            offLinePos += inputArrArrArr[pos].length;
        }
    }

    static void actAssertExpectedOutput(final de.schmiereck.smkEasyNN.geniNet.GeniNet geniNet, final int[][] inputArrArr, final int[][] expectedOutputArrArr, final int delta) {
        actAssertExpectedOutput(geniNet, 0, inputArrArr, expectedOutputArrArr, delta);
    }

    static void actAssertExpectedOutput(final de.schmiereck.smkEasyNN.geniNet.GeniNet geniNet, final int outputPos, final int[][] inputArrArr, final int[][] expectedOutputArrArr, final int delta) {
        actAssertExpectedOutput(geniNet, outputPos, inputArrArr, expectedOutputArrArr, delta, false);
    }

    static void actAssertExpectedOutput(final de.schmiereck.smkEasyNN.geniNet.GeniNet geniNet, final int outputLinePos, final int[][] inputArrArr, final int[][] expectedOutputArrArr,
                                        final int delta, final boolean withResetOutputs) {
        if (withResetOutputs) {
            GeniNetService.resetNetOutputs(geniNet);
        }
        for (int resultPos = 0; resultPos < expectedOutputArrArr.length; resultPos++) {
            final int[] expectedOutputArr = expectedOutputArrArr[resultPos];
            final int[] inputArr = inputArrArr[resultPos];

            actAssertExpectedOutput(geniNet, outputLinePos + resultPos, delta, inputArr, expectedOutputArr);
        }
    }

    private static void actAssertExpectedOutput(final GeniNet geniNet, final int outputLinePos, final int delta, final int[] inputArr, final int[] expectedOutputArr) {
        final int[] outputArr = GeniNetService.run(geniNet, inputArr);

        assertExpectedOutput("expectedOutput line %d: ".formatted(outputLinePos), delta, inputArr, expectedOutputArr, outputArr);
    }

    public static void assertExpectedOutput(final String infoStr, final int delta,
                                            final int[] inputArr, final int[] expectedOutputArr, final int[] outputArr) {
        for (int expectedOutputPos = 0; expectedOutputPos < expectedOutputArr.length; expectedOutputPos++) {
            Assertions.assertEquals(expectedOutputArr[expectedOutputPos], outputArr[expectedOutputPos], delta,
                    "%s expectedOutputPos %d\n%s".formatted(infoStr, expectedOutputPos,
                            formatResultLine(inputArr, outputArr, expectedOutputArr)));
        }
    }

    public static int[][] calcToMaxValue(int[][] arrArr) {
        final int[][] toMaxValueArrArr = new int[arrArr.length][];
        for (int arrPos = 0; arrPos < arrArr.length; arrPos++) {
            final int[] toMaxValueArr = new int[arrArr[arrPos].length];
            for (int pos = 0; pos < arrArr[arrPos].length; pos++) {
                //toMaxValueArr[pos] = arrArr[arrPos][pos] == 1 ? GeniNetService.VALUE_MAX2 : 0;
                toMaxValueArr[pos] = arrArr[arrPos][pos] * GeniNetService.VALUE_MAX2;
            }
            toMaxValueArrArr[arrPos] = toMaxValueArr;
        }
        return toMaxValueArrArr;
    }


}
