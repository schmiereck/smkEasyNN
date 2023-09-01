package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.printResultForEpoch;
import static de.schmiereck.smkEasyNN.mlp.MlpService.runTrain;

import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MlpNetXorTest {

    @Test
    void GIVEN_2_binary_inputs_THEN_XOR_output() {
        // Arrange
        final float[][] trainInputArrArr = new float[][]
                {
                        new float[]{0, 0},
                        new float[]{0, 1},
                        new float[]{1, 0},
                        new float[]{1, 1}
                };
        final float[][] expectedOutputArrArr = new float[][]
                {
                        new float[]{0},
                        new float[]{1},
                        new float[]{1},
                        new float[]{0}
                };
        final int[] layerSizeArr = new int[]{ 2, 1 };

        final Random rnd = new Random(12345);
        //final Random rnd = new Random();

        final MlpNet mlpNet = new MlpNet(layerSizeArr, true, rnd);

        final int epochMax = 500;
        for (int epochPos = 0; epochPos <= epochMax; epochPos++) {

            runTrain(mlpNet, expectedOutputArrArr, trainInputArrArr, rnd);

            if ((epochPos + 1) % 100 == 0) {
                printResultForEpoch(mlpNet, expectedOutputArrArr, trainInputArrArr, epochPos);
            }
        }

        // Act
        // Assert
        for (int resultPos = 0; resultPos < expectedOutputArrArr.length; resultPos++) {
            final float[] expectedOutputArr = expectedOutputArrArr[resultPos];
            float[] trainInputArr = trainInputArrArr[resultPos];

            final float[] resultArr = MlpService.run(mlpNet, trainInputArr);

            for (int expectedOutputPos = 0; expectedOutputPos < expectedOutputArr.length; expectedOutputPos++) {
                Assertions.assertEquals(expectedOutputArr[expectedOutputPos], resultArr[expectedOutputPos], 0.05F,
                        "expectedOutput line %d: expectedOutputPos %d".formatted(resultPos, expectedOutputPos));
            }
        }
    }
}
