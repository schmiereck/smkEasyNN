package de.schmiereck.smkEasyNN.ticTacToe;

import static de.schmiereck.smkEasyNN.mlp.MlpLayerService.addForwwardInputs;
import static de.schmiereck.smkEasyNN.mlp.MlpService.run;
import static de.schmiereck.smkEasyNN.mlp.MlpService.trainWithOutput;

import de.schmiereck.smkEasyNN.mlp.MlpConfiguration;
import de.schmiereck.smkEasyNN.mlp.MlpNet;
import de.schmiereck.smkEasyNN.mlp.MlpNetService;

import java.util.Arrays;
import java.util.Random;

public class TicTacToeMain {
    public static void main(String[] args) {

        // Input:
        // Move   Player
        // 3x3    2        = 11
        // Output:
        // Move
        // 3x3             = 9
        final int[] layerSizeArr = new int[]{ 11, 32, 32, 32, 32, 9 };

        final Random rnd = new Random(12345);
        //final Random rnd = new Random();

        final MlpNet[] netArr = new MlpNet[2];
        for (int pos = 0; pos < netArr.length; pos++) {
            final MlpConfiguration config = new MlpConfiguration(true, true, 3.0F);
            netArr[pos] = MlpNetService.createNet(config, layerSizeArr, rnd);

            addForwwardInputs(netArr[pos], 2, 1, true, false, true, false, false, rnd);
            addForwwardInputs(netArr[pos], 3, 2, true, false, true, false, false, rnd);
        }

        final int[] board = new int[3 * 3];
        final float[] lastOutputArr = new float[9];
        for (int movePos = 0; movePos < 1_000; movePos++) {
            final int aNetPos = movePos % 2;
            final int bNetPos = (movePos + 1) % 2;
            final MlpNet aNet = netArr[aNetPos];
            final MlpNet bNet = netArr[bNetPos];
            final float[] inputArr = new float[11];

            inputArr[aNetPos] = 1.0F;
            inputArr[bNetPos] = 0.0F;
            System.arraycopy(lastOutputArr, 0, inputArr, 2, 9);

            final float[] aOutputArr = run(aNet, inputArr);

            // Find the highest output.
            int aMovePos = -1;
            float aMoveOut = Float.MIN_VALUE;
            for (int outPos = 0; outPos < aOutputArr.length; outPos++) {
                if (aOutputArr[outPos] > aMoveOut) {
                    aMovePos = aNetPos;
                    aMoveOut = aOutputArr[outPos];
                }
            }
            for (int outPos = 0; outPos < aOutputArr.length; outPos++) {
                if (aMovePos == outPos) {
                    lastOutputArr[outPos] = 1.0F;
                } else {
                    lastOutputArr[outPos] = 0.0F;
                }
            }

            // Evaluation:
            final float[] expectedOutputArr = new float[9];
            for (int outPos = 0; outPos < aOutputArr.length; outPos++) {
                final float lastOutput = lastOutputArr[outPos];

                if (lastOutput == 1.0F) {
                    // Board-Field empty?
                    if (board[outPos] == 0) {
                        // Agree with this move.
                        expectedOutputArr[outPos] = 1.0F;
                        board[outPos] = aNetPos + 1;
                    } else {
                        // Disagree with this move.
                        lastOutputArr[outPos] = 0.0F;
                        expectedOutputArr[outPos] = 0.0F;
                    }
                }
            }

            System.out.printf("%d: %s: %s: %s\n", aNetPos, Arrays.toString(board), Arrays.toString(lastOutputArr), Arrays.toString(aOutputArr));

            // Train:
            trainWithOutput(aNet, expectedOutputArr, aOutputArr, 0.3F, 0.6F);
        }
    }
}
