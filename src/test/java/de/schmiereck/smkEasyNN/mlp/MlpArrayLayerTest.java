package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpNetService.resetNetOutputs;
import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.actAssertExpectedOutput;
import static de.schmiereck.smkEasyNN.mlp.MlpLayerService.addForwwardInputs;
import static de.schmiereck.smkEasyNN.mlp.MlpService.runTrainRandomOrder;

import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MlpArrayLayerTest {

    @Test
    void GIVEN_3x4_inputs_THEN_all_inputs_are_connected() {
        // Arrange
        final Random rnd = new Random(123456);
        //final Random rnd = new Random();

        final MlpConfiguration mlpConfiguration = new MlpConfiguration(true, false, 1.25F);
        final MlpLayerConfig[] layerConfigArr = new MlpLayerConfig[2];
        layerConfigArr[0] = new MlpLayerConfig(4*3);
        layerConfigArr[1] = new MlpLayerConfig(4*3);

        layerConfigArr[0].setIsArray(true,1, 1, 4, 3, 0);
        layerConfigArr[1].setIsArray(true,1, 1, 4, 3, 0);

        final MlpNet net = MlpNetService.createNet(mlpConfiguration, layerConfigArr, rnd);

        // Act & Assert
        final MlpLayer[] layerArr = net.getLayerArr();

        Assertions.assertEquals(4*3, net.getValueInputArr().length);
        Assertions.assertEquals(2, layerArr.length);
        Assertions.assertEquals(4*3, layerArr[0].neuronArr.length);

        //   | 1  2  3  4
        // --+------------
        // 1 | 0  1  2  3
        // 2 | 4  5  6  7
        // 3 | 8  9 10 11

        Assertions.assertEquals(2*2 + 1, layerArr[0].neuronArr[0].synapseList.size());
        Assertions.assertEquals(2*3 + 1, layerArr[0].neuronArr[1].synapseList.size());
        Assertions.assertEquals(2*3 + 1, layerArr[0].neuronArr[2].synapseList.size());
        Assertions.assertEquals(2*2 + 1, layerArr[0].neuronArr[3].synapseList.size());

        Assertions.assertEquals(2*3 + 1, layerArr[0].neuronArr[4].synapseList.size());
        Assertions.assertEquals(3*3 + 1, layerArr[0].neuronArr[5].synapseList.size());
        Assertions.assertEquals(3*3 + 1, layerArr[0].neuronArr[6].synapseList.size());
        Assertions.assertEquals(2*3 + 1, layerArr[0].neuronArr[7].synapseList.size());

        Assertions.assertEquals(2*2 + 1, layerArr[0].neuronArr[8].synapseList.size());
        Assertions.assertEquals(2*3 + 1, layerArr[0].neuronArr[9].synapseList.size());
        Assertions.assertEquals(2*3 + 1, layerArr[0].neuronArr[10].synapseList.size());
        Assertions.assertEquals(2*2 + 1, layerArr[0].neuronArr[11].synapseList.size());
    }

    @Test
    void GIVEN_4x4x4p1_inputs_THEN_all_inputs_are_connected() {
        // Arrange
        final Random rnd = new Random(123456);
        //final Random rnd = new Random();

        final MlpConfiguration mlpConfiguration = new MlpConfiguration(true, false, 1.25F);
        final MlpLayerConfig[] layerConfigArr = new MlpLayerConfig[2];
        layerConfigArr[0] = new MlpLayerConfig(64+1);
        layerConfigArr[1] = new MlpLayerConfig(64);

        layerConfigArr[0].setIsArray(true,1, 1, 16, 4, 1);
        layerConfigArr[1].setIsArray(true,1, 1, 16, 4, 0);

        final MlpNet net = MlpNetService.createNet(mlpConfiguration, layerConfigArr, rnd);

        // Act & Assert
        final MlpLayer[] layerArr = net.getLayerArr();

        Assertions.assertEquals(64+1, net.getValueInputArr().length);
        Assertions.assertEquals(2, layerArr.length);
        Assertions.assertEquals(64+1, layerArr[0].neuronArr.length);

        Assertions.assertEquals(2*2 + 1, layerArr[0].neuronArr[0].synapseList.size());
        Assertions.assertEquals(2*3 + 1, layerArr[0].neuronArr[1].synapseList.size());
        Assertions.assertEquals(2*3 + 1, layerArr[0].neuronArr[2].synapseList.size());
        Assertions.assertEquals(2*3 + 1, layerArr[0].neuronArr[3].synapseList.size());
        // ...
        Assertions.assertEquals(2*3 + 1, layerArr[0].neuronArr[14].synapseList.size());
        Assertions.assertEquals(2*2 + 1, layerArr[0].neuronArr[15].synapseList.size());

        Assertions.assertEquals(2*3 + 1, layerArr[0].neuronArr[16].synapseList.size());
        Assertions.assertEquals(3*3 + 1, layerArr[0].neuronArr[17].synapseList.size());
        // ...
        Assertions.assertEquals(3*3 + 1, layerArr[0].neuronArr[30].synapseList.size());
        Assertions.assertEquals(2*3 + 1, layerArr[0].neuronArr[31].synapseList.size());

        Assertions.assertEquals(2*3 + 1, layerArr[0].neuronArr[32].synapseList.size());
        Assertions.assertEquals(3*3 + 1, layerArr[0].neuronArr[33].synapseList.size());
        // ...
        Assertions.assertEquals(3*3 + 1, layerArr[0].neuronArr[46].synapseList.size());
        Assertions.assertEquals(3*2 + 1, layerArr[0].neuronArr[47].synapseList.size());

        Assertions.assertEquals(2*2 + 1, layerArr[0].neuronArr[48].synapseList.size());
        Assertions.assertEquals(2*3 + 1, layerArr[0].neuronArr[49].synapseList.size());
        // ...
        Assertions.assertEquals(2*3 + 1, layerArr[0].neuronArr[62].synapseList.size());
        Assertions.assertEquals(2*2 + 1, layerArr[0].neuronArr[63].synapseList.size());

        Assertions.assertEquals(16*4+1 + 1, layerArr[0].neuronArr[64].synapseList.size());
    }

    @Test
    void GIVEN_value_input_moves_THEN_direction_is_output() {
        // Arrange
        final float[][][] trainInputArrArrArr = new float[][][]
                {
                        {       // A1
                                new float[]{
                                        0, 0, 0, 0,     // stay
                                        0, 1, 0, 0,
                                        0, 0, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // right
                                        0, 0, 1, 0,
                                        0, 0, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // right
                                        0, 0, 0, 1,
                                        0, 0, 0, 0},
                                new float[]{
                                        0, 0, 0, 1,     // up
                                        0, 0, 0, 0,
                                        0, 0, 0, 0},
                                new float[]{
                                        0, 0, 1, 0,     // left
                                        0, 0, 0, 0,
                                        0, 0, 0, 0},
                                new float[]{
                                        0, 1, 0, 0,     // left
                                        0, 0, 0, 0,
                                        0, 0, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // down
                                        0, 1, 0, 0,
                                        0, 0, 0, 0},
                        },
                        {       // A2
                                new float[]{
                                        0, 0, 0, 0,     // stay
                                        0, 1, 0, 0,
                                        0, 0, 0, 0},
                                new float[]{
                                        0, 1, 0, 0,     // up
                                        0, 0, 0, 0,
                                        0, 0, 0, 0},
                                new float[]{
                                        1, 0, 0, 0,     // left
                                        0, 0, 0, 0,
                                        0, 0, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // down
                                        1, 0, 0, 0,
                                        0, 0, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // down
                                        0, 0, 0, 0,
                                        1, 0, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // right
                                        0, 0, 0, 0,
                                        0, 1, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // up
                                        0, 1, 0, 0,
                                        0, 0, 0, 0},
                        },
                        {       // A3
                                new float[]{
                                        0, 0, 0, 0,     // stay
                                        0, 1, 0, 0,
                                        0, 0, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // down
                                        0, 0, 0, 0,
                                        0, 1, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // right
                                        0, 0, 0, 0,
                                        0, 0, 1, 0},
                                new float[]{
                                        0, 0, 0, 0,     // up
                                        0, 0, 1, 0,
                                        0, 0, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // left
                                        0, 1, 0, 0,
                                        0, 0, 0, 0},
                        },
                        {       // A4
                                new float[]{
                                        0, 0, 0, 0,     // stay
                                        0, 1, 0, 0,
                                        0, 0, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // right
                                        0, 0, 1, 0,
                                        0, 0, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // left
                                        0, 1, 0, 0,
                                        0, 0, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // left
                                        1, 0, 0, 0,
                                        0, 0, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // right
                                        0, 1, 0, 0,
                                        0, 0, 0, 0},
                        },
                        {       // A5
                                new float[]{
                                        0, 0, 0, 0,     // stay
                                        0, 1, 0, 0,
                                        0, 0, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // down
                                        0, 0, 0, 0,
                                        0, 1, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // left
                                        0, 0, 0, 0,
                                        1, 0, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // right
                                        0, 0, 0, 0,
                                        0, 1, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // right
                                        0, 0, 0, 0,
                                        0, 0, 1, 0},
                                new float[]{
                                        0, 0, 0, 0,     // right
                                        0, 0, 0, 0,
                                        0, 0, 0, 1},
                                new float[]{
                                        0, 0, 0, 0,     // left
                                        0, 0, 0, 0,
                                        0, 0, 1, 0},
                                new float[]{
                                        0, 0, 0, 0,     // left
                                        0, 0, 0, 0,
                                        0, 1, 0, 0},
                                new float[]{
                                        0, 0, 0, 0,     // up
                                        0, 1, 0, 0,
                                        0, 0, 0, 0},
                        },
                };
        final float[][][] expectedOutputArrArrArr = new float[][][]
                {
                        //              stay, left, right, up, down
                        {       // A1
                                new float[]{ 1, 0, 0, 0, 0 },     // stay
                                new float[]{ 0, 0, 1, 0, 0 },     // right
                                new float[]{ 0, 0, 1, 0, 0 },     // right
                                new float[]{ 0, 0, 0, 1, 0 },     // up
                                new float[]{ 0, 1, 0, 0, 0 },     // left
                                new float[]{ 0, 1, 0, 0, 0 },     // left
                                new float[]{ 0, 0, 0, 0, 1 },     // down
                        },
                        {       // A2
                                new float[]{ 1, 0, 0, 0, 0 },     // stay
                                new float[]{ 0, 0, 0, 1, 0 },     // up
                                new float[]{ 0, 1, 0, 0, 0 },     // left
                                new float[]{ 0, 0, 0, 0, 1 },     // down
                                new float[]{ 0, 0, 0, 0, 1 },     // down
                                new float[]{ 0, 0, 1, 0, 0 },     // right
                                new float[]{ 0, 0, 0, 1, 0 },     // up
                        },
                        //              stay, left, right, up, down
                        {       // A3
                                new float[]{ 1, 0, 0, 0, 0 },     // stay
                                new float[]{ 0, 0, 0, 0, 1 },     // down
                                new float[]{ 0, 0, 1, 0, 0 },     // right
                                new float[]{ 0, 0, 0, 1, 0 },     // up
                                new float[]{ 0, 1, 0, 0, 0 },     // left
                        },
                        {       // A4
                                new float[]{ 1, 0, 0, 0, 0 },     // stay
                                new float[]{ 0, 0, 1, 0, 0 },     // right
                                new float[]{ 0, 1, 0, 0, 0 },     // left
                                new float[]{ 0, 1, 0, 0, 0 },     // left
                                new float[]{ 0, 0, 1, 0, 0 },     // right
                        },
                        {       // A5
                                new float[]{ 1, 0, 0, 0, 0 },     // stay
                                new float[]{ 0, 0, 0, 0, 1 },     // down
                                new float[]{ 0, 1, 0, 0, 0 },     // left
                                new float[]{ 0, 0, 1, 0, 0 },     // right
                                new float[]{ 0, 0, 1, 0, 0 },     // right
                                new float[]{ 0, 0, 1, 0, 0 },     // right
                                new float[]{ 0, 1, 0, 0, 0 },     // left
                                new float[]{ 0, 1, 0, 0, 0 },     // left
                                new float[]{ 0, 0, 0, 1, 0 },     // up
                        },
                };

        final Random rnd = new Random(123456);
        //final Random rnd = new Random();

        final MlpConfiguration mlpConfiguration = new MlpConfiguration(true, false,
                0.5F, 0.0F);
        final MlpLayerConfig[] layerConfigArr = new MlpLayerConfig[5];
        layerConfigArr[0] = new MlpLayerConfig(12);
        layerConfigArr[1] = new MlpLayerConfig(24);
        layerConfigArr[2] = new MlpLayerConfig(12);
        layerConfigArr[3] = new MlpLayerConfig(8);
        layerConfigArr[4] = new MlpLayerConfig(5);

        layerConfigArr[0].setIsArray(true,4, 3, 4, 3, 0);
        layerConfigArr[1].setIsArray(true,4, 3, 4, 3, 12);

        final MlpNet mlpNet = MlpNetService.createNet(mlpConfiguration, layerConfigArr, rnd);

        //addForwwardInputs2(mlpNet, 2, 1, true, false, true, false, false, rnd);
        addForwwardInputs(mlpNet, 2, 1, false, false, true, true, true, rnd);
        //addAdditionalBiasInputToLayer(mlpNet, 2, false, rnd);

        final int epochMax = 66_000;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            resetNetOutputs(mlpNet);

            final float mainOutputMseErrorValue = runTrainRandomOrder(mlpNet,
                    expectedOutputArrArrArr, trainInputArrArrArr, 0.1F, 0.9F, rnd);

            if ((epochPos + 1) % 600 == 0) {
                MlpNetPrintUtils.printFullResultForEpoch(mlpNet, trainInputArrArrArr, expectedOutputArrArrArr, epochPos, mainOutputMseErrorValue);
            }
        }

        // Act & Assert
        actAssertExpectedOutput(mlpNet, trainInputArrArrArr, expectedOutputArrArrArr, 0.1F);
        /*
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
        */
    }
}
