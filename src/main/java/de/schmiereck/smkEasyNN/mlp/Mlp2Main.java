package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.printResultForEpoch;
import static de.schmiereck.smkEasyNN.mlp.MlpService.runTrainRandom;

import java.util.Random;

public class Mlp2Main {

    public static void main(final String[] args) {

        final float[][] trainInputArrArr = new float[][]
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
        final float[][] expectedOutputArrArr = new float[][]
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
        final int[] layerSizeArr = new int[]{ 3, 3, 1 };

        final Random rnd = new Random(1234);
        //final Random rnd = new Random();

        final MlpNet mlpNet = new MlpNet(layerSizeArr, true, rnd);

        final int epochMax = 600;
        for (int epochPos = 0; epochPos < epochMax; epochPos++) {

            runTrainRandom(mlpNet, expectedOutputArrArr, trainInputArrArr, rnd);

            if ((epochPos + 1) % 100 == 0) {
                printResultForEpoch(mlpNet, trainInputArrArr, expectedOutputArrArr, epochPos);
            }
        }
    }
}
