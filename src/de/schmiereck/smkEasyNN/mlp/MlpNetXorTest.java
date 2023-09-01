package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpNetTestUtils.printResultForEpoch;
import static de.schmiereck.smkEasyNN.mlp.MlpService.runTrain;

import java.util.Random;

public class MlpNetXorTest {

    public static void main(final String[] args) {
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

        final float[][] trainArr = train1Arr;
        final float[][] expectedResultArr = expectedResult1Arr;
        final int[] layerSizeArr = layerSize1Arr;

        //final Random rnd = new Random(1234);
        final Random rnd = new Random();

        final MlpNet mlpNet = new MlpNet(layerSizeArr, true, rnd);

        final int epochMax = 500;
        for (int epochPos = 0; epochPos < epochMax; epochPos++) {

            runTrain(mlpNet, expectedResultArr, trainArr, rnd);

            if ((epochPos + 1) % 100 == 0) {
                printResultForEpoch(mlpNet, expectedResultArr, trainArr, epochPos);
            }
        }
    }
}
