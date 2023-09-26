package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpNetPrintUtils.printFullResultForEpoch;
import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.actAssertExpectedOutput;
import static de.schmiereck.smkEasyNN.mlp.MlpLayerService.addForwwardInputs;
import static de.schmiereck.smkEasyNN.mlp.MlpService.runTrainRandomOrder;

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
        final int[] layerSizeArr = new int[]{ 9, 6, 6, 7 };

        final Random rnd = new Random(123456);
        //final Random rnd = new Random();

        final MlpNet mlpNet = MlpNetService.createNet(layerSizeArr, true, rnd);

        // 0
        // 1 to   <---,
        // 2 from ----'
        addForwwardInputs(mlpNet, 2, 1, true, false, true, false, true, rnd);
        //addForwwardInputs(mlpNet, 1, 0, rnd);

        final int epochMax = 13_000;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            final float mainOutputMseErrorValue = runTrainRandomOrder(mlpNet, expectedOutputArrArrArr, trainInputArrArrArr, 0.1F, 0.6F, rnd);

            if ((epochPos + 1) % 100 == 0) {
                MlpNetPrintUtils.printFullResultForEpoch(mlpNet, trainInputArrArrArr, expectedOutputArrArrArr, epochPos, mainOutputMseErrorValue);
            }
        }

        // Act & Assert
        actAssertExpectedOutput(mlpNet, trainInputArrArrArr, expectedOutputArrArrArr, 0.1F);
    }
}
