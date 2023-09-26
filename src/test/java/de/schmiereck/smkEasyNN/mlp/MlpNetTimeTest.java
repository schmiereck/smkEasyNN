package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpNetPrintUtils.printFullResultForEpoch;
import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.actAssertExpectedOutput;
import static de.schmiereck.smkEasyNN.mlp.MlpLayerService.addForwwardInputs;
import static de.schmiereck.smkEasyNN.mlp.MlpService.runTrainRandomOrder;

import java.util.Random;

import org.junit.jupiter.api.Test;

public class MlpNetTimeTest {

    @Test
    void GIVEN_0_input_bits_THEN_output_is_0_1_in_sequence() {
        // Arrange
        final float[][][] trainInputArrArrArr = new float[][][]
                {
                        //                   1
                        {
                                new float[]{ .2F }, //
                                new float[]{ 0 }, //
                                new float[]{ 0 }, //
                                new float[]{ 0 }, //
                                new float[]{ 0 }, //
                                new float[]{ 0 }, //
                                new float[]{ 0 }, //
                                new float[]{ 0 }, // = 0, 0, 0, 0
                        },
                };
        final float[][][] expectedOutputArrArrArr = new float[][][]
                {
                        //                   1
                        {
                                new float[]{ 0 }, //
                                new float[]{ 1 }, //
                                new float[]{ 0 }, //
                                new float[]{ 1 }, //
                                new float[]{ 0 }, //
                                new float[]{ 1 }, //
                                new float[]{ 0 }, //
                                new float[]{ 1 }, // = 0, 1, 0, 1
                        },
                };
        final int[] layerSizeArr = new int[]{ 1, 4, 7, 1 };

        final Random rnd = new Random(123456);
        //final Random rnd = new Random();

        final MlpNet mlpNet = MlpNetService.createNet(layerSizeArr, true, false, rnd);

        // 0
        // 1 to   <---,
        // 2 from ----'
        addForwwardInputs(mlpNet, 2, 1, true, false, true, true, true, rnd);
        //addForwwardInputs(mlpNet, 3, 1, rnd);
        //addForwwardInputs(mlpNet, 3, 2, rnd);
        //addInternalInputs(mlpNet, 2, rnd);

        final int epochMax = 7_500;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            final float mainOutputMseErrorValue = runTrainRandomOrder(mlpNet, expectedOutputArrArrArr, trainInputArrArrArr, rnd);

            if ((epochPos + 1) % 100 == 0) {
                MlpNetPrintUtils.printFullResultForEpoch(mlpNet, trainInputArrArrArr, expectedOutputArrArrArr, epochPos, mainOutputMseErrorValue);
            }
        }

        // Act & Assert
        MlpNetTestUtils.actAssertExpectedOutput(mlpNet, trainInputArrArrArr, expectedOutputArrArrArr, 0.05F);
    }

    @Test
    void GIVEN_0_input_bits_and_clock_input_THEN_output_is_0_1_in_sequence() {
        // Arrange
        final float[][][] trainInputArrArrArr = new float[][][]
                {
                        //                   1
                        {
                                new float[]{ 0 }, //
                                new float[]{ 0 }, //
                                new float[]{ 0 }, //
                                new float[]{ 0 }, //
                                new float[]{ 0 }, //
                                new float[]{ 0 }, //
                                new float[]{ 0 }, //
                                new float[]{ 0 }, // = 0, 0, 0, 0
                        },
                };
        final float[][][] expectedOutputArrArrArr = new float[][][]
                {
                        //                   1
                        {
                                new float[]{ 0 }, //
                                new float[]{ 1 }, //
                                new float[]{ 0 }, //
                                new float[]{ 1 }, //
                                new float[]{ 0 }, //
                                new float[]{ 1 }, //
                                new float[]{ 0 }, //
                                new float[]{ 1 }, // = 0, 1, 0, 1
                        },
                };
        final int[] layerSizeArr = new int[]{ 1 };

        final Random rnd = new Random(123456);
        //final Random rnd = new Random();

        final MlpNet mlpNet = MlpNetService.createNet(layerSizeArr, true, true, rnd);

        final int epochMax = 100;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            final float mainOutputMseErrorValue = runTrainRandomOrder(mlpNet, expectedOutputArrArrArr, trainInputArrArrArr, rnd);

            if ((epochPos + 1) % 100 == 0) {
                MlpNetPrintUtils.printFullResultForEpoch(mlpNet, trainInputArrArrArr, expectedOutputArrArrArr, epochPos, mainOutputMseErrorValue);
            }
        }

        // Act & Assert
        MlpNetTestUtils.actAssertExpectedOutput(mlpNet, trainInputArrArrArr, expectedOutputArrArrArr, 0.05F);
    }

    @Test
    void GIVEN_0_input_bits_and_clock_input_THEN_output_is_sinus_sequence() {
        // Arrange
        final float[][][] trainInputArrArrArr = new float[][][]
                {
                        //                   1
                        {
                                new float[]{ 1 }, // 0,5
                                new float[]{ 0 }, // 1
                                new float[]{ 0 }, // 1,5
                                new float[]{ 0 }, // 2
                                new float[]{ 0 }, // 2,5
                                new float[]{ 0 }, // 3
                                new float[]{ -1 }, // 3,5
                                new float[]{ 0 }, // 4
                                new float[]{ 0 }, // 4,5
                                new float[]{ 0 }, // 5
                                new float[]{ 0 }, // 5,5
                                new float[]{ 0 }, // 6
                        },
                };
        final float[][][] expectedOutputArrArrArr = new float[][][]
                {
                        //                   1
                        {
                                new float[]{ 0.479425539F }, //
                                new float[]{ 0.841470985F }, //
                                new float[]{ 0.997494987F }, //
                                new float[]{ 0.909297427F }, //
                                new float[]{ 0.598472144F }, //
                                new float[]{ 0.141120008F }, //
                                new float[]{ -0.350783228F }, //
                                new float[]{ -0.756802495F }, //
                                new float[]{ -0.977530118F }, //
                                new float[]{ -0.958924275F }, //
                                new float[]{ -0.705540326F }, //
                                new float[]{ -0.279415498F }, //
                        },
                };
        final int[] layerSizeArr = new int[]{ 1, 16, 32, 32, 16, 1 };

        final Random rnd = new Random(123456);
        //final Random rnd = new Random();

        final MlpNet mlpNet = MlpNetService.createNet(layerSizeArr, true, true, rnd);

        // 0
        // 1 to   <---,
        // 2 from ----'
        addForwwardInputs(mlpNet, 2, 1, true, false, true, false, true, rnd);
        addForwwardInputs(mlpNet, 3, 2, true, false, true, false, true, rnd);

        final int epochMax = 8_000;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            final float mainOutputMseErrorValue = runTrainRandomOrder(mlpNet, expectedOutputArrArrArr, trainInputArrArrArr, rnd);

            if ((epochPos + 1) % 100 == 0) {
                MlpNetPrintUtils.printFullResultForEpoch(mlpNet, trainInputArrArrArr, expectedOutputArrArrArr, epochPos, mainOutputMseErrorValue);
            }
        }

        // Act & Assert
        MlpNetTestUtils.actAssertExpectedOutput(mlpNet, trainInputArrArrArr, expectedOutputArrArrArr, 0.05F);
    }
}
