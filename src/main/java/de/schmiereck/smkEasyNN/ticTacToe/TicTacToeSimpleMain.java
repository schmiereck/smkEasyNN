package de.schmiereck.smkEasyNN.ticTacToe;

import static de.schmiereck.smkEasyNN.mlp.MlpService.addForwwardInputs;
import static de.schmiereck.smkEasyNN.mlp.MlpService.run;
import static de.schmiereck.smkEasyNN.mlp.MlpService.trainWithOutput;

import de.schmiereck.smkEasyNN.mlp.MlpNet;

import java.util.Arrays;
import java.util.Random;

public class TicTacToeSimpleMain {
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
            netArr[pos] = new MlpNet(layerSizeArr, true, true, rnd);

            //addForwwardInputs(netArr[pos], 2, 1, rnd);
            //addForwwardInputs(netArr[pos], 3, 2, rnd);
        }

        final int[] board = new int[3 * 3];
        final float[] lastOutputArr = new float[9];
        for (int movePos = 0; movePos < 1_000; movePos++) {
            final int aNetPos = movePos % 2;
            final int bNetPos = (movePos + 1) % 2;
            final MlpNet aNet = netArr[aNetPos];
            final MlpNet bNet = netArr[bNetPos];
            final float[] inputArr = new float[11];

            // Player:
            inputArr[aNetPos] = 1.0F;
            inputArr[bNetPos] = 0.0F;

            // Input-Move:
            for (int boardPos = 0; boardPos < board.length; boardPos++) {
                final float field = board[boardPos];
                if (field == 1) {
                    inputArr[2 + boardPos] = 1.0F;
                } else {
                    if (field == 2) {
                        inputArr[2 + boardPos] = -1.0F;
                    } else {
                        inputArr[2 + boardPos] = 0.0F;
                    }
                }
            }
            //System.arraycopy(lastOutputArr, 0, inputArr, 2, 9);

            // Play:
            final float[] aOutputArr = run(aNet, inputArr);

            // Find the highest output.
            int aMovePos = -1;
            float aMoveOutValue = Float.MIN_VALUE;
            for (int outPos = 0; outPos < aOutputArr.length; outPos++) {
                if (aOutputArr[outPos] > aMoveOutValue) {
                    aMovePos = outPos;
                    aMoveOutValue = aOutputArr[aMovePos];
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
            boolean foundMove = false;
            int foundMovePos = -1;
            final float[] expectedOutputArr = new float[9];
            for (int outPos = 0; outPos < aOutputArr.length; outPos++) {
                final float lastOutputValue = lastOutputArr[outPos];

                if (lastOutputValue == 1.0F) {
                    // Board-Field empty?
                    if (board[outPos] == 0) {
                        // Agree with this move.
                        expectedOutputArr[outPos] = 1.0F;
                        board[outPos] = aNetPos + 1;
                        foundMove = true;
                        foundMovePos = outPos;
                    } else {
                        // Disagree with this move.
                        lastOutputArr[outPos] = 0.0F;
                        expectedOutputArr[outPos] = 0.0F;
                    }
                }
            }

            if (!foundMove) {
                for (int outPos = 0; outPos < expectedOutputArr.length; outPos++) {
                    if (board[outPos] != 0) {
                        expectedOutputArr[outPos] = rnd.nextFloat() - 0.5F;
                    }
                }
            }

            System.out.printf("%d: %s:: %s: %s: %s\n", aNetPos, Arrays.toString(inputArr), Arrays.toString(board), Arrays.toString(lastOutputArr), Arrays.toString(aOutputArr));

            // Train:
            trainWithOutput(aNet, expectedOutputArr, aOutputArr, 0.3F, 0.6F);
        }
    }
}
