package de.schmiereck.smkEasyNN.genNet;

import de.schmiereck.smkEasyNN.mlp.MlpConfiguration;
import de.schmiereck.smkEasyNN.mlp.MlpNet;
import de.schmiereck.smkEasyNN.mlp.MlpNetService;
import org.junit.jupiter.api.Test;

import java.util.Random;

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
        //final int[] layerSizeArr = new int[]{ 7, 7, 7, 3, 7, 7, 7 };
        final int[] layerSizeArr = new int[]{ 7, 7, 7, 3, 7, 7, 7 };

        //final Random rnd = new Random(123456);
        final Random rnd = new Random();

        //final GenNet genNet = GenNetService.createNet(layerSizeArr, rnd);

        //final int bottleneckLayerPos = 3;
        //mlpNet.getLayer(bottleneckLayerPos).setIsOutputLayer(true);

        // Act & Assert
        // better Results: lower mutationRate (/10), bigger population size (*10), bigger epoche size (*10)
        final float minMutationRate = 0.015F;
        final float maxMutationRate = 0.2F;
        final int populationSize = 3_200;
        final int epocheSize = 16_000;
        final float copyPercent = 0.0025F;
        final int epochMax = 8000;

        final GenNet trainedGenNet = GenNetTrainService.runTrainNet(layerSizeArr, minMutationRate, maxMutationRate, populationSize, epocheSize, copyPercent, expectedOutputArrArr, trainInputArrArr, rnd);

        System.out.println();
        System.out.println("samplesOutput:");
        //actAssertExpectedOutput(mlpNet, inputArrArr, unexpectedOutputArrArr, 0.6F);
        //GenNetPrintUtils.printSamplesOutput(mlpNet, trainInputArrArr, expectedOutputArrArr, bottleneckLayerPos);
        GenNetPrintUtils.printFullResultForEpoch(trainedGenNet, trainInputArrArr, expectedOutputArrArr, 1, 2);
        GenNetTestUtils.actAssertExpectedOutput(trainedGenNet, trainInputArrArr, expectedOutputArrArr, 0.05F);
    }
}
