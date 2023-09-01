package de.schmiereck.smkEasyNN.mlp;

import org.junit.jupiter.api.Assertions;

public class MlpNetTestUtils {

    public static void printResultForEpoch(final MlpNet mlpNet, final float[][] expectedOutputArrArr, final float[][] trainInputArrArr, final int epochPos) {
        System.out.println();
        System.out.printf("%d epoch\n", epochPos + 1);
        for (int resultPos = 0; resultPos < expectedOutputArrArr.length; resultPos++) {
            float[] expectedOutputArr = expectedOutputArrArr[resultPos];
            float[] trainInputArr = trainInputArrArr[resultPos];

            final float[] outputArr = MlpService.run(mlpNet, trainInputArr);

            for (int trainInputPos = 0; trainInputPos < trainInputArr.length; trainInputPos++) {
                if (trainInputPos > 0) {
                    System.out.printf(" | ");
                }
                System.out.printf("%.1f", trainInputArr[trainInputPos]);
            }
            System.out.print(" --> ");
            for (int outputPos = 0; outputPos < outputArr.length; outputPos++) {
                if (outputPos > 0) {
                    System.out.printf(" | ");
                }
                System.out.printf("%6.3f", outputArr[outputPos]);
            }
            System.out.println();
        }
    }

    static void actAssertExpectedOutput(final MlpNet mlpNet, final float[][] expectedOutputArrArr, final float[][] trainInputArrArr) {
        for (int resultPos = 0; resultPos < expectedOutputArrArr.length; resultPos++) {
            final float[] expectedOutputArr = expectedOutputArrArr[resultPos];
            final float[] trainInputArr = trainInputArrArr[resultPos];

            final float[] resultArr = MlpService.run(mlpNet, trainInputArr);

            for (int expectedOutputPos = 0; expectedOutputPos < expectedOutputArr.length; expectedOutputPos++) {
                Assertions.assertEquals(expectedOutputArr[expectedOutputPos], resultArr[expectedOutputPos], 0.05F,
                        "expectedOutput line %d: expectedOutputPos %d".formatted(resultPos, expectedOutputPos));
            }
        }
    }

}
