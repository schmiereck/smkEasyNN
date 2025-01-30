package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpNetPrintUtils.printFullResultForEpoch;
import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.actAssertExpectedOutput;
import static de.schmiereck.smkEasyNN.mlp.MlpService.runTrainRandom;

import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MlpNetLogicTest {

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

        final MlpConfiguration config = new MlpConfiguration(true, false);
        final MlpNet net = MlpNetService.createNet(config, layerSizeArr, rnd);

        final int epochMax = 1500;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            final float mainOutputMseErrorValue = runTrainRandom(net, expectedOutputArrArr, trainInputArrArr, rnd);

            if ((epochPos + 1) % 100 == 0) {
                printFullResultForEpoch(net, trainInputArrArr, expectedOutputArrArr, epochPos, mainOutputMseErrorValue);
            }
        }
        final MlpNet net2 = MlpNetService.duplicateNet(net);

        // Act & Assert
        System.out.println("Act & Assert 1");
        printFullResultForEpoch(net, trainInputArrArr, expectedOutputArrArr, 1, 2);
        actAssertExpectedOutput(net, trainInputArrArr, expectedOutputArrArr, 0.05F);

        System.out.println("Act & Assert 2");
        printFullResultForEpoch(net2, trainInputArrArr, expectedOutputArrArr, 2, 2);
        actAssertExpectedOutput(net2, trainInputArrArr, expectedOutputArrArr, 0.05F);
    }

    @Test
    void GIVEN_2_binary_inputs_THEN_OR_output() {
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
                        new float[]{1}
                };
        final int[] layerSizeArr = new int[]{ 2, 1 };

        final Random rnd = new Random(12345);
        //final Random rnd = new Random();

        final MlpConfiguration config = new MlpConfiguration(true, false);
        final MlpNet net = MlpNetService.createNet(config, layerSizeArr, rnd);

        final int epochMax = 1500;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            final float mainOutputMseErrorValue = runTrainRandom(net, expectedOutputArrArr, trainInputArrArr, rnd);

            if ((epochPos + 1) % 100 == 0) {
                printFullResultForEpoch(net, trainInputArrArr, expectedOutputArrArr, epochPos, mainOutputMseErrorValue);
            }
        }
        final MlpNet net2 = MlpNetService.duplicateNet(net);

        // Act & Assert
        System.out.println("Act & Assert 1");
        printFullResultForEpoch(net, trainInputArrArr, expectedOutputArrArr, 1, 2);
        actAssertExpectedOutput(net, trainInputArrArr, expectedOutputArrArr, 0.05F);

        System.out.println("Act & Assert 2");
        printFullResultForEpoch(net2, trainInputArrArr, expectedOutputArrArr, 2, 2);
        actAssertExpectedOutput(net2, trainInputArrArr, expectedOutputArrArr, 0.05F);
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

        final MlpConfiguration config = new MlpConfiguration(true, false);
        final MlpNet net = MlpNetService.createNet(config, layerSizeArr, rnd);

        {
            final MlpLayer[] layerArr = net.getLayerArr();
            Assertions.assertEquals(2 + 1, layerArr[1].neuronArr[0].synapseList.size());
            Assertions.assertTrue(layerArr[1].neuronArr[0].synapseList.get(0).weight != 0.0F);
            Assertions.assertTrue(layerArr[1].neuronArr[0].synapseList.get(1).weight != 0.0F);
            Assertions.assertTrue(layerArr[1].neuronArr[0].synapseList.get(2).weight == 0.0F);
        }
        final int epochMax = 1500;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            final float mainOutputMseErrorValue = runTrainRandom(net, expectedOutputArrArr, trainInputArrArr, rnd);

            if ((epochPos + 1) % 100 == 0) {
                printFullResultForEpoch(net, trainInputArrArr, expectedOutputArrArr, epochPos, mainOutputMseErrorValue);
            }
        }
        final MlpNet mlpNet2 = MlpNetService.duplicateNet(net);

        // Act & Assert
        final MlpLayer[] layerArr = net.getLayerArr();
        Assertions.assertEquals(2, net.getValueInputArr().length);
        Assertions.assertEquals(3, layerArr.length);
        Assertions.assertEquals(2, layerArr[0].neuronArr.length);
        Assertions.assertEquals(2, layerArr[1].neuronArr.length);
        Assertions.assertEquals(1, layerArr[2].neuronArr.length);

        Assertions.assertEquals(2 + 1, layerArr[1].neuronArr[0].synapseList.size());
        Assertions.assertTrue(layerArr[1].neuronArr[0].synapseList.get(0).weight != 0.0F);
        Assertions.assertTrue(layerArr[1].neuronArr[0].synapseList.get(1).weight != 0.0F);
        Assertions.assertTrue(layerArr[1].neuronArr[0].synapseList.get(2).weight != 0.0F);

        System.out.println("Act & Assert 1");
        printFullResultForEpoch(net, trainInputArrArr, expectedOutputArrArr, 1, 2);
        actAssertExpectedOutput(net, trainInputArrArr, expectedOutputArrArr, 0.05F);

        System.out.println("Act & Assert 2");
        printFullResultForEpoch(mlpNet2, trainInputArrArr, expectedOutputArrArr, 2, 2);
        actAssertExpectedOutput(mlpNet2, trainInputArrArr, expectedOutputArrArr, 0.05F);
    }

    @Test
    void GIVEN_2_binary_inputs_THEN_XOR_AND_OR_output() {
        // Arrange
        final float[][] trainInputArrArr = new float[][]
                {
                        // AND:
                        new float[]{0, 1, 0,  0, 0},
                        new float[]{0, 1, 0,  0, 1},
                        new float[]{0, 1, 0,  1, 0},
                        new float[]{0, 1, 0,  1, 1},
                        // OR:
                        new float[]{0, 0, 1,  0, 0},
                        new float[]{0, 0, 1,  0, 1},
                        new float[]{0, 0, 1,  1, 0},
                        new float[]{0, 0, 1,  1, 1},
                        // XOR:
                        new float[]{0, 1, 1,  0, 0},
                        new float[]{0, 1, 1,  0, 1},
                        new float[]{0, 1, 1,  1, 0},
                        new float[]{0, 1, 1,  1, 1},
                        // NAND:
                        new float[]{1, 1, 0,  0, 0},
                        new float[]{1, 1, 0,  0, 1},
                        new float[]{1, 1, 0,  1, 0},
                        new float[]{1, 1, 0,  1, 1},
                        // NOR:
                        new float[]{1, 0, 1,  0, 0},
                        new float[]{1, 0, 1,  0, 1},
                        new float[]{1, 0, 1,  1, 0},
                        new float[]{1, 0, 1,  1, 1},
                        // XNOR:
                        new float[]{1, 1, 1,  0, 0},
                        new float[]{1, 1, 1,  0, 1},
                        new float[]{1, 1, 1,  1, 0},
                        new float[]{1, 1, 1,  1, 1},
                };
        final float[][] expectedOutputArrArr = new float[][]
                {
                        // AND:
                        new float[]{0},
                        new float[]{0},
                        new float[]{0},
                        new float[]{1},
                        // OR:
                        new float[]{0},
                        new float[]{1},
                        new float[]{1},
                        new float[]{1},
                        // XOR:
                        new float[]{0},
                        new float[]{1},
                        new float[]{1},
                        new float[]{0},
                        // NAND:
                        new float[]{1},
                        new float[]{1},
                        new float[]{1},
                        new float[]{0},
                        // NOR:
                        new float[]{1},
                        new float[]{0},
                        new float[]{0},
                        new float[]{0},
                        // XNOR:
                        new float[]{1},
                        new float[]{0},
                        new float[]{0},
                        new float[]{1},
                };
        final int[] layerSizeArr = new int[]{ 3+2, 2, 1 };

        final Random rnd = new Random(12345);
        //final Random rnd = new Random();

        final MlpConfiguration config = new MlpConfiguration(true, false);
        final MlpNet net = MlpNetService.createNet(config, layerSizeArr, rnd);

        final int epochMax = 2000;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            final float mainOutputMseErrorValue = runTrainRandom(net, expectedOutputArrArr, trainInputArrArr, rnd);

            if ((epochPos + 1) % 100 == 0) {
                printFullResultForEpoch(net, trainInputArrArr, expectedOutputArrArr, epochPos, mainOutputMseErrorValue);
            }
        }

        // Act & Assert
        System.out.println("Act & Assert 1");
        printFullResultForEpoch(net, trainInputArrArr, expectedOutputArrArr, 1, 2);
        actAssertExpectedOutput(net, trainInputArrArr, expectedOutputArrArr, 0.05F);
    }
}
