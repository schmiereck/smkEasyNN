package de.schmiereck.smkEasyNN.mlp;

public class MlpNetTestUtils {

    public static void printResultForEpoch(final MlpNet mlpNet, final float[][] expectedOutputArrArr, final float[][] trainInputArrArr, final int epochPos) {
        System.out.println();
        System.out.printf("%d epoch\n", epochPos + 1);
        for (int resultPos = 0; resultPos < expectedOutputArrArr.length; resultPos++) {
            float[] trainInputArr = trainInputArrArr[resultPos];

            final float[] resultArr = MlpService.run(mlpNet, trainInputArr);

            for (int trainPos = 0; trainPos < trainInputArr.length; trainPos++) {
                if (trainPos > 0) {
                    System.out.printf(" | ");
                }
                System.out.printf("%.1f", trainInputArr[trainPos]);
            }
            System.out.printf(" --> %.3f\n", resultArr[0]);
        }
    }

}
