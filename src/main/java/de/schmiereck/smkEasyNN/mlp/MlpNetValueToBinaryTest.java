package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.actAssertExpectedOutput;
import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.printResultForEpoch;
import static de.schmiereck.smkEasyNN.mlp.MlpService.runTrain;

import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MlpNetValueToBinaryTest {

    @Test
    void GIVEN_7_value_inputs_THEN_binary_number_output() {
        // Arrange
        final float[][] trainInputArrArr = new float[][]
                {
                        new float[]{ 0, 0, 0, 0, 0, 0, 0 },
                        new float[]{ 0, 0, 0, 0, 0, 0, 1 },
                        new float[]{ 0, 0, 0, 0, 0, 1, 0 },
                        new float[]{ 0, 0, 0, 0, 1, 0, 0 },

                        new float[]{ 0, 0, 0, 1, 0, 0, 0 },
                        new float[]{ 0, 0, 1, 0, 0, 0, 0 },
                        new float[]{ 0, 1, 0, 0, 0, 0, 0 },
                        new float[]{ 1, 0, 0, 0, 0, 0, 0 },
                };
        final float[][] expectedOutputArrArr = new float[][]
                {
                        new float[]{0, 0, 0},
                        new float[]{0, 0, 1},
                        new float[]{0, 1, 0},
                        new float[]{0, 1, 1},

                        new float[]{1, 0, 0},
                        new float[]{1, 0, 1},
                        new float[]{1, 1, 0},
                        new float[]{1, 1, 1},
                };
        final int[] layerSizeArr = new int[]{ 7, 3 };

        final Random rnd = new Random(123456);
        //final Random rnd = new Random();

        final MlpNet mlpNet = new MlpNet(layerSizeArr, true, rnd);

        final int epochMax = 300;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            runTrain(mlpNet, expectedOutputArrArr, trainInputArrArr, rnd);

            if ((epochPos + 1) % 100 == 0) {
                printResultForEpoch(mlpNet, expectedOutputArrArr, trainInputArrArr, epochPos);
            }
        }

        // Act & Assert
        actAssertExpectedOutput(mlpNet, expectedOutputArrArr, trainInputArrArr);
    }
}
