package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpLayer.calcInitWeight;
import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.actAssertExpectedOutput;
import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.printResultForEpoch;
import static de.schmiereck.smkEasyNN.mlp.MlpService.addForwwardInputs;
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
        final int[] layerSizeArr = new int[]{ 9, 24, 24, 24, 24, 24, 7 };

        final Random rnd = new Random(123456);
        //final Random rnd = new Random();

        final MlpNet mlpNet = new MlpNet(layerSizeArr, true, rnd);

        // 0
        // 1 to   <---,
        // 2 from ----'
        addForwwardInputs(mlpNet, 2, 1, rnd);
        addForwwardInputs(mlpNet, 3, 2, rnd);
        addForwwardInputs(mlpNet, 4, 2, rnd);
        addForwwardInputs(mlpNet, 4, 3, rnd);
        addForwwardInputs(mlpNet, 5, 4, rnd);

        final int epochMax = 5000000;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            runTrainRandomOrder(mlpNet, expectedOutputArrArrArr, trainInputArrArrArr, rnd);

            if ((epochPos + 1) % 100 == 0) {
                printResultForEpoch(mlpNet, trainInputArrArrArr, expectedOutputArrArrArr, epochPos);
            }
        }

        // Act & Assert
        actAssertExpectedOutput(mlpNet, trainInputArrArrArr, expectedOutputArrArrArr, 0.7F);
    }
}
