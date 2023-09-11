package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpNetPrintUtils.printResultForEpoch;
import static de.schmiereck.smkEasyNN.mlp.MlpNetPrintUtils.printSamplesOutput;
import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.actAssertExpectedOutput;
import static de.schmiereck.smkEasyNN.mlp.MlpService.runTrainRandom;

import java.util.Random;

import org.junit.jupiter.api.Test;

public class MlpNetValueToValueBottleneckTest {

    @Test
    void GIVEN_7_value_inputs_THEN_value_output_after_bottleneck() {
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
                        new float[]{ 0, 0, 0, 0, 0, 0, 0 },
                        new float[]{ 0, 0, 0, 0, 0, 0, 1 },
                        new float[]{ 0, 0, 0, 0, 0, 1, 0 },
                        new float[]{ 0, 0, 0, 0, 1, 0, 0 },

                        new float[]{ 0, 0, 0, 1, 0, 0, 0 },
                        new float[]{ 0, 0, 1, 0, 0, 0, 0 },
                        new float[]{ 0, 1, 0, 0, 0, 0, 0 },
                        new float[]{ 1, 0, 0, 0, 0, 0, 0 },
                };
        final int[] layerSizeArr = new int[]{ 7, 7, 7, 3, 7, 7, 7 };

        final Random rnd = new Random(123456);
        //final Random rnd = new Random();

        final MlpNet mlpNet = new MlpNet(layerSizeArr, true, rnd);

        final int bottleneckLayerPos = 3;
        mlpNet.getLayer(bottleneckLayerPos).setOutputLayer(true);

        //final int epochMax = 2200;
        final int epochMax = 8000;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            runTrainRandom(mlpNet, expectedOutputArrArr, trainInputArrArr, rnd);

            if ((epochPos + 1) % 100 == 0) {
                printResultForEpoch(mlpNet, trainInputArrArr, expectedOutputArrArr, epochPos);
            }
        }

        // Act & Assert
        actAssertExpectedOutput(mlpNet, trainInputArrArr, expectedOutputArrArr, 0.05F);

        System.out.println();
        System.out.println("samplesOutput:");
        //actAssertExpectedOutput(mlpNet, inputArrArr, unexpectedOutputArrArr, 0.6F);
        printSamplesOutput(mlpNet, trainInputArrArr, expectedOutputArrArr, bottleneckLayerPos);
    }
}
