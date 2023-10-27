package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpLayer.calcInitWeight;
import static de.schmiereck.smkEasyNN.mlp.MlpLayerService.addForwwardInputs;
import static de.schmiereck.smkEasyNN.mlp.MlpNetPrintUtils.printFullResultForEpochWithTrainSize;
import static de.schmiereck.smkEasyNN.mlp.MlpNetService.resetNetOutputs;
import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.actAssertExpectedOutput;
import static de.schmiereck.smkEasyNN.mlp.MlpService.runTrainRandom;
import static de.schmiereck.smkEasyNN.mlp.MlpService.runTrainRandomOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MlpLayerConfigTest {

    @Test
    void test_calcBoundingsWrap() {
        assertEquals(1, MlpLayerService.calcBoundingsWrap(-2, 3));
        assertEquals(2, MlpLayerService.calcBoundingsWrap(-1, 3));
        assertEquals(0, MlpLayerService.calcBoundingsWrap(0, 3));
        assertEquals(1, MlpLayerService.calcBoundingsWrap(1, 3));
        assertEquals(2, MlpLayerService.calcBoundingsWrap(2, 3));
        assertEquals(0, MlpLayerService.calcBoundingsWrap(3, 3));
        assertEquals(1, MlpLayerService.calcBoundingsWrap(4, 3));
        assertEquals(2, MlpLayerService.calcBoundingsWrap(5, 3));
    }

    @Test
    void test_calcBoundingsPosOffset() {
        {
            final int limitedSize = 3;
            final int layerSize = 7;
            final int half1LimitedSize = limitedSize / 2;
            final int half2LimitedSize = limitedSize - half1LimitedSize;

            assertEquals(1, MlpLayerService.calcBoundingsPosOffset(layerSize, 0, half1LimitedSize, half2LimitedSize));
            assertEquals(0, MlpLayerService.calcBoundingsPosOffset(layerSize, 1, half1LimitedSize, half2LimitedSize));
            assertEquals(0, MlpLayerService.calcBoundingsPosOffset(layerSize, 2, half1LimitedSize, half2LimitedSize));
            assertEquals(0, MlpLayerService.calcBoundingsPosOffset(layerSize, 3, half1LimitedSize, half2LimitedSize));
            assertEquals(0, MlpLayerService.calcBoundingsPosOffset(layerSize, 4, half1LimitedSize, half2LimitedSize));
            assertEquals(0, MlpLayerService.calcBoundingsPosOffset(layerSize, 5, half1LimitedSize, half2LimitedSize));
            assertEquals(-1, MlpLayerService.calcBoundingsPosOffset(layerSize, 6, half1LimitedSize, half2LimitedSize));
        }
        {
            final int limitedSize = 4;
            final int layerSize = 7;
            final int half1LimitedSize = limitedSize / 2;
            final int half2LimitedSize = limitedSize - half1LimitedSize;

            assertEquals(2, MlpLayerService.calcBoundingsPosOffset(layerSize, 0, half1LimitedSize, half2LimitedSize));
            assertEquals(1, MlpLayerService.calcBoundingsPosOffset(layerSize, 1, half1LimitedSize, half2LimitedSize));
            assertEquals(0, MlpLayerService.calcBoundingsPosOffset(layerSize, 2, half1LimitedSize, half2LimitedSize));
            assertEquals(0, MlpLayerService.calcBoundingsPosOffset(layerSize, 3, half1LimitedSize, half2LimitedSize));
            assertEquals(0, MlpLayerService.calcBoundingsPosOffset(layerSize, 4, half1LimitedSize, half2LimitedSize));
            assertEquals(0, MlpLayerService.calcBoundingsPosOffset(layerSize, 5, half1LimitedSize, half2LimitedSize));
            assertEquals(-1, MlpLayerService.calcBoundingsPosOffset(layerSize, 6, half1LimitedSize, half2LimitedSize));
        }
        {
            final int limitedSize = 5;
            final int layerSize = 7;
            final int half1LimitedSize = limitedSize / 2;
            final int half2LimitedSize = limitedSize - half1LimitedSize;

            assertEquals(2, MlpLayerService.calcBoundingsPosOffset(layerSize, 0, half1LimitedSize, half2LimitedSize));
            assertEquals(1, MlpLayerService.calcBoundingsPosOffset(layerSize, 1, half1LimitedSize, half2LimitedSize));
            assertEquals(0, MlpLayerService.calcBoundingsPosOffset(layerSize, 2, half1LimitedSize, half2LimitedSize));
            assertEquals(0, MlpLayerService.calcBoundingsPosOffset(layerSize, 3, half1LimitedSize, half2LimitedSize));
            assertEquals(0, MlpLayerService.calcBoundingsPosOffset(layerSize, 4, half1LimitedSize, half2LimitedSize));
            assertEquals(-1, MlpLayerService.calcBoundingsPosOffset(layerSize, 5, half1LimitedSize, half2LimitedSize));
            assertEquals(-2, MlpLayerService.calcBoundingsPosOffset(layerSize, 6, half1LimitedSize, half2LimitedSize));
        }
        {
            final int limitedSize = 7;
            final int layerSize = 7;
            final int half1LimitedSize = limitedSize / 2;
            final int half2LimitedSize = limitedSize - half1LimitedSize;

            assertEquals(3, MlpLayerService.calcBoundingsPosOffset(layerSize, 0, half1LimitedSize, half2LimitedSize));
            assertEquals(2, MlpLayerService.calcBoundingsPosOffset(layerSize, 1, half1LimitedSize, half2LimitedSize));
            assertEquals(1, MlpLayerService.calcBoundingsPosOffset(layerSize, 2, half1LimitedSize, half2LimitedSize));
            assertEquals(0, MlpLayerService.calcBoundingsPosOffset(layerSize, 3, half1LimitedSize, half2LimitedSize));
            assertEquals(-1, MlpLayerService.calcBoundingsPosOffset(layerSize, 4, half1LimitedSize, half2LimitedSize));
            assertEquals(-2, MlpLayerService.calcBoundingsPosOffset(layerSize, 5, half1LimitedSize, half2LimitedSize));
            assertEquals(-3, MlpLayerService.calcBoundingsPosOffset(layerSize, 6, half1LimitedSize, half2LimitedSize));
        }
        {
            final int limitedSize = 8;
            final int layerSize = 7;
            final int half1LimitedSize = limitedSize / 2;
            final int half2LimitedSize = limitedSize - half1LimitedSize;

            assertEquals(4, MlpLayerService.calcBoundingsPosOffset(layerSize, 0, half1LimitedSize, half2LimitedSize));
            assertEquals(3, MlpLayerService.calcBoundingsPosOffset(layerSize, 1, half1LimitedSize, half2LimitedSize));
            assertEquals(2, MlpLayerService.calcBoundingsPosOffset(layerSize, 2, half1LimitedSize, half2LimitedSize));
            assertEquals(1, MlpLayerService.calcBoundingsPosOffset(layerSize, 3, half1LimitedSize, half2LimitedSize));
            assertEquals(-1, MlpLayerService.calcBoundingsPosOffset(layerSize, 4, half1LimitedSize, half2LimitedSize));
            assertEquals(-2, MlpLayerService.calcBoundingsPosOffset(layerSize, 5, half1LimitedSize, half2LimitedSize));
            assertEquals(-3, MlpLayerService.calcBoundingsPosOffset(layerSize, 6, half1LimitedSize, half2LimitedSize));
        }
        {
            final int limitedSize = 9;
            final int layerSize = 7;
            final int half1LimitedSize = limitedSize / 2;
            final int half2LimitedSize = limitedSize - half1LimitedSize;

            assertEquals(4, MlpLayerService.calcBoundingsPosOffset(layerSize, 0, half1LimitedSize, half2LimitedSize));
            assertEquals(3, MlpLayerService.calcBoundingsPosOffset(layerSize, 1, half1LimitedSize, half2LimitedSize));
            assertEquals(2, MlpLayerService.calcBoundingsPosOffset(layerSize, 2, half1LimitedSize, half2LimitedSize));
            assertEquals(1, MlpLayerService.calcBoundingsPosOffset(layerSize, 3, half1LimitedSize, half2LimitedSize));
            assertEquals(-2, MlpLayerService.calcBoundingsPosOffset(layerSize, 4, half1LimitedSize, half2LimitedSize));
            assertEquals(-3, MlpLayerService.calcBoundingsPosOffset(layerSize, 5, half1LimitedSize, half2LimitedSize));
            assertEquals(-4, MlpLayerService.calcBoundingsPosOffset(layerSize, 6, half1LimitedSize, half2LimitedSize));
        }
    }

    @Test
    void GIVEN_inputs_THEN_not_all_inputs_are_connected() {
        // Arrange
        final Random rnd = new Random(123456);
        //final Random rnd = new Random();

        final MlpConfiguration mlpConfiguration = new MlpConfiguration(true, false);
        final MlpLayerConfig[] layerConfigArr = new MlpLayerConfig[2];
        layerConfigArr[0] = new MlpLayerConfig(8);
        layerConfigArr[1] = new MlpLayerConfig(7);

        layerConfigArr[0].setIsLimited(3, true);
        layerConfigArr[1].setIsLimited(4, false);

        final MlpNet net = MlpNetService.createNet(mlpConfiguration, layerConfigArr, rnd);

        // Act & Assert
        final MlpLayer[] layerArr = net.getLayerArr();

        assertEquals(2, layerArr.length);

        assertEquals(8, net.getValueInputArr().length);

        assertEquals(8, layerArr[0].neuronArr.length);
        assertEquals(3 + 1, layerArr[0].neuronArr[0].synapseList.size());
        assertEquals(3 + 1, layerArr[0].neuronArr[0].synapseList.size());
        assertEquals(3 + 1, layerArr[0].neuronArr[1].synapseList.size());
        assertEquals(3 + 1, layerArr[0].neuronArr[2].synapseList.size());
        assertEquals(3 + 1, layerArr[0].neuronArr[3].synapseList.size());

        assertEquals(3 + 1, layerArr[0].neuronArr[4].synapseList.size());
        assertEquals(3 + 1, layerArr[0].neuronArr[5].synapseList.size());
        assertEquals(3 + 1, layerArr[0].neuronArr[6].synapseList.size());
        assertEquals(3 + 1, layerArr[0].neuronArr[7].synapseList.size());

        assertEquals(7, layerArr[1].neuronArr.length);
        assertEquals(4 + 1, layerArr[1].neuronArr[0].synapseList.size());
        assertEquals(4 + 1, layerArr[1].neuronArr[1].synapseList.size());
        assertEquals(4 + 1, layerArr[1].neuronArr[2].synapseList.size());
        assertEquals(4 + 1, layerArr[1].neuronArr[3].synapseList.size());

        assertEquals(4 + 1, layerArr[1].neuronArr[4].synapseList.size());
        assertEquals(4 + 1, layerArr[1].neuronArr[5].synapseList.size());
        assertEquals(4 + 1, layerArr[1].neuronArr[6].synapseList.size());

        assertEquals(0, layerArr[0].neuronArr[0].synapseList.get(0).getInput().getNeuronNr());
        assertEquals(1, layerArr[0].neuronArr[0].synapseList.get(1).getInput().getNeuronNr());
        assertEquals(2, layerArr[0].neuronArr[0].synapseList.get(2).getInput().getNeuronNr());

        assertEquals(0, layerArr[0].neuronArr[0].synapseList.get(3).getInput().getNeuronNr());
        assertEquals(-2, layerArr[0].neuronArr[0].synapseList.get(3).getInput().getLayerNr());

        assertEquals(0, layerArr[0].neuronArr[1].synapseList.get(0).getInput().getNeuronNr());
        assertEquals(1, layerArr[0].neuronArr[1].synapseList.get(1).getInput().getNeuronNr());
        assertEquals(2, layerArr[0].neuronArr[1].synapseList.get(2).getInput().getNeuronNr());

        assertEquals(0, layerArr[0].neuronArr[1].synapseList.get(3).getInput().getNeuronNr());
        assertEquals(-2, layerArr[0].neuronArr[1].synapseList.get(3).getInput().getLayerNr());

        assertEquals(1, layerArr[0].neuronArr[2].synapseList.get(0).getInput().getNeuronNr());
        assertEquals(2, layerArr[0].neuronArr[2].synapseList.get(1).getInput().getNeuronNr());
        assertEquals(3, layerArr[0].neuronArr[2].synapseList.get(2).getInput().getNeuronNr());

        assertEquals(0, layerArr[0].neuronArr[2].synapseList.get(3).getInput().getNeuronNr());
        assertEquals(-2, layerArr[0].neuronArr[2].synapseList.get(3).getInput().getLayerNr());

        assertEquals(5, layerArr[0].neuronArr[7].synapseList.get(0).getInput().getNeuronNr());
        assertEquals(6, layerArr[0].neuronArr[7].synapseList.get(1).getInput().getNeuronNr());
        assertEquals(7, layerArr[0].neuronArr[7].synapseList.get(2).getInput().getNeuronNr());

        assertEquals(0, layerArr[0].neuronArr[7].synapseList.get(3).getInput().getNeuronNr());
        assertEquals(-2, layerArr[0].neuronArr[7].synapseList.get(3).getInput().getLayerNr());

    }

    @Test
    void GIVEN_value_input_moves_THEN_direction_is_output() {
        // Arrange
        final float[][] trainInputArrArr = new float[][]
                {
                        new float[]{
                                1, 1, 1, 1,     // 0
                                1, 0, 0, 1,
                                1, 0, 0, 1,
                                1, 0, 0, 1,
                                1, 1, 1, 1,
                        },
                        new float[]{
                                0, 0, 0, 1,     // 1
                                0, 0, 0, 1,
                                0, 0, 0, 1,
                                0, 0, 0, 1,
                                0, 0, 0, 1,
                        },
                        new float[]{
                                0, 1, 1, 0,     // 2
                                1, 0, 0, 1,
                                0, 0, 1, 0,
                                0, 1, 0, 0,
                                1, 1, 1, 1,
                        },
                        new float[]{
                                1, 1, 1, 1,     // 3
                                0, 0, 0, 1,
                                0, 0, 1, 1,
                                0, 0, 0, 1,
                                1, 1, 1, 1,
                        },
                        new float[]{
                                0, 0, 1, 0,     // 4
                                0, 1, 0, 0,
                                1, 1, 1, 1,
                                0, 0, 1, 0,
                                0, 0, 1, 0,
                        },
                };
        final float[][] expectedOutputArrArr = new float[][]
                {
                                //           0  1  2  3  4
                                new float[]{ 1, 0, 0, 0, 0 },     // 0
                                new float[]{ 0, 1, 0, 0, 0 },     // 1
                                new float[]{ 0, 0, 1, 0, 0 },     // 2
                                new float[]{ 0, 0, 0, 1, 0 },     // 3
                                new float[]{ 0, 0, 0, 0, 1 },     // 4
                };

        final Random rnd = new Random(123456);
        //final Random rnd = new Random();

        final MlpConfiguration config = new MlpConfiguration(true, false);
        final MlpLayerConfig[] layerConfigArr = new MlpLayerConfig[5];
        layerConfigArr[0] = new MlpLayerConfig(4 * 5);
        layerConfigArr[1] = new MlpLayerConfig(24);
        layerConfigArr[2] = new MlpLayerConfig(12);
        layerConfigArr[3] = new MlpLayerConfig(8);
        layerConfigArr[4] = new MlpLayerConfig(5);

        //layerConfigArr[0].setIsArray(true,4, 3, 4, 3, 0);
        //layerConfigArr[1].setIsArray(true,4, 3, 4, 3, 12);
        layerConfigArr[0].setIsLimited(20, true);
        layerConfigArr[1].setIsLimited(6, true);
        layerConfigArr[2].setIsLimited(6, true);
        layerConfigArr[3].setIsLimited(4, true);
        //layerConfigArr[4].setIsLimited(4, true);

        final MlpNet net = MlpNetService.createNet(config, layerConfigArr, rnd);

        //addForwwardInputs2(net, 2, 1, true, false, true, false, false, rnd);
        addForwwardInputs(net, 2, 1, false, false, true, true, true, rnd);
        //addAdditionalBiasInputToLayer(net, 2, false, rnd);

        int successfulCounterMax = 30;
        int successfulCounter = 0;
        final int epochMax = 66_000;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            resetNetOutputs(net);

            final float mainOutputMseErrorValue = runTrainRandom(net,
                    expectedOutputArrArr, trainInputArrArr, 0.1F, 0.9F, rnd);

            if ((epochPos + 1) % 600 == 0) {
                MlpNetPrintUtils.printFullResultForEpoch(net, trainInputArrArr, expectedOutputArrArr, epochPos, mainOutputMseErrorValue);
            }
            if (mainOutputMseErrorValue < 0.0001F) {
                successfulCounter++;
                if (successfulCounter > successfulCounterMax) {
                    printFullResultForEpochWithTrainSize(net, trainInputArrArr, expectedOutputArrArr, epochPos, mainOutputMseErrorValue);
                    break;
                }
            } else {
                successfulCounter = 0;
            }
        }

        // Act & Assert
        actAssertExpectedOutput(net, trainInputArrArr, expectedOutputArrArr, 0.1F);
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

        actAssertExpectedOutput(net, inputArrArr, unexpectedOutputArrArr, 0.6F);
        System.out.println();
        System.out.println("unexpectedOutput:");
        printResult(net, inputArrArr, unexpectedOutputArrArr);
        */
    }
}
