package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpNetPrintUtils.printResultForEpoch;
import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.actAssertExpectedOutput;
import static de.schmiereck.smkEasyNN.mlp.MlpService.runTrainRandom;

import java.util.Random;

import org.junit.jupiter.api.Test;

public class MlpNetXorTest {

    @Test
    void GIVEN_2_binary_inputs_THEN_XOR_output() {
        // Arrange
        final float[][] trainInputArrArr = new float[][]
                {
                        new float[]{0, 0},
                        new float[]{0, 1},
                        new float[]{1, 0},
                        new float[]{1, 1}
                };
        final float[][] expectedOutputArrArr = new float[][]
                {
                        new float[]{0},
                        new float[]{1},
                        new float[]{1},
                        new float[]{0}
                };
        final int[] layerSizeArr = new int[]{ 2, 1 };

        final Random rnd = new Random(12345);
        //final Random rnd = new Random();

        final MlpNet mlpNet = new MlpNet(layerSizeArr, true, rnd);

        final int epochMax = 500;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            runTrainRandom(mlpNet, expectedOutputArrArr, trainInputArrArr, rnd);

            if ((epochPos + 1) % 100 == 0) {
                printResultForEpoch(mlpNet, trainInputArrArr, expectedOutputArrArr, epochPos);
            }
        }

        // Act & Assert
        actAssertExpectedOutput(mlpNet, trainInputArrArr, expectedOutputArrArr, 0.05F);
    }
}
