package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpNetPrintUtils.formatResultLine;

import de.schmiereck.smkEasyNN.mlp.MlpNet;
import de.schmiereck.smkEasyNN.mlp.MlpService;

import java.util.Arrays;
import java.util.Formatter;

import org.junit.jupiter.api.Assertions;

public class MlpNetTestUtils {

    static void actAssertExpectedOutput(final MlpNet mlpNet, final float[][][] inputArrArrArr, final float[][][] expectedOutputArrArrArr, final float delta) {
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
