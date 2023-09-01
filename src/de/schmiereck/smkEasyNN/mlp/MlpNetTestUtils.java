package de.schmiereck.smkEasyNN.mlp;

public class MlpNetTestUtils {

    public static void printResultForEpoch(final MlpNet mlpNet, final float[][] expectedResultArr, final float[][] trainArr, final int epochPos) {
        System.out.println();
        System.out.printf("%d epoch\n", epochPos + 1);
        for (int resultPos = 0; resultPos < expectedResultArr.length; resultPos++) {
            float[] train = trainArr[resultPos];

            final float[] resultArr = MlpService.run(mlpNet, train);

            for (int trainPos = 0; trainPos < train.length; trainPos++) {
                if (trainPos > 0) {
                    System.out.printf(" | ");
                }
                System.out.printf("%.1f", train[trainPos]);
            }
            System.out.printf(" --> %.3f\n", resultArr[0]);
        }
    }

}
