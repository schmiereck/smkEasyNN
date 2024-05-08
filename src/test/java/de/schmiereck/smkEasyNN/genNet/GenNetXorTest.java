package de.schmiereck.smkEasyNN.genNet;

import de.schmiereck.smkEasyNN.mlp.MlpConfiguration;
import de.schmiereck.smkEasyNN.mlp.MlpLayer;
import de.schmiereck.smkEasyNN.mlp.MlpNet;
import de.schmiereck.smkEasyNN.mlp.MlpNetService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static de.schmiereck.smkEasyNN.mlp.MlpNetPrintUtils.printFullResultForEpoch;
import static de.schmiereck.smkEasyNN.genNet.GenNetTestUtils.actAssertExpectedOutput;
import static de.schmiereck.smkEasyNN.mlp.MlpService.runTrainRandom;

public class GenNetXorTest {

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
        final int[] layerSizeArr = new int[]{ 2, 2, 1 };

        final Random rnd = new Random(12345);
        //final Random rnd = new Random();

        //final MlpConfiguration config = new MlpConfiguration(true, false);
        //final MlpNet net = MlpNetService.createNet(config, layerSizeArr, rnd);
        final GenNet genNet = GenNetService.createNet(layerSizeArr, rnd);
        {
            Assertions.assertEquals(2, genNet.inputNeuronList.size());
            Assertions.assertEquals(1, genNet.outputNeuronList.size());
            Assertions.assertEquals(2 + 2 + 1, genNet.neuronList.size());

            Assertions.assertEquals(2, genNet.neuronList.get(2).inputSynapseList.size());
            Assertions.assertTrue(genNet.neuronList.get(2).inputSynapseList.get(0).weight != 0.0F);
            Assertions.assertTrue(genNet.neuronList.get(2).inputSynapseList.get(1).weight != 0.0F);
        }

        // better Results: lower mutationRate (/10), bigger population size (*10), bigger epoche size (*10)
        final float mutationRate = 0.1F;
        final int populationSize = 60;
        final int epocheSize = 1_500;
        final int epochMax = 1500;

        final GenNet trainedGenNet = GenNetTrainService.runTrainNet(genNet, mutationRate, populationSize, epocheSize, 0.0F, expectedOutputArrArr, trainInputArrArr, rnd);

        // Act & Assert
        System.out.println("Act & Assert 1");
        //printFullResultForEpoch(net, trainInputArrArr, expectedOutputArrArr, 1, 2);
        //actAssertExpectedOutput(net, trainInputArrArr, expectedOutputArrArr, 0.05F);
        GenNetPrintUtils.printFullResultForEpoch(trainedGenNet, trainInputArrArr, expectedOutputArrArr, 1, 2);
        GenNetTestUtils.actAssertExpectedOutput(trainedGenNet, trainInputArrArr, expectedOutputArrArr, 0.05F);

        System.out.println("Act & Assert 2");
        //printFullResultForEpoch(net2, trainInputArrArr, expectedOutputArrArr, 2, 2);
        //actAssertExpectedOutput(net2, trainInputArrArr, expectedOutputArrArr, 0.05F);
        GenNetPrintUtils.printFullResultForEpoch(trainedGenNet, trainInputArrArr, expectedOutputArrArr, 2, 2);
        GenNetTestUtils.actAssertExpectedOutput(trainedGenNet, trainInputArrArr, expectedOutputArrArr, 0.05F);
    }

    @Test
    void GIVEN_2_binary_inputs_THEN_AND_output() {
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
                        new float[]{0},
                        new float[]{0},
                        new float[]{1}
                };
        final int[] layerSizeArr = new int[]{ 2, 2, 1 };

        final Random rnd = new Random(12345);
        //final Random rnd = new Random();

        //final MlpConfiguration config = new MlpConfiguration(true, false);
        //final MlpNet net = MlpNetService.createNet(config, layerSizeArr, rnd);
        final GenNet genNet = GenNetService.createNet(layerSizeArr, rnd);

        {
            Assertions.assertEquals(2, genNet.inputNeuronList.size());
            Assertions.assertEquals(1, genNet.outputNeuronList.size());
            Assertions.assertEquals(2 + 2 + 1, genNet.neuronList.size());

            Assertions.assertEquals(2, genNet.neuronList.get(3).inputSynapseList.size());
            Assertions.assertTrue(genNet.neuronList.get(3).inputSynapseList.get(0).weight != 0.0F);
            Assertions.assertTrue(genNet.neuronList.get(3).inputSynapseList.get(1).weight != 0.0F);
        }

        // better Results: lower mutationRate (/10), bigger population size (*10), bigger epoche size (*10)
        final float mutationRate = 0.15F;
        final int populationSize = 60;
        final int epocheSize = 1_500;
        final int epochMax = 1500;

        final GenNet trainedGenNet = GenNetTrainService.runTrainNet(genNet, mutationRate, populationSize, epocheSize, 0.3F, expectedOutputArrArr, trainInputArrArr, rnd);

        // Act & Assert
        Assertions.assertEquals(2, genNet.inputNeuronList.size());
        Assertions.assertEquals(1, genNet.outputNeuronList.size());
        Assertions.assertEquals(2 + 2 + 1, genNet.neuronList.size());

        System.out.println("Act & Assert 1");
        //printFullResultForEpoch(net, trainInputArrArr, expectedOutputArrArr, 1, 2);
        //actAssertExpectedOutput(net, trainInputArrArr, expectedOutputArrArr, 0.05F);
        GenNetPrintUtils.printFullResultForEpoch(trainedGenNet, trainInputArrArr, expectedOutputArrArr, 1, 2);
        GenNetTestUtils.actAssertExpectedOutput(trainedGenNet, trainInputArrArr, expectedOutputArrArr, 0.05F);

        System.out.println("Act & Assert 2");
        //printFullResultForEpoch(net2, trainInputArrArr, expectedOutputArrArr, 2, 2);
        //actAssertExpectedOutput(net2, trainInputArrArr, expectedOutputArrArr, 0.05F);
        GenNetPrintUtils.printFullResultForEpoch(trainedGenNet, trainInputArrArr, expectedOutputArrArr, 2, 2);
        GenNetTestUtils.actAssertExpectedOutput(trainedGenNet, trainInputArrArr, expectedOutputArrArr, 0.05F);
    }
}
