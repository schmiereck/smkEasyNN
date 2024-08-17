package de.schmiereck.smkEasyNN.geniNet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static de.schmiereck.smkEasyNN.geniNet.GeniNetTestUtils.calcToValueMax;
import static de.schmiereck.smkEasyNN.geniNet.GeniNetTestUtils.calcToValueMax2;

public class GeniNetBooleanOperationsTest {

    @Test
    void GIVEN_2_binary_inputs_THEN_OR_output() {
        // Arrange
        GeniNetService.initValueRange(64);
        //GeniNetService.initValueRange(6);
        //GeniNetService.initValueRange(100);

        final int[][] trainInputArrArr = calcToValueMax(new int[][]
                {
                        new int[]{0, 0},
                        new int[]{0, 1},
                        new int[]{1, 0},
                        new int[]{1, 1}
                });
        final int[][] expectedOutputArrArr = calcToValueMax(new int[][]
                {
                        new int[]{0},
                        new int[]{1},
                        new int[]{1},
                        new int[]{1}
                });
        final int[] layerSizeArr = new int[]{ 2, 2, 1 };

        final Random rnd = new Random(12345);
        //final Random rnd = new Random();

        //final MlpConfiguration config = new MlpConfiguration(true, false);
        //final MlpNet net = MlpNetService.createNet(config, layerSizeArr, rnd);
        final GeniNet geniNet = GeniNetService.createNet(layerSizeArr, rnd);
        {
            Assertions.assertEquals(2, geniNet.inputNeuronList.size());
            Assertions.assertEquals(1, geniNet.outputNeuronList.size());
            Assertions.assertEquals(2 + 2 + 1, geniNet.neuronList.size());

            Assertions.assertEquals(2, geniNet.neuronList.get(2).inputSynapseList.size());
            //Assertions.assertTrue(geniNet.neuronList.get(2).inputSynapseList.get(0).weight != 0);
            //Assertions.assertTrue(geniNet.neuronList.get(2).inputSynapseList.get(1).weight != 0);
        }

        // better Results: lower mutationRate (/10), bigger population size (*10), bigger epoche size (*10)
        final float minMutationRate = 0.1F;//0.1F;//0.014F;
        final float maxMutationRate = 0.4F;//0.18F;
        final float copyPercent = 0.011F;//0.011F;
        final int populationSize = 360;
        final int epocheSize = 1_000;
        final int epochMax = 1500;

        final GeniNet trainedGeniNet = GeniNetTrainService.runTrainNet(geniNet,
                minMutationRate, maxMutationRate, populationSize, epocheSize, copyPercent,
                expectedOutputArrArr, trainInputArrArr, rnd);

        // Act & Assert
        System.out.println("Act & Assert 1");
        //printFullResultForEpoch(net, trainInputArrArr, expectedOutputArrArr, 1, 2);
        //actAssertExpectedOutput(net, trainInputArrArr, expectedOutputArrArr, 0.05F);
        GeniNetPrintUtils.printFullResultForEpoch(trainedGeniNet, trainInputArrArr, expectedOutputArrArr, 1, trainedGeniNet.error);
        GeniNetTestUtils.actAssertExpectedOutput(trainedGeniNet, trainInputArrArr, expectedOutputArrArr, GeniNetService.calcPercentValue(0.05F));
    }

    @Test
    void GIVEN_2_binary_inputs_THEN_XOR_output() {
        // Arrange
        //GeniNetService.initValueRange(6);
        GeniNetService.initValueRange(100);

        final int[][] trainInputArrArr = calcToValueMax2(new int[][]
                {
                        new int[]{0, 0},
                        new int[]{0, 1},
                        new int[]{1, 0},
                        new int[]{1, 1}
                });
        final int[][] expectedOutputArrArr = calcToValueMax2(new int[][]
                {
                        new int[]{0},
                        new int[]{1},
                        new int[]{1},
                        new int[]{0}
                });
        final int[] layerSizeArr = new int[]{ 2, 4, 3, 1 };

        final Random rnd = new Random(12345);
        //final Random rnd = new Random();

        //final MlpConfiguration config = new MlpConfiguration(true, false);
        //final MlpNet net = MlpNetService.createNet(config, layerSizeArr, rnd);
        final GeniNet geniNet = GeniNetService.createNet(layerSizeArr, rnd);
        {
            Assertions.assertEquals(2, geniNet.inputNeuronList.size());
            Assertions.assertEquals(1, geniNet.outputNeuronList.size());
            Assertions.assertEquals(2 + 4 + 3 + 1, geniNet.neuronList.size());

            Assertions.assertEquals(2, geniNet.neuronList.get(2).inputSynapseList.size());
            Assertions.assertTrue(geniNet.neuronList.get(2).inputSynapseList.get(0).weight != 0.0F);
            Assertions.assertTrue(geniNet.neuronList.get(2).inputSynapseList.get(1).weight != 0.0F);
        }

        // better Results: lower mutationRate (/10), bigger population size (*10), bigger epoche size (*10)
        final float minMutationRate = 0.1F;//0.014F;
        final float maxMutationRate = 0.18F;//0.18F;
        final float copyPercent = 0.011F;
        final int populationSize = 460;
        final int epocheSize = 1_000;
        final int epochMax = 1500;

        final GeniNet trainedGeniNet = GeniNetTrainService.runTrainNet(geniNet,
                minMutationRate, maxMutationRate, populationSize, epocheSize, copyPercent,
                expectedOutputArrArr, trainInputArrArr, rnd);

        // Act & Assert
        System.out.println("Act & Assert 1");
        //printFullResultForEpoch(net, trainInputArrArr, expectedOutputArrArr, 1, 2);
        //actAssertExpectedOutput(net, trainInputArrArr, expectedOutputArrArr, 0.05F);
        GeniNetPrintUtils.printFullResultForEpoch(trainedGeniNet, trainInputArrArr, expectedOutputArrArr, 1, trainedGeniNet.error);
        GeniNetTestUtils.actAssertExpectedOutput(trainedGeniNet, trainInputArrArr, expectedOutputArrArr, GeniNetService.calcPercentValue(0.05F));
    }

    @Test
    void GIVEN_2_binary_inputs_change_net_THEN_XOR_output() {
        // Arrange
        //GeniNetService.initValueRange(6);
        GeniNetService.initValueRange(100);

        final int[][] trainInputArrArr = calcToValueMax2(new int[][]
                {
                        new int[]{0, 0},
                        new int[]{0, 1},
                        new int[]{1, 0},
                        new int[]{1, 1}
                });
        final int[][] expectedOutputArrArr = calcToValueMax2(new int[][]
                {
                        new int[]{0},
                        new int[]{1},
                        new int[]{1},
                        new int[]{0}
                });
        final int[] layerSizeArr = new int[]{ 2, 4, 3, 1 };

        final Random rnd = new Random(12345);
        //final Random rnd = new Random();

        //final MlpConfiguration config = new MlpConfiguration(true, false);
        //final MlpNet net = MlpNetService.createNet(config, layerSizeArr, rnd);
        final GeniNet geniNet = GeniNetService.createNet(layerSizeArr, rnd);

        // better Results: lower mutationRate (/10), bigger population size (*10), bigger epoche size (*10)
        final float minMutationRate = 0.14F;//0.014F;
        final float maxMutationRate = 0.72F;
        final float copyPercent = 0.011F;//0.011F;
        final int populationSize = 260;
        final int epocheSize = 1_000;
        final int epochMax = 1500;

        final GeniNet trainedGeniNet = GeniNetTrainService.runTrainNet(geniNet,
                minMutationRate, maxMutationRate, populationSize, epocheSize, copyPercent,
                new GeniNetTrainService.GeniNetMutateConfig(true),
                expectedOutputArrArr, trainInputArrArr, rnd);

        // Act & Assert
        System.out.println("Act & Assert 1");
        //printFullResultForEpoch(net, trainInputArrArr, expectedOutputArrArr, 1, 2);
        //actAssertExpectedOutput(net, trainInputArrArr, expectedOutputArrArr, 0.05F);
        GeniNetPrintUtils.printFullResultForEpoch(trainedGeniNet, trainInputArrArr, expectedOutputArrArr, 1, trainedGeniNet.error);
        GeniNetTestUtils.actAssertExpectedOutput(trainedGeniNet, trainInputArrArr, expectedOutputArrArr, GeniNetService.calcPercentValue(0.05F));

        System.out.printf("neuronList.size: %d\n", geniNet.neuronList.size());
        System.out.printf("neuronList.size: %d\n", trainedGeniNet.neuronList.size());
    }

    @Test
    void GIVEN_2_binary_inputs_THEN_AND_output() {
        // Arrange
        //GeniNetService.initValueRange(24);
        GeniNetService.initValueRange(100);

        final int[][] trainInputArrArr = calcToValueMax2(new int[][]
                {
                        new int[]{0, 0},
                        new int[]{0, 1},
                        new int[]{1, 0},
                        new int[]{1, 1}
                });
        final int[][] expectedOutputArrArr = calcToValueMax2(new int[][]
                {
                        new int[]{0},
                        new int[]{0},
                        new int[]{0},
                        new int[]{1}
                });
        final int[] layerSizeArr = new int[]{ 2, 2, 1 };

        final Random rnd = new Random(12345);
        //final Random rnd = new Random();

        //final MlpConfiguration config = new MlpConfiguration(true, false);
        //final MlpNet net = MlpNetService.createNet(config, layerSizeArr, rnd);
        final GeniNet geniNet = GeniNetService.createNet(layerSizeArr, rnd);

        {
            Assertions.assertEquals(2, geniNet.inputNeuronList.size());
            Assertions.assertEquals(1, geniNet.outputNeuronList.size());
            Assertions.assertEquals(2 + 2 + 1, geniNet.neuronList.size());

            Assertions.assertEquals(2, geniNet.neuronList.get(3).inputSynapseList.size());
            //Assertions.assertTrue(geniNet.neuronList.get(3).inputSynapseList.get(0).weight != 0.0F);
            //Assertions.assertTrue(geniNet.neuronList.get(3).inputSynapseList.get(1).weight != 0.0F);
        }

        // better Results: lower mutationRate (/10), bigger population size (*10), bigger epoche size (*10)
        final float minMutationRate = 0.1F;//0.014F;
        final float maxMutationRate = 0.2F;//0.18F;
        final float copyPercent = 0.011F;
        final int populationSize = 100;
        final int epocheSize = 1_000;
        final int epochMax = 1500;

        final GeniNet trainedGeniNet = GeniNetTrainService.runTrainNet(geniNet,
                minMutationRate, maxMutationRate, populationSize, epocheSize, copyPercent,
                expectedOutputArrArr, trainInputArrArr, rnd);

        // Act & Assert
        Assertions.assertEquals(2, geniNet.inputNeuronList.size());
        Assertions.assertEquals(1, geniNet.outputNeuronList.size());
        Assertions.assertEquals(2 + 2 + 1, geniNet.neuronList.size());

        System.out.println("Act & Assert 1");
        //printFullResultForEpoch(net, trainInputArrArr, expectedOutputArrArr, 1, 2);
        //actAssertExpectedOutput(net, trainInputArrArr, expectedOutputArrArr, 0.05F);
        GeniNetPrintUtils.printFullResultForEpoch(trainedGeniNet, trainInputArrArr, expectedOutputArrArr, 1, trainedGeniNet.error);
        GeniNetTestUtils.actAssertExpectedOutput(trainedGeniNet, trainInputArrArr, expectedOutputArrArr, GeniNetService.calcPercentValue(0.05F));
    }
}
