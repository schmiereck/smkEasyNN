package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.actAssertExpectedOutput;
import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.printResultForEpoch;
import static de.schmiereck.smkEasyNN.mlp.MlpService.runTrainRandomOrder;

import java.util.Random;

import org.junit.jupiter.api.Test;

public class MlpNetLimitTest {

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
                                new float[]{ Float.NaN }, //
                        },
                };
        final int[] layerSizeArr = new int[]{ 1, 1001, 1 };

        final Random rnd = new Random(123456);
        //final Random rnd = new Random();

        final MlpNet mlpNet = new MlpNet(layerSizeArr, true, rnd);

        final int epochMax = 100;
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
