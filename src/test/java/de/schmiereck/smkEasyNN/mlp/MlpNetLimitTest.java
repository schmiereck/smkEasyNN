package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpNetPrintUtils.printFullResultForEpoch;
import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.actAssertExpectedOutput;

import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MlpNetLimitTest {

    @Test
    void GIVEN_multiply_big_values_THEN_output_is_NaN() {
        final float f1 = 1.0347248E19F;
        Assertions.assertFalse(Float.isInfinite(f1));
        Assertions.assertFalse(Float.isNaN(f1));
        final float f2 = -2.172922E20F;
        Assertions.assertFalse(Float.isInfinite(f2));
        Assertions.assertFalse(Float.isNaN(f2));

        final float f = f1 * f2;

        Assertions.assertFalse(Float.isNaN(f));
        Assertions.assertTrue(Float.isInfinite(f));
    }

    @Test
    void GIVEN_many_neurons_THEN_output_is_NaN() {
        // Arrange
        final float[][][] trainInputArrArrArr = new float[][][]
                {
                        {
                                new float[]{ 0 }, //
                        },
                };
        final float[][][] expectedOutputArrArrArr = new float[][][]
                {
                        {
                                //new float[]{ Float.NaN }, //
                                new float[]{ 0 }, //
                        },
                };
        final int[] layerSizeArr = new int[]{ 1, 1001, 1 };

        final Random rnd = new Random(123456);
        //final Random rnd = new Random();

        // final MlpConfiguration config = new MlpConfiguration(true, false, 4.0F); -> Infinite Error/Weight Sum.
        final MlpConfiguration config = new MlpConfiguration(true, false);
        final MlpNet mlpNet = MlpNetService.createNet(config, layerSizeArr, rnd);

        final int epochMax = 100;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            final float mainOutputMseErrorValue = MlpService.runTrainRandomOrder(mlpNet, expectedOutputArrArrArr, trainInputArrArrArr, rnd);

            if ((epochPos + 1) % 100 == 0) {
                MlpNetPrintUtils.printFullResultForEpoch(mlpNet, trainInputArrArrArr, expectedOutputArrArrArr, epochPos, mainOutputMseErrorValue);
            }
        }

        // Act & Assert
        actAssertExpectedOutput(mlpNet, trainInputArrArrArr, expectedOutputArrArrArr, 0.05F);
    }
}
