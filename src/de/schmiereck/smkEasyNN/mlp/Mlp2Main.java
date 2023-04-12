package de.schmiereck.smkEasyNN.mlp;

import java.util.Random;

public class Mlp2Main {

    public static void main(String[] args) {
        final float[][] train1Arr = new float[][]
                {
                        new float[]{0, 0},
                        new float[]{0, 1},
                        new float[]{1, 0},
                        new float[]{1, 1}
                };
        final float[][] expectedResult1Arr = new float[][]
                {
                        new float[]{0},
                        new float[]{1},
                        new float[]{1},
                        new float[]{0}
                };
        final int[] layerSize1Arr = new int[]{ 2, 1 };

        final float[][] train2Arr = new float[][]
                {
                        new float[]{0, 0, 0},
                        new float[]{0, 0, 1},
                        new float[]{0, 1, 0},
                        new float[]{0, 1, 1},

                        new float[]{1, 0, 0},
                        new float[]{1, 0, 1},
                        new float[]{1, 1, 0},
                        new float[]{1, 1, 1}
                };
        final float[][] expectedResult2Arr = new float[][]
                {
                        new float[]{0},
                        new float[]{1},
                        new float[]{1},
                        new float[]{1},

                        new float[]{1},
                        new float[]{1},
                        new float[]{1},
                        new float[]{0}
                };
        final int[] layerSize2Arr = new int[]{ 3, 3, 1 };

        final float[][] trainArr = train2Arr;
        final float[][] expectedResultArr = expectedResult2Arr;
        final int[] layerSizeArr = layerSize2Arr;

        //final Random rnd = new Random(1234);
        final Random rnd = new Random();

        final MlpNet mlpNet = new MlpNet(layerSizeArr, true, rnd);


        final int epochMax = 500;
        for (int epochPos = 0; epochPos < epochMax; epochPos++) {

            for (int expectedResultPos = 0; expectedResultPos < expectedResultArr.length; expectedResultPos++) {
                //int idx = rnd.nextInt(expectedResultArr.length);
                int idx = expectedResultPos;
                MlpService.train(mlpNet, trainArr[idx], expectedResultArr[idx], 0.3F, 0.6F);
            }

            if ((epochPos + 1) % 100 == 0) {
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
    }
}
