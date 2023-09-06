package de.schmiereck.smkEasyNN.gridworld;

import static de.schmiereck.smkEasyNN.mlp.MlpService.addForwwardInputs;
import static de.schmiereck.smkEasyNN.mlp.MlpService.run;
import static de.schmiereck.smkEasyNN.mlp.MlpService.trainWithOutput;

import de.schmiereck.smkEasyNN.mlp.MlpNet;

import java.util.Random;

/**
 * Reinforcement Learning (RL)
 *
 * model-free Ansatz
 *
 * https://www.mikrocontroller.net/topic/412417
 *
 *
 * Q-Learning
 * http://outlace.com/rlpart3.html
 */
public class GridworldMain {
    private static final int ElementPosPlayer = 0;
    private static final int ElementPosWall = 1;
    private static final int ElementPosPit = 2;
    private static final int ElementPosGoal = 3;

    private static final int MovePosUp = 0;
    private static final int MovePosDown = 1;
    private static final int MovePosLeft = 2;
    private static final int MovePosRight = 3;

    public static void main(String[] args) {
        // Elements (Player, Wall, Pit, Goal)

        // Input:
        // Board  Elements
        // 4x4    x4        = 64
        // Output:
        // Move-Dir (up, down, left, right)
        // 4             = 4

        //final int[] layerSizeArr = new int[]{ 64, 164, 150, 4 };
        final int[] layerSizeArr = new int[]{ 64, 32, 32, 4 };

        final Random rnd = new Random(12345);
        //final Random rnd = new Random();

        final MlpNet net = new MlpNet(layerSizeArr, true, false, rnd);

        addForwwardInputs(net, 2, 1, rnd);

        int level = 0;
        int fittnesCounter = 0;
        int hitGoalCounter = 0;
        int epoche = 0;
        while (true) {
            final boolean board[][][] = new boolean[4][4][4];

            if (hitGoalCounter > 3) {
                if (hitGoalCounter > 8) {
                    hitGoalCounter = 0;
                    if (fittnesCounter > 10) {
                        fittnesCounter = 0;
                        level++;
                    }
                } else {
                    // init randomly
                    if (fittnesCounter > 10) {
                        fittnesCounter = 0;
                        level++;
                    }
                }
                fittnesCounter++;
            }

            initBoard(board, level, rnd);

            //printBoard(board);

            System.out.printf("%5d: ", epoche);

            final float[] inputArr = new float[4 * 4 * 4];

            int move = 0;
            while (true) {
                initInputArr(inputArr, board);

                // Play:
                final float[] outputArr = run(net, inputArr);

                // Evaluation:
                final int action = findMax(outputArr);
                final ActionResult actionResult = runPlayerAction(board, action);

                // Train:
                final float[] expectedOutputArr = new float[4];

                switch (actionResult) {
                    case Moved -> {
                        //System.arraycopy(outputArr, 0, expectedOutputArr, 0, 4);
                        expectedOutputArr[action] = 0.75F;
                    }
                    case HitWall -> {
                        System.arraycopy(outputArr, 0, expectedOutputArr, 0, 4);
                        expectedOutputArr[action] = 0.0F;
                    }
                    case MovedGoal -> {
                        expectedOutputArr[action] = 1.0F;
                    }
                    case MovedPit -> {
                        System.arraycopy(outputArr, 0, expectedOutputArr, 0, 4);
                        expectedOutputArr[action] = 0.0F;
                    }
                }
                trainWithOutput(net, expectedOutputArr, outputArr, 0.3F, 0.6F);

                if (actionResult == ActionResult.MovedGoal) {
                    System.out.printf("%3d - %3d: ", level, move);
                    System.out.println("MovedGoal");
                    hitGoalCounter++;
                    break;
                } else {
                    if (actionResult == ActionResult.MovedPit) {
                        System.out.printf("%3d - %3d: ", level, move);
                        System.out.println("MovedPit");
                        hitGoalCounter = 0;
                        break;
                    } else {
                        if (move > 16) {
                            System.out.printf("%3d - %3d: ", level, move);
                            System.out.println("BREAK");
                            hitGoalCounter = 0;
                            break;
                        }
                    }
                }
                move++;
            }
            epoche++;
        }
    }

    private static void printBoard(boolean[][][] board) {
        for (int yBoardPos = 0; yBoardPos < 4; yBoardPos++) {
            for (int xBoardPos = 0; xBoardPos < 4; xBoardPos++) {
                final int fieldElementPos = calcFieldElementPos(board, xBoardPos, yBoardPos);
                switch (fieldElementPos) {
                    case ElementPosPlayer -> System.out.print("|P");
                    case ElementPosWall -> System.out.print("|W");
                    case ElementPosPit -> System.out.print("|-");
                    case ElementPosGoal -> System.out.print("|+");
                    default -> System.out.print("| ");
                }

            }
            System.out.println("|");
        }
    }

    private static void initInputArr(float[] inputArr, boolean[][][] board) {
        for (int yBoardPos = 0; yBoardPos < 4; yBoardPos++) {
            for (int xBoardPos = 0; xBoardPos < 4; xBoardPos++) {
                for (int elementPos = 0; elementPos < 4; elementPos++) {
                    inputArr[(yBoardPos * 4 * 4) + (xBoardPos * 4) + elementPos] = board[yBoardPos][xBoardPos][elementPos] ? 1.0F : 0.0F;
                }
            }
        }
    }

    private static void initBoard(boolean[][][] board, final int level, final Random rnd) {
        if (level == 0) {
            board[2][3][ElementPosPlayer] = true;
            board[2][1][ElementPosWall] = true;
            board[1][3][ElementPosPit] = true;
            board[3][3][ElementPosGoal] = true;
        } else {
            if (level == 1) {
                // init randomly player pos
                board[2][1][ElementPosWall] = true;
                board[1][3][ElementPosPit] = true;
                board[3][3][ElementPosGoal] = true;
                initBoardField(board, ElementPosPlayer, rnd);
            } else {
                // init randomly with increasing distance between player and goal
                initBoardField(board, ElementPosWall, rnd);
                initBoardField(board, ElementPosPit, rnd);
                final Pos goalPos = initBoardField(board, ElementPosGoal, rnd);
                final int distance = (level - 1) <= 4 ? (level - 1) : 4;
                initBoardField(board, ElementPosPlayer, goalPos, distance, rnd);
            }
        }
    }

    private static Pos initBoardField(final boolean[][][] board, final int elementPos, final Pos pos, final int distance, final Random rnd) {
        while (true) {
            final int posX = wrapBoardPos(pos.x + rnd.nextInt(distance * 2 + 1) - distance);
            final int posY = wrapBoardPos(pos.y + rnd.nextInt(distance * 2 + 1) - distance);

            if (checkFieldEmpty(board, posX, posY)) {
                board[posY][posX][elementPos] = true;
                return new Pos(posX, posY);
            }
        }
    }

    private static Pos initBoardField(final boolean[][][] board, final int elementPos, final Random rnd) {
        while (true) {
            final int posX = rnd.nextInt(4);
            final int posY = rnd.nextInt(4);

            if (checkFieldEmpty(board, posX, posY)) {
                board[posY][posX][elementPos] = true;
                return new Pos(posX, posY);
            }
        }
    }

    private static boolean checkFieldEmpty(final boolean[][][] board, final int posX, final int posY) {
        boolean foundElement = false;
        for (int elementPos = 0; elementPos < 4; elementPos++) {
            if (board[posY][posX][elementPos]) {
                foundElement = true;
                break;
            }
        }
        return !foundElement;
    }

    private static int calcFieldElementPos(final boolean[][][] board, final int posX, final int posY) {
        int retElementPos = -1;
        boolean foundElement = false;
        for (int elementPos = 0; elementPos < 4; elementPos++) {
            if (board[posY][posX][elementPos]) {
                retElementPos = elementPos;
                break;
            }
        }
        return retElementPos;
    }

    private static class Pos {
        int x, y;
        public Pos(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private enum ActionResult {
        HitWall, MovedPit, MovedGoal, Moved
    }

    private static ActionResult runPlayerAction(final boolean[][][] board, final int actionPos) {
        final ActionResult actionResult;
        final Pos playerPos = calcPos(board, ElementPosPlayer);

        final boolean wallField = board[playerPos.y][playerPos.x][ElementPosWall];

        if (!wallField) {
            board[playerPos.y][playerPos.x][ElementPosPlayer] = false;
            switch (actionPos) {
                case MovePosUp -> board[wrapBoardPos(playerPos.y - 1)][playerPos.x][ElementPosPlayer] = true;
                case MovePosDown -> board[wrapBoardPos(playerPos.y + 1)][playerPos.x][ElementPosPlayer] = true;
                case MovePosLeft -> board[playerPos.y][wrapBoardPos(playerPos.x - 1)][ElementPosPlayer] = true;
                case MovePosRight -> board[playerPos.y][wrapBoardPos(playerPos.x + 1)][ElementPosPlayer] = true;
            }
            final boolean pitField = board[playerPos.y][playerPos.x][ElementPosPit];
            if (pitField) {
                actionResult = ActionResult.MovedPit;
            } else {
                final boolean goalField = board[playerPos.y][playerPos.x][ElementPosGoal];
                if (goalField) {
                    actionResult = ActionResult.MovedGoal;
                } else {
                    actionResult = ActionResult.Moved;
                }
            }
        } else {
            actionResult = ActionResult.HitWall;
        }
        return actionResult;
    }

    private static Pos calcPos(final boolean[][][] board, final int elementPos) {
        for (int yBoardPos = 0; yBoardPos < 4; yBoardPos++) {
            for (int xBoardPos = 0; xBoardPos < 4; xBoardPos++) {
                final boolean field = board[yBoardPos][xBoardPos][elementPos];

                if (field) {
                    return new Pos(xBoardPos, yBoardPos);
                }
            }
        }
        return null;
    }

    private static int findMax(final float[] outputArr) {
        int maxPos = -1;
        float moveOutValue = -Float.MAX_VALUE;
        for (int outPos = 0; outPos < outputArr.length; outPos++) {
            if (outputArr[outPos] > moveOutValue) {
                maxPos = outPos;
                moveOutValue = outputArr[maxPos];
            }
        }
        return maxPos;
    }

    private static int wrapBoardPos(final int pos) {
        final int retPos;

        if (pos >= 4) {
            retPos = pos - 4;
        } else {
            if (pos < 0) {
                retPos = pos + 4;
            } else {
                retPos = pos;
            }
        }
        return retPos;
    }
}
