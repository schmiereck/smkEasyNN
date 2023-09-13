package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpNetPrintUtils.printResultForEpoch;
import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.actAssertExpectedOutput;
import static de.schmiereck.smkEasyNN.mlp.MlpLayerService.addForwwardInputs;
import static de.schmiereck.smkEasyNN.mlp.MlpService.runTrainRandomOrder;

import java.util.Random;

import org.junit.jupiter.api.Test;

public class MlpNetMemorySerializerTest {

    @Test
    void GIVEN_digital_input_THEN_output_serialized_digit_1_memory_1() {
        // Arrange
        final float[][][] trainInputArrArrArr = new float[][][]
                {
                        {
                                new float[]{ 0,  0, 0, 0 }, // 000
                                new float[]{ 1,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0,  1, 1, 1 }, // 001
                                new float[]{ 1,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0,  0, 0, 0 }, // 010
                                new float[]{ 1,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0,  1, 1, 1 }, // 011
                                new float[]{ 1,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0,  0, 0, 0 }, // 100
                                new float[]{ 1,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0,  1, 1, 1 }, // 101
                                new float[]{ 1,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0,  0, 0, 0 }, // 110
                                new float[]{ 1,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0,  1, 1, 1 }, // 111
                                new float[]{ 1,  0, 0, 0 },
                        },
                };
        final float[][][] expectedOutputArrArrArr = new float[][][]
                {
                        {
                                new float[]{ 0 }, // 000
                                new float[]{ 0 },
                        },
                        {
                                new float[]{ 0 }, // 001
                                new float[]{ 1 },
                        },
                        {
                                new float[]{ 0 }, // 010
                                new float[]{ 0 },
                        },
                        {
                                new float[]{ 0 }, // 011
                                new float[]{ 1 },
                        },
                        {
                                new float[]{ 0 }, // 100
                                new float[]{ 0 },
                        },
                        {
                                new float[]{ 0 }, // 101
                                new float[]{ 1 },
                        },
                        {
                                new float[]{ 0 }, // 110
                                new float[]{ 0 },
                        },
                        {
                                new float[]{ 0 }, // 111
                                new float[]{ 1 },
                        },
                };
        final int[] layerSizeArr = new int[]{ 1+3,  20, 20, 10, 10, 10,  1 };

        final Random rnd = new Random(123456);
        //final Random rnd = new Random();

        final MlpNet mlpNet = MlpNetService.createNet(layerSizeArr, true, true, rnd);

        //addForwwardInputs(mlpNet, 3, 3, rnd);
        //addForwwardInputs(mlpNet, 4, 4, rnd);
        //addForwwardInputs(mlpNet, 5, 5, rnd);

        //addForwwardInputs(mlpNet, 3, 2, rnd);
        //addForwwardInputs(mlpNet, 4, 2, rnd);
        //addForwwardInputs(mlpNet, 5, 2, rnd);

        //addForwwardInputs(mlpNet, 2, 1, rnd);
        addForwwardInputs(mlpNet, 4, 2, rnd);
        //addForwwardInputs(mlpNet, 5, 2, rnd);
        //addForwwardInputs(mlpNet, 4, 3, rnd);
        //addForwwardInputs(mlpNet, 5, 3, rnd);
        //addForwwardInputs(mlpNet, 6, 3, rnd);
        //addForwwardInputs(mlpNet, 5, 3, rnd);

        final int epochMax = 60_000;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            runTrainRandomOrder(mlpNet, expectedOutputArrArrArr, trainInputArrArrArr, 0.3F, 0.6F, rnd);

            if ((epochPos + 1) % 100 == 0) {
                printResultForEpoch(mlpNet, trainInputArrArrArr, expectedOutputArrArrArr, epochPos);
            }
        }

        // Act & Assert
        actAssertExpectedOutput(mlpNet, trainInputArrArrArr, expectedOutputArrArrArr, 0.05F);
    }

    @Test
    void GIVEN_digital_input_THEN_output_serialized_digit_1_memory_2() {
        // Arrange
        final float[][][] trainInputArrArrArr = new float[][][]
                {
                        {
                                new float[]{ 0,  0, 0, 0 }, // 000
                                new float[]{ 1,  0, 0, 0 },
                                new float[]{ 1,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0,  1, 1, 1 }, // 001
                                new float[]{ 1,  0, 0, 0 },
                                new float[]{ 1,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0,  0, 0, 0 }, // 010
                                new float[]{ 1,  0, 0, 0 },
                                new float[]{ 1,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0,  1, 1, 1 }, // 011
                                new float[]{ 1,  0, 0, 0 },
                                new float[]{ 1,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0,  0, 0, 0 }, // 100
                                new float[]{ 1,  0, 0, 0 },
                                new float[]{ 1,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0,  1, 1, 1 }, // 101
                                new float[]{ 1,  0, 0, 0 },
                                new float[]{ 1,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0,  0, 0, 0 }, // 110
                                new float[]{ 1,  0, 0, 0 },
                                new float[]{ 1,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0,  1, 1, 1 }, // 111
                                new float[]{ 1,  0, 0, 0 },
                                new float[]{ 1,  0, 0, 0 },
                        },
                };
        final float[][][] expectedOutputArrArrArr = new float[][][]
                {
                        {
                                new float[]{ 0 }, // 000
                                new float[]{ 0 },
                                new float[]{ 0 },
                        },
                        {
                                new float[]{ 1 }, // 001
                                new float[]{ 1 },
                                new float[]{ 1 },
                        },
                        {
                                new float[]{ 0 }, // 010
                                new float[]{ 0 },
                                new float[]{ 0 },
                        },
                        {
                                new float[]{ 1 }, // 011
                                new float[]{ 1 },
                                new float[]{ 1 },
                        },
                        {
                                new float[]{ 0 }, // 100
                                new float[]{ 0 },
                                new float[]{ 0 },
                        },
                        {
                                new float[]{ 1 }, // 101
                                new float[]{ 1 },
                                new float[]{ 1 },
                        },
                        {
                                new float[]{ 0 }, // 110
                                new float[]{ 0 },
                                new float[]{ 0 },
                        },
                        {
                                new float[]{ 1 }, // 111
                                new float[]{ 1 },
                                new float[]{ 1 },
                        },
                };
        final int[] layerSizeArr = new int[]{ 1+3,  20, 20, 10, 10, 10, 20, 20,  1 };

        final Random rnd = new Random(123456);
        //final Random rnd = new Random();

        final MlpConfiguration configuration = new MlpConfiguration(true, false, 3.0F);
        final MlpNet mlpNet = MlpNetService.createNet(configuration, layerSizeArr, rnd);

        //addForwwardInputs(mlpNet, 3, 3, rnd);
        //addForwwardInputs(mlpNet, 4, 4, rnd);
        //addForwwardInputs(mlpNet, 5, 5, rnd);

        //addForwwardInputs(mlpNet, 3, 2, rnd);
        //addForwwardInputs(mlpNet, 4, 2, rnd);
        //addForwwardInputs(mlpNet, 5, 2, rnd);

        //addForwwardInputs(mlpNet, 2, 1, rnd);
        addForwwardInputs(mlpNet, 5, 4, rnd);
        addForwwardInputs(mlpNet, 6, 4, rnd);
        //addForwwardInputs(mlpNet, 5, 2, rnd);
        //addForwwardInputs(mlpNet, 4, 3, rnd);
        //addForwwardInputs(mlpNet, 5, 3, rnd);
        //addForwwardInputs(mlpNet, 6, 3, rnd);
        //addForwwardInputs(mlpNet, 5, 3, rnd);

        final int epochMax = 50_000;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            runTrainRandomOrder(mlpNet, expectedOutputArrArrArr, trainInputArrArrArr, 0.3F, 0.6F, rnd);

            if ((epochPos + 1) % 100 == 0) {
                printResultForEpoch(mlpNet, trainInputArrArrArr, expectedOutputArrArrArr, epochPos);
            }
        }

        // Act & Assert
        actAssertExpectedOutput(mlpNet, trainInputArrArrArr, expectedOutputArrArrArr, 0.05F);
    }

    //@Test
    void GIVEN_digital_input_THEN_output_serialized_digits() {
        // Arrange
        final float[][][] trainInputArrArrArr = new float[][][]
                {
                        {
                                new float[]{ 0, 1,  0, 0, 0 }, // 000
                                new float[]{ 1, 0,  0, 0, 0 },
                                new float[]{ 1, 0,  0, 0, 0 },
                                new float[]{ 1, 0,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0, 1,  0, 0, 1 }, // 001
                                new float[]{ 1, 0,  0, 0, 0 },
                                new float[]{ 1, 0,  0, 0, 0 },
                                new float[]{ 1, 0,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0, 1,  0, 1, 0 }, // 010
                                new float[]{ 1, 0,  0, 0, 0 },
                                new float[]{ 1, 0,  0, 0, 0 },
                                new float[]{ 1, 0,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0, 1,  0, 1, 1 }, // 011
                                new float[]{ 1, 0,  0, 0, 0 },
                                new float[]{ 1, 0,  0, 0, 0 },
                                new float[]{ 1, 0,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0, 1,  1, 0, 0 }, // 100
                                new float[]{ 1, 0,  0, 0, 0 },
                                new float[]{ 1, 0,  0, 0, 0 },
                                new float[]{ 1, 0,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0, 1,  1, 0, 1 }, // 101
                                new float[]{ 1, 0,  0, 0, 0 },
                                new float[]{ 1, 0,  0, 0, 0 },
                                new float[]{ 1, 0,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0, 1,  1, 1, 0 }, // 110
                                new float[]{ 1, 0,  0, 0, 0 },
                                new float[]{ 1, 0,  0, 0, 0 },
                                new float[]{ 1, 0,  0, 0, 0 },
                        },
                        {
                                new float[]{ 0, 1,  1, 1, 1 }, // 111
                                new float[]{ 1, 0,  0, 0, 0 },
                                new float[]{ 1, 0,  0, 0, 0 },
                                new float[]{ 1, 0,  0, 0, 0 },
                        },
                };
        final float[][][] expectedOutputArrArrArr = new float[][][]
                {
                        {
                                new float[]{ 0 }, // 000
                                new float[]{ 0 },
                                new float[]{ 0 },
                                new float[]{ 0 },
                        },
                        {
                                new float[]{ 1 }, // 001
                                new float[]{ 1 },
                                new float[]{ 0 },
                                new float[]{ 0 },
                        },
                        {
                                new float[]{ 0 }, // 010
                                new float[]{ 0 },
                                new float[]{ 1 },
                                new float[]{ 0 },
                        },
                        {
                                new float[]{ 1 }, // 011
                                new float[]{ 1 },
                                new float[]{ 1 },
                                new float[]{ 0 },
                        },
                        {
                                new float[]{ 0 }, // 100
                                new float[]{ 0 },
                                new float[]{ 0 },
                                new float[]{ 1 },
                        },
                        {
                                new float[]{ 1 }, // 101
                                new float[]{ 1 },
                                new float[]{ 0 },
                                new float[]{ 1 },
                        },
                        {
                                new float[]{ 0 }, // 110
                                new float[]{ 0 },
                                new float[]{ 1 },
                                new float[]{ 1 },
                        },
                        {
                                new float[]{ 1 }, // 111
                                new float[]{ 1 },
                                new float[]{ 1 },
                                new float[]{ 1 },
                        },
                };
        final int[] layerSizeArr = new int[]{ 2+3, 12*2, 12, 12, 12, 12*2, 12, 12, 12, 12*2, 12, 12, 12, 1 };

        final Random rnd = new Random(123456);
        //final Random rnd = new Random();

        final MlpConfiguration configuration = new MlpConfiguration(true, false, 3.0F);
        final MlpNet mlpNet = MlpNetService.createNet(configuration, layerSizeArr, rnd);

        addForwwardInputs(mlpNet, 12, 1, rnd);
        addForwwardInputs(mlpNet, 2, 1, rnd);

        addForwwardInputs(mlpNet, 13, 5, rnd);
        addForwwardInputs(mlpNet, 6, 5, rnd);

        addForwwardInputs(mlpNet, 13, 9, rnd);
        //addForwwardInputs(mlpNet, 10, 9, rnd);

        final int epochMax = 100_000;
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
