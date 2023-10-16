package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpNetPrintUtils.printFullResultForEpoch;
import static de.schmiereck.smkEasyNN.mlp.MlpNetPrintUtils.printFullResultForEpochWithTrainSize;
import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.actAssertExpectedOutput;
import static de.schmiereck.smkEasyNN.mlp.MlpService.runTrainRandom;
import static de.schmiereck.smkEasyNN.mlp.MlpService.train;

import java.util.Objects;
import java.util.Random;

import org.junit.jupiter.api.Test;

public class MlpNetMathTest {
    private record Result(float[][] trainInputArrArr, float[][] expectedOutputArrArr) {
    }

    public static float runTrainRandom(final MlpNet net, final float[][] expectedOutputArrArr, final float[][] trainInputArrArr,
                                       final float learningRate, final float momentum, final Random rnd,
                                       final MlpNet trainNet, final int trainSize) {
        float mainOutputMseErrorValue = 0.0F;
        int mainOutputCount = 0;
        for (int expectedResultPos = 0; expectedResultPos < expectedOutputArrArr.length; expectedResultPos++) {
            int idx = rnd.nextInt(expectedOutputArrArr.length);

            final float[][][] inArrArrArr = new float[net.getLayerArr().length][trainSize][3 + (trainSize + 1) * 3];

            for (int layerPos = 0; layerPos < net.getLayerArr().length; layerPos++) {
                final MlpLayer layer = net.getLayer(layerPos);

                final float[][] inArrArr = inArrArrArr[layerPos];

                for (int neuronPos = 0; neuronPos < layer.neuronArr.length; neuronPos++) {
                    final MlpNeuron neuron = layer.neuronArr[neuronPos];

                    final float[] inArr = inArrArr[neuronPos];

                    for (int synapsePos = 0; synapsePos < neuron.synapseList.size(); synapsePos++) {
                        final MlpSynapse synapse = neuron.synapseList.get(synapsePos);

                        inArr[3 + synapsePos * 3 + 0] = synapse.weight;
                        inArr[3 + synapsePos * 3 + 1] = Objects.nonNull(synapse.getInputError()) ? synapse.getInputError().getErrorValue() : 0.0F;
                    }
                }
            }
            mainOutputMseErrorValue += train(net, trainInputArrArr[idx], expectedOutputArrArr[idx], learningRate, momentum);
            mainOutputCount++;

            for (int layerPos = 0; layerPos < net.getLayerArr().length; layerPos++) {
                final MlpLayer layer = net.getLayer(layerPos);

                final float[][] inArrArr = inArrArrArr[layerPos];

                for (int neuronPos = 0; neuronPos < layer.neuronArr.length; neuronPos++) {
                    final MlpNeuron neuron = layer.neuronArr[neuronPos];

                    final float[] inArr = inArrArr[neuronPos];

                    inArr[0] = layerPos / (float)net.getLayerArr().length;
                    inArr[1] = neuron.errorValue;
                    inArr[2] = neuron.outputValue;

                    final float[] outArr = new float[(trainSize + 1) * 2];

                    for (int synapsePos = 0; synapsePos < neuron.synapseList.size(); synapsePos++) {
                        final MlpSynapse synapse = neuron.synapseList.get(synapsePos);

                        inArr[3 + synapsePos * 3 + 2] = synapse.getInput().getInputValue();

                        outArr[0] = synapse.weight;
                        outArr[1] = Objects.nonNull(synapse.getInputError()) ? synapse.getInputError().getErrorValue() : 0.0F;
                    }

                    train(trainNet, inArr, outArr, learningRate, momentum);
                }
            }
        }
        return mainOutputMseErrorValue / mainOutputCount;
    }

    @Test
    void GIVEN_2_value_inputs_THEN_add_output() {
        // Arrange
        final Result result = arrangeAddResult();

        final Random rnd = new Random(12345);
        //final Random rnd = new Random();

        final int[] layerSizeArr = new int[]{ 2, 6, 4, 1 };
        final MlpConfiguration config = new MlpConfiguration(true, false);
        final MlpNet net = MlpNetService.createNet(config, layerSizeArr, rnd);

        final int trainSize = 6;
        final int[] trainLayerSizeArr = new int[]{ 3 + 3 * trainSize, 6 * trainSize, 4 * trainSize, 2 * trainSize };
        final MlpConfiguration trainConfig = new MlpConfiguration(true, false);
        final MlpNet trainNet = MlpNetService.createNet(trainConfig, trainLayerSizeArr, rnd);

        final int successfulCounterMax = 60;
        int successfulCounter = 0;
        final int epochMax = 7_000;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            final float mainOutputMseErrorValue = runTrainRandom(net, result.expectedOutputArrArr, result.trainInputArrArr,
                    0.1F, 0.6F, rnd,
                    trainNet, trainSize);

            if ((epochPos + 1) % 100 == 0) {
                printFullResultForEpoch(net, result.trainInputArrArr, result.expectedOutputArrArr, epochPos, mainOutputMseErrorValue);
            }
            if (mainOutputMseErrorValue < 0.0001F) {
                successfulCounter++;
                if (successfulCounter > successfulCounterMax) {
                    printFullResultForEpochWithTrainSize(net, result.trainInputArrArr, result.expectedOutputArrArr, epochPos, mainOutputMseErrorValue);
                    break;
                }
            } else {
                successfulCounter = 0;
            }
        }

        // Act & Assert
        System.out.println("Act & Assert");
        actAssertExpectedOutput(net, result.trainInputArrArr, result.expectedOutputArrArr, 0.01F);
    }
    @Test
    void GIVEN_2_value_inputs_THEN_sub_output() {
        // Arrange
        final Result result = arrangeSubResult();
        final int[] layerSizeArr = new int[]{ 2, 6, 4, 1 };

        final Random rnd = new Random(12345);
        //final Random rnd = new Random();

        final MlpConfiguration config = new MlpConfiguration(true, false);
        final MlpNet net = MlpNetService.createNet(config, layerSizeArr, rnd);

        final int successfulCounterMax = 60;
        int successfulCounter = 0;
        final int epochMax = 7_000;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            final float mainOutputMseErrorValue = MlpService.runTrainRandom(net, result.expectedOutputArrArr, result.trainInputArrArr,
                    0.1F, 0.6F, rnd);

            if ((epochPos + 1) % 100 == 0) {
                printFullResultForEpoch(net, result.trainInputArrArr, result.expectedOutputArrArr, epochPos, mainOutputMseErrorValue);
            }
            if (mainOutputMseErrorValue < 0.0001F) {
                successfulCounter++;
                if (successfulCounter > successfulCounterMax) {
                    printFullResultForEpochWithTrainSize(net, result.trainInputArrArr, result.expectedOutputArrArr, epochPos, mainOutputMseErrorValue);
                    break;
                }
            } else {
                successfulCounter = 0;
            }
        }

        // Act & Assert
        System.out.println("Act & Assert");
        actAssertExpectedOutput(net, result.trainInputArrArr, result.expectedOutputArrArr, 0.01F);
    }

    @Test
    void GIVEN_2_value_inputs_THEN_mult_output() {
        // Arrange
        final Result result = arrangeMultResult();
        final int[] layerSizeArr = new int[]{ 2, 4, 8, 16, 4, 1 };

        final Random rnd = new Random(12345);
        //final Random rnd = new Random();

        final MlpConfiguration config = new MlpConfiguration(true, false);
        final MlpNet net = MlpNetService.createNet(config, layerSizeArr, rnd);

        final int successfulCounterMax = 80;
        int successfulCounter = 0;
        final int epochMax = 247_000;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            final float mainOutputMseErrorValue = MlpService.runTrainRandom(net, result.expectedOutputArrArr, result.trainInputArrArr,
                    0.01F, 0.8F, rnd);

            if ((epochPos + 1) % 100 == 0) {
                printFullResultForEpoch(net, result.trainInputArrArr, result.expectedOutputArrArr, epochPos, mainOutputMseErrorValue);
            }
            if (mainOutputMseErrorValue < 0.0001F) {
                successfulCounter++;
                if (successfulCounter > successfulCounterMax) {
                    printFullResultForEpochWithTrainSize(net, result.trainInputArrArr, result.expectedOutputArrArr, epochPos, mainOutputMseErrorValue);
                    break;
                }
            } else {
                successfulCounter = 0;
            }
        }

        // Act & Assert
        System.out.println("Act & Assert");
        actAssertExpectedOutput(net, result.trainInputArrArr, result.expectedOutputArrArr, 0.02F);
    }

    private static Result arrangeAddResult() {
        final float[][] trainInputArrArr = new float[][]
                {
                        new float[]{0, 0},

                        new float[]{0, 0},
                        new float[]{0, 1},
                        new float[]{0, 2},
                        new float[]{0, 3},

                        new float[]{1, 0},
                        new float[]{1, 1},
                        new float[]{1, 2},
                        new float[]{1, 3},

                        new float[]{2, 0},
                        new float[]{2, 1},
                        new float[]{2, 2},
                        new float[]{2, 3},

                        new float[]{3, 0},
                        new float[]{3, 1},
                        new float[]{3, 2},
                        new float[]{3, 3},
                };
        final float[][] expectedOutputArrArr = new float[][]
                {
                        new float[]{0},

                        new float[]{0},
                        new float[]{1},
                        new float[]{2},
                        new float[]{3},

                        new float[]{1},
                        new float[]{2},
                        new float[]{3},
                        new float[]{4},

                        new float[]{2},
                        new float[]{3},
                        new float[]{4},
                        new float[]{5},

                        new float[]{3},
                        new float[]{4},
                        new float[]{5},
                        new float[]{6},
                };
        return new Result(trainInputArrArr, expectedOutputArrArr);
    }

    private static Result arrangeSubResult() {
        final float[][] trainInputArrArr = new float[][]
                {
                        new float[]{0, 0},

                        new float[]{0, 0},
                        new float[]{0, 1},
                        new float[]{0, 2},
                        new float[]{0, 3},

                        new float[]{1, 0},
                        new float[]{1, 1},
                        new float[]{1, 2},
                        new float[]{1, 3},

                        new float[]{2, 0},
                        new float[]{2, 1},
                        new float[]{2, 2},
                        new float[]{2, 3},

                        new float[]{3, 0},
                        new float[]{3, 1},
                        new float[]{3, 2},
                        new float[]{3, 3},
                };
        final float[][] expectedOutputArrArr = new float[][]
                {
                        new float[]{0},

                        new float[]{0},
                        new float[]{-1},
                        new float[]{-2},
                        new float[]{-3},

                        new float[]{1},
                        new float[]{0},
                        new float[]{-1},
                        new float[]{-2},

                        new float[]{2},
                        new float[]{1},
                        new float[]{0},
                        new float[]{-1},

                        new float[]{3},
                        new float[]{2},
                        new float[]{1},
                        new float[]{0},
                };
        return new Result(trainInputArrArr, expectedOutputArrArr);
    }

    private static Result arrangeMultResult() {
        final float[][] trainInputArrArr = new float[][]
                {
                        new float[]{0, 0},

                        new float[]{0, 0},
                        new float[]{0, 1},
                        new float[]{0, 2},
                        new float[]{0, 3},

                        new float[]{1, 0},
                        new float[]{1, 1},
                        new float[]{1, 2},
                        new float[]{1, 3},

                        new float[]{2, 0},
                        new float[]{2, 1},
                        new float[]{2, 2},
                        new float[]{2, 3},

                        new float[]{3, 0},
                        new float[]{3, 1},
                        new float[]{3, 2},
                        new float[]{3, 3},
                };
        final float[][] expectedOutputArrArr = new float[][]
                {
                        new float[]{0},

                        new float[]{0},
                        new float[]{0},
                        new float[]{0},
                        new float[]{0},

                        new float[]{0},
                        new float[]{1},
                        new float[]{2},
                        new float[]{3},

                        new float[]{0},
                        new float[]{2},
                        new float[]{4},
                        new float[]{6},

                        new float[]{0},
                        new float[]{3},
                        new float[]{6},
                        new float[]{9},
                };
        return new Result(trainInputArrArr, expectedOutputArrArr);
    }
}
