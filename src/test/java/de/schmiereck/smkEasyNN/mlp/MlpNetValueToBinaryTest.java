package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpNetPrintUtils.printResult;
import static de.schmiereck.smkEasyNN.mlp.MlpNetPrintUtils.printResultForEpoch;
import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.actAssertExpectedOutput;
import static de.schmiereck.smkEasyNN.mlp.MlpService.runTrainRandom;

import java.util.Random;

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

        final Random rnd = new Random(123456);
        //final Random rnd = new Random();

        final MlpConfiguration mlpConfiguration = new MlpConfiguration(true, false);
        final MlpLayerConfig[] layerConfigArr = new MlpLayerConfig[3];
        layerConfigArr[0] = new MlpLayerConfig(7);
        layerConfigArr[1] = new MlpLayerConfig(7);
        layerConfigArr[2] = new MlpLayerConfig(3);

        final MlpNet mlpNet = MlpNetService.createNet(mlpConfiguration, layerConfigArr, rnd);

        final int epochMax = 300;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            runTrainRandom(mlpNet, expectedOutputArrArr, trainInputArrArr, rnd);

            if ((epochPos + 1) % 100 == 0) {
                printResultForEpoch(mlpNet, trainInputArrArr, expectedOutputArrArr, epochPos);
            }
        }

        // Act & Assert
        actAssertExpectedOutput(mlpNet, trainInputArrArr, expectedOutputArrArr, 0.05F);

        final float[][] inputArrArr = new float[][]
                {
                        new float[]{ 0, 0, 0, 0, 0, 1, 1 }, // 1 + 2 = 3
                        new float[]{ 0, 0, 0, 1, 0, 0, 1 }, // 1 + 4 = 5
                        new float[]{ 0, 0, 0, 1, 0, 1, 0 }, // 2 + 4 = 6
                        new float[]{ 0, 0, 0, 1, 1, 0, 0 }, // 3 + 4 = 7
                };
        final float[][] unexpectedOutputArrArr = new float[][]
                {
                        new float[]{0, 1, 1}, // 3
                        new float[]{1, 0, 1}, // 5
                        new float[]{1, 1, 0}, // 6
                        new float[]{1, 1, 1}, // 7
                };

        actAssertExpectedOutput(mlpNet, inputArrArr, unexpectedOutputArrArr, 0.6F);
        System.out.println();
        System.out.println("unexpectedOutput:");
        printResult(mlpNet, inputArrArr, unexpectedOutputArrArr);
    }
}
