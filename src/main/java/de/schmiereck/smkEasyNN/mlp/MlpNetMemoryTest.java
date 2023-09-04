package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.actAssertExpectedOutput;
import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.printResultForEpoch;
import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.printSamplesOutput;
import static de.schmiereck.smkEasyNN.mlp.MlpService.addForwwardInputs;
import static de.schmiereck.smkEasyNN.mlp.MlpService.addInternalInputs;
import static de.schmiereck.smkEasyNN.mlp.MlpService.runTrainRandomOrder;

import java.util.Random;

import org.junit.jupiter.api.Test;

public class MlpNetMemoryTest {

    @Test
    void GIVEN_1_input_bits_in_sequence_THEN_output_is_input_in_sequence_before() {
        // Arrange
        final float[][][] trainInputArrArrArr = new float[][][]
                {
                        //                          1
                        {
                                new float[]{ 0 }, //
                                new float[]{ 0 }, //
                                new float[]{ 0 }, //
                                new float[]{ 0 }, // = 0, 0, 0
                        },
                        {
                                new float[]{ 0 }, //
                                new float[]{ 1 }, //
                                new float[]{ 0 }, //
                                new float[]{ 0 }, // = 1, 0, 0
                        },
                        {
                                new float[]{ 0 }, //
                                new float[]{ 0 }, //
                                new float[]{ 1 }, //
                                new float[]{ 0 }, // = 0, 1, 0
                        },
                        {
                                new float[]{ 0 }, //
                                new float[]{ 1 }, //
                                new float[]{ 1 }, //
                                new float[]{ 0 }, // = 1, 1, 0
                        },
                        {
                                new float[]{ 0 }, //
                                new float[]{ 0 }, //
                                new float[]{ 0 }, //
                                new float[]{ 1 }, //
                                new float[]{ 0 }, //
                                new float[]{ 0 }, // = 0, 1, 0
                        },
                        {
                                new float[]{ 0 }, //
                                new float[]{ 0 }, //
                                new float[]{ 1 }, //
                                new float[]{ 1 }, //
                                new float[]{ 0 }, //
                                new float[]{ 0 }, // = 0, 1, 0
                        },
                };
        final float[][][] expectedOutputArrArrArr = new float[][][]
                {
                        //                   1
                        {
                                new float[]{ 0 }, //
                                new float[]{ 0 }, //
                                new float[]{ 0 }, //
                                new float[]{ 0 }, // = 0, 0, 0
                        },
                        {
                                new float[]{ 0 }, //
                                new float[]{ 0 }, //
                                new float[]{ 1 }, //
                                new float[]{ 0 }, // = 1, 0, 0
                        },
                        {
                                new float[]{ 0 }, //
                                new float[]{ 0 }, //
                                new float[]{ 0 }, //
                                new float[]{ 1 }, // = 0, 1, 0
                        },
                        {
                                new float[]{ 0 }, //
                                new float[]{ 0 }, //
                                new float[]{ 1 }, //
                                new float[]{ 1 }, // = 1, 1, 0
                        },
                        {
                                new float[]{ 0 }, //
                                new float[]{ 0 }, //
                                new float[]{ 0 }, //
                                new float[]{ 0 }, //
                                new float[]{ 1 }, //
                                new float[]{ 0 }, // = 0, 1, 0
                        },
                        {
                                new float[]{ 0 }, //
                                new float[]{ 0 }, //
                                new float[]{ 0 }, //
                                new float[]{ 1 }, //
                                new float[]{ 1 }, //
                                new float[]{ 0 }, // = 0, 1, 0
                        },
                };
        final int[] layerSizeArr = new int[]{ 1, 4, 8, 1 };

        final Random rnd = new Random(123456);
        //final Random rnd = new Random();

        final MlpNet mlpNet = new MlpNet(layerSizeArr, true, rnd);

        // 0
        // 1 to   <---,
        // 2 from ----'
        addForwwardInputs(mlpNet, 2, 1, rnd);
        //addInternalInputs(mlpNet, 1, rnd);

        final int epochMax = 26_000;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            runTrainRandomOrder(mlpNet, expectedOutputArrArrArr, trainInputArrArrArr, rnd);

            if ((epochPos + 1) % 100 == 0) {
                printResultForEpoch(mlpNet, trainInputArrArrArr, expectedOutputArrArrArr, epochPos, 1);
            }
        }

        // Act & Assert
        actAssertExpectedOutput(mlpNet, trainInputArrArrArr, expectedOutputArrArrArr, 0.05F);
    }

    @Test
    void GIVEN_1_input_bits_in_sequence_THEN_combine_inputs_with_And() {
        // Arrange
        final float[][][] trainInputArrArrArr = new float[][][]
                {
                        //                          1
                        {
                                new float[]{ 0, 1,  0 }, //
                                new float[]{ 1, 0,  0 }, // = 0 & 0
                        },
                        {
                                new float[]{ 0, 1,  1 }, //
                                new float[]{ 1, 0,  0 }, // = 1 & 0
                        },
                        {
                                new float[]{ 0, 1,  0 }, //
                                new float[]{ 1, 0,  1 }, // = 0 & 1
                        },
                        {
                                new float[]{ 0, 1,  1 }, //
                                new float[]{ 1, 0,  1 }, // = 1 & 1
                        },
                };
        final float[][][] expectedOutputArrArrArr = new float[][][]
                {
                        //                   1
                        {
                                new float[]{ 0 }, //
                                new float[]{ 0 }, // = 0 & 0
                        },
                        {
                                new float[]{ 1 }, //
                                new float[]{ 0 }, // = 1 & 0
                        },
                        {
                                new float[]{ 0 }, //
                                new float[]{ 0 }, // = 0 & 1
                        },
                        {
                                new float[]{ 1 }, //
                                new float[]{ 1 }, // = 1 & 1
                        },
                };
        final int[] layerSizeArr = new int[]{ 3, 6, 6, 6, 6, 1 };

        final Random rnd = new Random(123456);
        //final Random rnd = new Random();

        final MlpNet mlpNet = new MlpNet(layerSizeArr, true, rnd);

        // 0
        // 1 to   <---,
        // 2 from ----'
        //addForwwardInputs(mlpNet, 1, 1, rnd);
        addForwwardInputs(mlpNet, 2, 1, rnd);
        //addForwwardInputs(mlpNet, 2, 2, rnd);
        //addForwwardInputs(mlpNet, 3, 2, rnd);
        //addInternalInputs(mlpNet, 1, rnd);

        final int epochMax = 20_000;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            runTrainRandomOrder(mlpNet, expectedOutputArrArrArr, trainInputArrArrArr, rnd);

            if ((epochPos + 1) % 100 == 0) {
                printResultForEpoch(mlpNet, trainInputArrArrArr, expectedOutputArrArrArr, epochPos);
            }
        }

        // Act & Assert
        actAssertExpectedOutput(mlpNet, trainInputArrArrArr, expectedOutputArrArrArr, 0.05F);
    }
}
