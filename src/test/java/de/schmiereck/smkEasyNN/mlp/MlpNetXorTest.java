package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpNetPrintUtils.printFullResultForEpoch;
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

        final MlpNet mlpNet = MlpNetService.createNet(layerSizeArr, true, rnd);

        final int epochMax = 500;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            final float mainOutputMseErrorValue = runTrainRandom(mlpNet, expectedOutputArrArr, trainInputArrArr, rnd);

            if ((epochPos + 1) % 100 == 0) {
                printFullResultForEpoch(mlpNet, trainInputArrArr, expectedOutputArrArr, epochPos, mainOutputMseErrorValue);
            }
        }
        final MlpNet mlpNet2 = MlpNetService.duplicateNet(mlpNet);

        // Act & Assert
        System.out.println("Act & Assert 1");
        printFullResultForEpoch(mlpNet, trainInputArrArr, expectedOutputArrArr, 1, 2);
        actAssertExpectedOutput(mlpNet, trainInputArrArr, expectedOutputArrArr, 0.05F);

        System.out.println("Act & Assert 2");
        printFullResultForEpoch(mlpNet2, trainInputArrArr, expectedOutputArrArr, 2, 2);
        actAssertExpectedOutput(mlpNet2, trainInputArrArr, expectedOutputArrArr, 0.05F);
    }
}
