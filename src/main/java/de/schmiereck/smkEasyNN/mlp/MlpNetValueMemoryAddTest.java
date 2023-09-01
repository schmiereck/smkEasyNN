package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpLayer.calcInitWeight;
import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.actAssertExpectedOutput;
import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.printResult;
import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.printResultForEpoch;
import static de.schmiereck.smkEasyNN.mlp.MlpService.runTrainRandom;
import static de.schmiereck.smkEasyNN.mlp.MlpService.runTrainRandomOrder;

import java.util.Arrays;
import java.util.Random;

import org.junit.jupiter.api.Test;

public class MlpNetValueMemoryAddTest {

    @Test
    void GIVEN_7_value_inputs_THEN_value_output_after_bottleneck() {
        // Arrange
        final float[][][] trainInputArrArrArr = new float[][][]
                {
                        /*
                        {
                        new float[]{ 0, 0,  0, 0, 0, 0, 0, 0, 0 },
                        },
                        {
                        new float[]{ 0, 0,  0, 0, 0, 0, 0, 0, 1 },
                        },
                        {
                        new float[]{ 0, 0,  0, 0, 0, 0, 0, 1, 0 },
                        },
                        {
                        new float[]{ 0, 0,  0, 0, 0, 0, 1, 0, 0 },
                        },

                        {
                        new float[]{ 0, 0,  0, 0, 0, 1, 0, 0, 0 },
                        },
                        {
                        new float[]{ 0, 0,  0, 0, 1, 0, 0, 0, 0 },
                        },
                        {
                        new float[]{ 0, 0,  0, 1, 0, 0, 0, 0, 0 },
                        },
                        {
                        new float[]{ 0, 0,  1, 0, 0, 0, 0, 0, 0 },
                        },

                        {
                        new float[]{ 0, 0,  1, 0, 0, 0, 0, 0, 0 },
                        },
                        {
                        new float[]{ 0, 0,  1, 0, 0, 0, 0, 0, 0 },
                        },

                        //               7  6  5  4  3  2  1
                        {
                        new float[]{ 0, 0,  0, 0, 0, 0, 0, 1, 1 }, // 1 + 2 = 3
                        },
                        {
                        new float[]{ 0, 0,  0, 0, 0, 0, 1, 0, 1 }, // 1 + 3 = 4
                        },
                        {
                        new float[]{ 0, 0,  0, 0, 0, 1, 0, 0, 1 }, // 1 + 4 = 5
                        },
                        {
                        new float[]{ 0, 0,  0, 0, 0, 1, 0, 0, 1 }, // 1 + 5 = 6
                        },
                        {
                        new float[]{ 0, 0,  0, 0, 0, 1, 0, 1, 0 }, // 2 + 4 = 6
                        },
                        {
                        new float[]{ 0, 0,  0, 0, 1, 0, 0, 1, 0 }, // 2 + 5 = 7
                        },
                        {
                        new float[]{ 0, 0,  0, 1, 0, 0, 0, 0, 1 }, // 1 + 6 = 7
                        },
                        {
                        new float[]{ 0, 0,  0, 0, 0, 0, 1, 1, 0 }, // 2 + 3 = 5
                        },
                        {
                        new float[]{ 0, 0,  0, 0, 0, 1, 1, 0, 0 }, // 3 + 4 = 7
                        },
                        */
                        //                       7  6  5  4  3  2  1
                        {
                                new float[]{ 0, 1,  0, 0, 0, 0, 0, 0, 1 }, // 1 + 2 = 3
                                new float[]{ 1, 0,  0, 0, 0, 0, 0, 1, 0 }, // 1 + 2 = 3
                        },
                        {
                                new float[]{ 0, 1,  0, 0, 0, 0, 0, 0, 1 }, // 1 + 3 = 4
                                new float[]{ 1, 0,  0, 0, 0, 0, 1, 0, 0 }, // 1 + 3 = 4
                        },
                        {
                                new float[]{ 0, 1,  0, 0, 0, 0, 0, 0, 1 }, // 1 + 4 = 5
                                new float[]{ 1, 0,  0, 0, 0, 1, 0, 0, 0 }, // 1 + 4 = 5
                        },
                        {
                                new float[]{ 0, 1,  0, 0, 0, 0, 0, 0, 1 }, // 1 + 5 = 6
                                new float[]{ 1, 0,  0, 0, 1, 0, 0, 0, 0 }, // 1 + 5 = 6
                        },
                        {
                                new float[]{ 0, 1,  0, 0, 0, 0, 0, 1, 0 }, // 2 + 4 = 6
                                new float[]{ 1, 0,  0, 0, 0, 1, 0, 0, 0 }, // 2 + 4 = 6
                        },
                        {
                                new float[]{ 0, 1,  0, 0, 0, 0, 0, 1, 0 }, // 2 + 5 = 7
                                new float[]{ 1, 0,  0, 0, 1, 0, 0, 0, 0 }, // 2 + 5 = 7
                        },
                        {
                                new float[]{ 0, 1,  0, 0, 0, 0, 0, 0, 1 }, // 1 + 6 = 7
                                new float[]{ 1, 0,  0, 1, 0, 0, 0, 0, 0 }, // 1 + 6 = 7
                        },
                        {
                                new float[]{ 0, 1,  0, 0, 0, 0, 0, 1, 0 }, // 2 + 3 = 5
                                new float[]{ 1, 0,  0, 0, 0, 0, 1, 0, 0 }, // 2 + 3 = 5
                        },
                        {
                                new float[]{ 0, 1,  0, 0, 0, 0, 1, 0, 0 }, // 3 + 4 = 7
                                new float[]{ 1, 0,  0, 0, 0, 1, 0, 0, 0 }, // 3 + 4 = 7
                        },
                };
        final float[][][] expectedOutputArrArrArr = new float[][][]
                {
                        /*
                        {
                        new float[]{ 0, 0, 0, 0, 0, 0, 0 },
                        },
                        {
                        new float[]{ 0, 0, 0, 0, 0, 0, 1 },
                        },
                        {
                        new float[]{ 0, 0, 0, 0, 0, 1, 0 },
                        },
                        {
                        new float[]{ 0, 0, 0, 0, 1, 0, 0 },
                        },

                        {
                        new float[]{ 0, 0, 0, 1, 0, 0, 0 },
                        },
                        {
                        new float[]{ 0, 0, 1, 0, 0, 0, 0 },
                        },
                        {
                        new float[]{ 0, 1, 0, 0, 0, 0, 0 },
                        },
                        {
                        new float[]{ 1, 0, 0, 0, 0, 0, 0 },
                        },

                        {
                        new float[]{ 1, 0, 0, 0, 0, 0, 0 },
                        },
                        {
                        new float[]{ 1, 0, 0, 0, 0, 0, 0 },
                        },

                        //           7  6  5  4  3  2  1
                        {
                        new float[]{ 0, 0, 0, 0, 0, 1, 1 }, // 1 + 2 = 3
                        },
                        {
                        new float[]{ 0, 0, 0, 0, 1, 0, 1 }, // 1 + 3 = 4
                        },
                        {
                        new float[]{ 0, 0, 0, 1, 0, 0, 1 }, // 1 + 4 = 5
                        },
                        {
                        new float[]{ 0, 0, 0, 1, 0, 0, 1 }, // 1 + 5 = 6
                        },
                        {
                        new float[]{ 0, 0, 0, 1, 0, 1, 0 }, // 2 + 4 = 6
                        },
                        {
                        new float[]{ 0, 0, 1, 0, 0, 1, 0 }, // 2 + 5 = 7
                        },
                        {
                        new float[]{ 0, 1, 0, 0, 0, 0, 1 }, // 1 + 6 = 7
                        },
                        {
                        new float[]{ 0, 0, 0, 0, 1, 1, 0 }, // 2 + 3 = 5
                        },
                        {
                        new float[]{ 0, 0, 0, 1, 1, 0, 0 }, // 3 + 4 = 7
                        },
                        */
                        //                       7  6  5  4  3  2  1
                        {
                                new float[]{ 0, 0, 0, 0, 0, 0, 1 }, // 1 + 2 = 3
                                new float[]{ 0, 0, 0, 0, 0, 1, 1 }, // 1 + 2 = 3
                        },
                        {
                                new float[]{ 0, 0, 0, 0, 0, 0, 1 }, // 1 + 3 = 4
                                new float[]{ 0, 0, 0, 0, 1, 0, 1 }, // 1 + 3 = 4
                        },
                        {
                                new float[]{ 0, 0, 0, 0, 0, 0, 1 }, // 1 + 4 = 5
                                new float[]{ 0, 0, 0, 1, 0, 0, 1 }, // 1 + 4 = 5
                        },
                        {
                                new float[]{ 0, 0, 0, 0, 0, 0, 1 }, // 1 + 5 = 6
                                new float[]{ 0, 0, 1, 0, 0, 0, 1 }, // 1 + 5 = 6
                        },
                        {
                                new float[]{ 0, 0, 0, 0, 0, 1, 0 }, // 2 + 4 = 6
                                new float[]{ 0, 0, 0, 1, 0, 1, 0 }, // 2 + 4 = 6
                        },
                        {
                                new float[]{ 0, 0, 0, 0, 0, 1, 0 }, // 2 + 5 = 7
                                new float[]{ 0, 0, 1, 0, 0, 1, 0 }, // 2 + 5 = 7
                        },
                        {
                                new float[]{ 0, 0, 0, 0, 0, 0, 1 }, // 1 + 6 = 7
                                new float[]{ 0, 1, 0, 0, 0, 0, 1 }, // 1 + 6 = 7
                        },
                        {
                                new float[]{ 0, 0, 0, 0, 0, 1, 0 }, // 2 + 3 = 5
                                new float[]{ 0, 0, 0, 0, 1, 1, 0 }, // 2 + 3 = 5
                        },
                        {
                                new float[]{ 0, 0, 0, 0, 1, 0, 0 }, // 3 + 4 = 7
                                new float[]{ 0, 0, 0, 1, 1, 0, 0 }, // 3 + 4 = 7
                        },
                };
        final int[] layerSizeArr = new int[]{ 9, 18, 20, 18, 12, 7 };

        final Random rnd = new Random(123456);
        //final Random rnd = new Random();

        final MlpNet mlpNet = new MlpNet(layerSizeArr, true, rnd);

        // 0
        // 1 dest <---,
        // 2 source --'
        addBackwardInputs(mlpNet, 2, 1, rnd);
        addBackwardInputs(mlpNet, 3, 2, rnd);
        addBackwardInputs(mlpNet, 4, 2, rnd);
        addBackwardInputs(mlpNet, 4, 3, rnd);

        final int epochMax = 50000;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            runTrainRandomOrder(mlpNet, expectedOutputArrArrArr, trainInputArrArrArr, rnd);

            if ((epochPos + 1) % 100 == 0) {
                printResultForEpoch(mlpNet, trainInputArrArrArr, expectedOutputArrArrArr, epochPos);
            }
        }

        // Act & Assert
        actAssertExpectedOutput(mlpNet, trainInputArrArrArr, expectedOutputArrArrArr, 0.7F);
    }

    private void addBackwardInputs(final MlpNet mlpNet, final int sourceLayerPos, final int destLayerPos, final Random rnd) {
        final MlpLayer sourceLayer = mlpNet.getLayer(sourceLayerPos);
        final MlpLayer destLayer = mlpNet.getLayer(destLayerPos);

        Arrays.stream(sourceLayer.neuronArr).forEach(sourceNeuron -> {
            Arrays.stream(destLayer.neuronArr).forEach(destNeuron -> {
                final MlpSynapse synapse = new MlpSynapse();
                synapse.input = new MlpInput(sourceNeuron);
                synapse.weight = calcInitWeight(rnd);
                destNeuron.synapseList.add(synapse);
            });
        });
    }
}
