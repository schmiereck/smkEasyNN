package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpNetPrintUtils.printFullResultForEpoch;
import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.actAssertExpectedOutput;
import static de.schmiereck.smkEasyNN.mlp.MlpLayerService.addForwwardInputs;
import static de.schmiereck.smkEasyNN.mlp.MlpService.runTrainRandomOrder;

import java.util.Random;

import org.junit.jupiter.api.Test;

public class MlpNetMemoryTest {

    private record Result(float[][][] trainInputArrArrArr, float[][][] expectedOutputArrArrArr) {
    }

    private static Result arrangeResult() {
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
                };
        return new Result(trainInputArrArrArr, expectedOutputArrArrArr);
    }

    @Test
    void GIVEN_many2one_1_input_bits_in_sequence_THEN_output_is_input_in_sequence_before() {
        // Arrange
        final Result result = arrangeResult();
        final int[] layerSizeArr = new int[]{ 1, 4, 8, 1 };

        final Random rnd = new Random(123456);
        //final Random rnd = new Random();

        final MlpNet mlpNet = MlpNetService.createNet(layerSizeArr, true, false, rnd);

        // 0
        // 1 to   <---,
        // 2 from ----'
        //addForwwardInputs(mlpNet, 2, 1, rnd);
        //addForwwardInputs(mlpNet, 2, 1, true, false, true, rnd);
        addForwwardInputs(mlpNet, 2, 1, true, false, true, rnd);
        //addForwwardInputs(mlpNet, 1, 1, true, false, true, rnd);
        //addForwwardInputs(mlpNet, 2, 2, true, false, true, rnd);

        final int epochMax = 66_000;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            runTrainRandomOrder(mlpNet, result.expectedOutputArrArrArr(), result.trainInputArrArrArr(), 0.3F, 0.6F, rnd);

            if ((epochPos + 1) % 100 == 0) {
                MlpNetPrintUtils.printFullResultForEpoch(mlpNet, result.trainInputArrArrArr(), result.expectedOutputArrArrArr(), epochPos, 1);
            }
        }

        // Act & Assert
        actAssertExpectedOutput(mlpNet, result.trainInputArrArrArr(), result.expectedOutputArrArrArr(), 0.05F);
    }

    @Test
    void GIVEN_one2one_1_input_bits_in_sequence_THEN_output_is_input_in_sequence_before() {
        // Arrange
        final Result result = arrangeResult();
        final int[] layerSizeArr = new int[]{ 1, 4, 8, 1 };

        final Random rnd = new Random(123456);
        //final Random rnd = new Random();

        final MlpNet mlpNet = MlpNetService.createNet(layerSizeArr, true, false, rnd);

        // 0
        // 1 to   <---,
        // 2 from ----'
        addForwwardInputs(mlpNet, 2, 1, rnd);
        //addForwwardInputs(mlpNet, 2, 1, true, false, true, rnd);
        //addForwwardInputs(mlpNet, 2, 1, false, false, true, rnd);
        //addForwwardInputs(mlpNet, 3, 1, false, false, true, rnd);
        //addForwwardInputs(mlpNet, 3, 1, false, false, true, rnd);

        final int epochMax = 60_000;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            runTrainRandomOrder(mlpNet, result.expectedOutputArrArrArr(), result.trainInputArrArrArr(), 0.3F, 0.6F, rnd);

            if ((epochPos + 1) % 100 == 0) {
                MlpNetPrintUtils.printFullResultForEpoch(mlpNet, result.trainInputArrArrArr(), result.expectedOutputArrArrArr(), epochPos, 1);
            }
        }

        // Act & Assert
        actAssertExpectedOutput(mlpNet, result.trainInputArrArrArr(), result.expectedOutputArrArrArr(), 0.05F);
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

        final MlpNet mlpNet = MlpNetService.createNet(layerSizeArr, true, rnd);

        // 0
        // 1 to   <---,
        // 2 from ----'
        //addForwwardInputs(mlpNet, 1, 1, rnd);
        addForwwardInputs(mlpNet, 2, 1, false, false, true, rnd);
        //addForwwardInputs(mlpNet, 2, 2, rnd);
        //addForwwardInputs(mlpNet, 3, 2, rnd);
        //addInternalInputs(mlpNet, 1, rnd);

        final int epochMax = 2_000;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            final float mainOutputMseErrorValue = runTrainRandomOrder(mlpNet, expectedOutputArrArrArr, trainInputArrArrArr, rnd);

            if ((epochPos + 1) % 100 == 0) {
                MlpNetPrintUtils.printFullResultForEpoch(mlpNet, trainInputArrArrArr, expectedOutputArrArrArr, epochPos, mainOutputMseErrorValue);
            }
        }

        // Act & Assert
        actAssertExpectedOutput(mlpNet, trainInputArrArrArr, expectedOutputArrArrArr, 0.05F);
    }

    @Test
    void GIVEN_2_input_bits_in_sequence_THEN_flip_flop_output() {
        // Arrange
        final float[][][] trainInputArrArrArr = new float[][][]
                {
                        //                          1
                        {
                                new float[]{0, 1}, //
                                new float[]{0, 0}, //
                                new float[]{0, 0}, //
                                new float[]{0, 0}, //
                                new float[]{0, 0}, //
                                new float[]{0, 0}, //
                        },
                        {
                                new float[]{1, 0}, //
                                new float[]{0, 0}, //
                                new float[]{0, 0}, //
                                new float[]{0, 0}, //
                        },
                        {
                                new float[]{0, 1}, //
                                new float[]{0, 0}, //
                                new float[]{0, 0}, //
                                new float[]{0, 0}, //
                                new float[]{0, 0}, //
                                new float[]{0, 0}, //
                                new float[]{0, 0}, //
                        },
                        {
                                new float[]{1, 0}, //
                                new float[]{0, 0}, //
                                new float[]{0, 0}, //
                        },
                };
        final float[][][] expectedOutputArrArrArr = new float[][][]
                {
                        //                   1
                        {
                                new float[]{0, 1}, //
                                new float[]{0, 1}, //
                                new float[]{0, 1}, //
                                new float[]{0, 1}, //
                                new float[]{0, 1}, //
                                new float[]{0, 1}, //
                        },
                        {
                                new float[]{1, 0}, //
                                new float[]{1, 0}, //
                                new float[]{1, 0}, //
                                new float[]{1, 0}, //
                        },
                        {
                                new float[]{0, 1}, //
                                new float[]{0, 1}, //
                                new float[]{0, 1}, //
                                new float[]{0, 1}, //
                                new float[]{0, 1}, //
                                new float[]{0, 1}, //
                                new float[]{0, 1}, //
                        },
                        {
                                new float[]{1, 0}, //
                                new float[]{1, 0}, //
                                new float[]{1, 0}, //
                        },
                };
        final int[] layerSizeArr = new int[]{ 2, 2, 1, 2 };

        final Random rnd = new Random(123456);
        //final Random rnd = new Random();

        final MlpNet mlpNet = MlpNetService.createNet(layerSizeArr, true, rnd);

        // 0
        // 1 to   <---,
        // 2 from ----'
        //addForwwardInputs(mlpNet, 1, 1, rnd);
        addForwwardInputs(mlpNet, 2, 1, rnd);
        //addForwwardInputs(mlpNet, 2, 2, rnd);
        //addForwwardInputs(mlpNet, 3, 2, rnd);
        //addInternalInputs(mlpNet, 1, rnd);

        final int epochMax = 700;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            final float mainOutputMseErrorValue = runTrainRandomOrder(mlpNet, expectedOutputArrArrArr, trainInputArrArrArr, rnd);

            if ((epochPos + 1) % 100 == 0) {
                MlpNetPrintUtils.printFullResultForEpoch(mlpNet, trainInputArrArrArr, expectedOutputArrArrArr, epochPos, mainOutputMseErrorValue);
            }
        }

        // Act & Assert
        actAssertExpectedOutput(mlpNet, trainInputArrArrArr, expectedOutputArrArrArr, 0.05F);
    }
}
