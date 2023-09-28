package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpNetPrintUtils.formatResultLine;
import static de.schmiereck.smkEasyNN.mlp.MlpNetPrintUtils.printFullResultForEpochWithTrainSize;
import static de.schmiereck.smkEasyNN.mlp.MlpNetPrintUtils.printResultForEpochWithTrainSize;
import static de.schmiereck.smkEasyNN.mlp.MlpNetService.resetNetOutputs;
import static de.schmiereck.smkEasyNN.mlp.MlpService.runTrainRandomOrder;

import java.util.Random;

import org.junit.jupiter.api.Assertions;

public class MlpNetTestUtils {

    public static void runTrainWithGrowingTrainSize(final MlpNet mlpNet,
                                                    final float[][][] expectedOutputArrArrArr, final float[][][] trainInputArrArrArr,
                                                    final int epochMax, final int successfulCounterMax, final boolean printFullResult,
                                                    final float learningRate, final float momentum,
                                                    final Random rnd) {
        int successfulCounter = 0;
        int expectedOutputTrainSize = 1;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            final float mainOutputMseErrorValue = runTrainRandomOrder(mlpNet, expectedOutputArrArrArr, trainInputArrArrArr,
                    expectedOutputTrainSize, learningRate, momentum, rnd);

            if ((epochPos + 1) % 100 == 0) {
                if (printFullResult) {
                    printFullResultForEpochWithTrainSize(mlpNet, trainInputArrArrArr, expectedOutputArrArrArr, epochPos, mainOutputMseErrorValue, expectedOutputTrainSize);
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
                        printFullResultForEpochWithTrainSize(mlpNet, trainInputArrArrArr, expectedOutputArrArrArr, epochPos, mainOutputMseErrorValue, expectedOutputTrainSize);
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
        int offPos = 0;
        for (int pos = 0; pos < inputArrArrArr.length; pos++) {
            actAssertExpectedOutput(mlpNet, offPos, inputArrArrArr[pos], expectedOutputArrArrArr[pos], delta);
            offPos += inputArrArrArr[pos].length;
        }
    }

    static void actAssertExpectedOutput(final MlpNet mlpNet, final float[][] inputArrArr, final float[][] expectedOutputArrArr, final float delta) {
        actAssertExpectedOutput(mlpNet, 0, inputArrArr, expectedOutputArrArr, delta);
    }

    static void actAssertExpectedOutput(final MlpNet mlpNet, final int outputPos, final float[][] inputArrArr, final float[][] expectedOutputArrArr, final float delta) {
        actAssertExpectedOutput(mlpNet, outputPos, inputArrArr, expectedOutputArrArr, delta, false);
    }

    static void actAssertExpectedOutput(final MlpNet mlpNet, final int outputPos, final float[][] inputArrArr, final float[][] expectedOutputArrArr,
                                        final float delta, final boolean withResetOutputs) {
        if (withResetOutputs) {
            resetNetOutputs(mlpNet);
        }
        for (int resultPos = 0; resultPos < expectedOutputArrArr.length; resultPos++) {
            final float[] expectedOutputArr = expectedOutputArrArr[resultPos];
            final float[] inputArr = inputArrArr[resultPos];

            final float[] outputArr = MlpService.run(mlpNet, inputArr);

            for (int expectedOutputPos = 0; expectedOutputPos < expectedOutputArr.length; expectedOutputPos++) {
                Assertions.assertEquals(expectedOutputArr[expectedOutputPos], outputArr[expectedOutputPos], delta,
                        "expectedOutput line %d: expectedOutputPos %d\n%s".formatted(outputPos + resultPos, expectedOutputPos,
                                formatResultLine(inputArr, outputArr, expectedOutputArr)));
            }
        }
    }

}
