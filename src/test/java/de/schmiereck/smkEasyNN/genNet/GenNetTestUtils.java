package de.schmiereck.smkEasyNN.genNet;

import org.junit.jupiter.api.Assertions;

import java.util.Random;

import static de.schmiereck.smkEasyNN.mlp.MlpNetPrintUtils.*;
import static de.schmiereck.smkEasyNN.mlp.MlpNetService.resetNetOutputs;

public class GenNetTestUtils {

//    public static void runTrainWithGrowingTrainSize(final GenNet net,
//                                                    final float[][][] expectedOutputArrArrArr, final float[][][] trainInputArrArrArr,
//                                                    final int epochMax, final int successfulCounterMax, final boolean printFullResult,
//                                                    final float learningRate, final float momentum,
//                                                    final Random rnd) {
//        int successfulCounter = 0;
//        int expectedOutputTrainSize = 1;
//        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {
//
//            final float mainOutputMseErrorValue = GenNetTrainService.runTrainRandomOrder(net, expectedOutputArrArrArr, trainInputArrArrArr,
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

    static void actAssertExpectedOutput(final GenNet genNet, final float[][][] inputArrArrArr, final float[][][] expectedOutputArrArrArr, final float delta) {
        actAssertExpectedOutput(genNet, inputArrArrArr, expectedOutputArrArrArr, delta, false);
    }

    static void actAssertExpectedOutput(final GenNet genNet,
                                        final float[][][] inputArrArrArr, final float[][][] expectedOutputArrArrArr,
                                        final float delta, final boolean withResetOutputs) {
        int offLinePos = 0;
        for (int pos = 0; pos < inputArrArrArr.length; pos++) {
            actAssertExpectedOutput(genNet, offLinePos, inputArrArrArr[pos], expectedOutputArrArrArr[pos], delta, withResetOutputs);
            offLinePos += inputArrArrArr[pos].length;
        }
    }

    static void actAssertExpectedOutput(final GenNet genNet, final float[][] inputArrArr, final float[][] expectedOutputArrArr, final float delta) {
        actAssertExpectedOutput(genNet, 0, inputArrArr, expectedOutputArrArr, delta);
    }

    static void actAssertExpectedOutput(final GenNet genNet, final int outputPos, final float[][] inputArrArr, final float[][] expectedOutputArrArr, final float delta) {
        actAssertExpectedOutput(genNet, outputPos, inputArrArr, expectedOutputArrArr, delta, false);
    }

    static void actAssertExpectedOutput(final GenNet genNet, final int outputLinePos, final float[][] inputArrArr, final float[][] expectedOutputArrArr,
                                        final float delta, final boolean withResetOutputs) {
        if (withResetOutputs) {
            GenNetService.resetNetOutputs(genNet);
        }
        for (int resultPos = 0; resultPos < expectedOutputArrArr.length; resultPos++) {
            final float[] expectedOutputArr = expectedOutputArrArr[resultPos];
            final float[] inputArr = inputArrArr[resultPos];

            actAssertExpectedOutput(genNet, outputLinePos + resultPos, delta, inputArr, expectedOutputArr);
        }
    }

    private static void actAssertExpectedOutput(final GenNet genNet, final int outputLinePos, final float delta, final float[] inputArr, final float[] expectedOutputArr) {
        final float[] outputArr = GenNetService.run(genNet, inputArr);

        assertExpectedOutput("expectedOutput line %d: ".formatted(outputLinePos), delta, inputArr, expectedOutputArr, outputArr);
    }

    public static void assertExpectedOutput(final String infoStr, final float delta,
                                            final float[] inputArr, final float[] expectedOutputArr, final float[] outputArr) {
        for (int expectedOutputPos = 0; expectedOutputPos < expectedOutputArr.length; expectedOutputPos++) {
            Assertions.assertEquals(expectedOutputArr[expectedOutputPos], outputArr[expectedOutputPos], delta,
                    "%s expectedOutputPos %d\n%s".formatted(infoStr, expectedOutputPos,
                            formatResultLine(inputArr, outputArr, expectedOutputArr)));
        }
    }

}
