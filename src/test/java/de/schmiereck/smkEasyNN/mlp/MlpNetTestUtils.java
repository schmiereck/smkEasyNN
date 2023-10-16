package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpNetPrintUtils.formatResultLine;
import static de.schmiereck.smkEasyNN.mlp.MlpNetPrintUtils.printFullResultForEpochWithTrainSize;
import static de.schmiereck.smkEasyNN.mlp.MlpNetPrintUtils.printResultForEpochWithTrainSize;
import static de.schmiereck.smkEasyNN.mlp.MlpNetService.resetNetOutputs;
import static de.schmiereck.smkEasyNN.mlp.MlpService.runTrainRandomOrder;

import java.util.Random;

import org.junit.jupiter.api.Assertions;

public class MlpNetTestUtils {

    public static void runTrainWithGrowingTrainSize(final MlpNet net,
                                                    final float[][][] expectedOutputArrArrArr, final float[][][] trainInputArrArrArr,
                                                    final int epochMax, final int successfulCounterMax, final boolean printFullResult,
                                                    final float learningRate, final float momentum,
                                                    final Random rnd) {
        int successfulCounter = 0;
        int expectedOutputTrainSize = 1;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            final float mainOutputMseErrorValue = runTrainRandomOrder(net, expectedOutputArrArrArr, trainInputArrArrArr,
                    expectedOutputTrainSize, learningRate, momentum, rnd);

            if ((epochPos + 1) % 100 == 0) {
                if (printFullResult) {
                    printFullResultForEpochWithTrainSize(net, trainInputArrArrArr, expectedOutputArrArrArr, epochPos, mainOutputMseErrorValue, expectedOutputTrainSize);
                } else {
                    printResultForEpochWithTrainSize(epochPos, mainOutputMseErrorValue, expectedOutputTrainSize);
                }
            }
            if (mainOutputMseErrorValue < 0.001F) {
                successfulCounter++;
                if (successfulCounter > successfulCounterMax) {
                    if (expectedOutputTrainSize < 3) {
                        expectedOutputTrainSize++;
                    } else {
                        printFullResultForEpochWithTrainSize(net, trainInputArrArrArr, expectedOutputArrArrArr, epochPos, mainOutputMseErrorValue, expectedOutputTrainSize);
                        break;
                    }
                    successfulCounter = 0;
                }
            } else {
                successfulCounter = 0;
            }
        }
    }

    static void actAssertExpectedOutput(final MlpNet mlpNet, final float[][][] inputArrArrArr, final float[][][] expectedOutputArrArrArr, final float delta) {
        actAssertExpectedOutput(mlpNet, inputArrArrArr, expectedOutputArrArrArr, delta, false);
    }

    static void actAssertExpectedOutput(final MlpNet mlpNet,
                                        final float[][][] inputArrArrArr, final float[][][] expectedOutputArrArrArr,
                                        final float delta, final boolean withResetOutputs) {
        int offLinePos = 0;
        for (int pos = 0; pos < inputArrArrArr.length; pos++) {
            actAssertExpectedOutput(mlpNet, offLinePos, inputArrArrArr[pos], expectedOutputArrArrArr[pos], delta, withResetOutputs);
            offLinePos += inputArrArrArr[pos].length;
        }
    }

    static void actAssertExpectedOutput(final MlpNet mlpNet, final float[][] inputArrArr, final float[][] expectedOutputArrArr, final float delta) {
        actAssertExpectedOutput(mlpNet, 0, inputArrArr, expectedOutputArrArr, delta);
    }

    static void actAssertExpectedOutput(final MlpNet mlpNet, final int outputPos, final float[][] inputArrArr, final float[][] expectedOutputArrArr, final float delta) {
        actAssertExpectedOutput(mlpNet, outputPos, inputArrArr, expectedOutputArrArr, delta, false);
    }

    static void actAssertExpectedOutput(final MlpNet mlpNet, final int outputLinePos, final float[][] inputArrArr, final float[][] expectedOutputArrArr,
                                        final float delta, final boolean withResetOutputs) {
        if (withResetOutputs) {
            resetNetOutputs(mlpNet);
        }
        for (int resultPos = 0; resultPos < expectedOutputArrArr.length; resultPos++) {
            final float[] expectedOutputArr = expectedOutputArrArr[resultPos];
            final float[] inputArr = inputArrArr[resultPos];

            actAssertExpectedOutput(mlpNet, outputLinePos + resultPos, delta, inputArr, expectedOutputArr);
        }
    }

    private static void actAssertExpectedOutput(final MlpNet mlpNet, final int outputLinePos, final float delta, final float[] inputArr, final float[] expectedOutputArr) {
        final float[] outputArr = MlpService.run(mlpNet, inputArr);

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
