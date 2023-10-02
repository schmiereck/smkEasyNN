package de.schmiereck.smkEasyNN.gridworld;

import de.schmiereck.smkEasyNN.mlp.MlpNet;
import de.schmiereck.smkEasyNN.mlp.MlpSaveService;
import de.schmiereck.smkEasyNN.mlp.MlpService;

import java.util.Random;

public class GridworldGameService {
    static final int ElementPosPlayer = 0;
    static final int ElementPosWall = 1;
    static final int ElementPosPit = 2;
    static final int ElementPosGoal = 3;

    static final int MovePosUp = 0;
    static final int MovePosDown = 1;
    static final int MovePosLeft = 2;
    static final int MovePosRight = 3;
    public static final int MAX_MOVE_COUNT = 32;

    public static final boolean useEndlessWorld = false;

    enum ActionResult {
        HitWall, MovedPit, MovedGoal, Moved
    }

    static class Pos {
        int x, y;
        public Pos(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static int runPlayGame(final MlpNet net, final Board board, final int level, final GameStatistic gameStatistic, final int hitGoalCounter, final Random rnd) {
        int retHitGoalCounter = hitGoalCounter;
        final float[] inputArr = new float[4 * 4 * 4 + 1];
        float mse = 0.0F;
        int trainCount = 0;

        // New Game Reset:
        inputArr[4 * 4 * 4] = 1.0F;
        {
            final float[] expectedOutputArr = new float[4];
            final float[] outputArr = MlpSaveService.run(net, inputArr);
            MlpService.trainWithOutput(net, expectedOutputArr, outputArr, gameStatistic.learningRate, gameStatistic.momentum);
        }
        {
            final float[] expectedOutputArr = new float[4];
            final float[] outputArr = MlpSaveService.run(net, inputArr);
            MlpService.trainWithOutput(net, expectedOutputArr, outputArr, gameStatistic.learningRate, gameStatistic.momentum);
        }
        inputArr[4 * 4 * 4] = 0.0F;

        // Game-Play Loop:
        int move = 0;
        while (true) {
            initInputArr(inputArr, board);

            // Play:
            //final float[] outputArr = MlpSaveService.run(net, inputArr);
            final float[] outputArr = MlpService.run(net, inputArr);

            // Evaluation:
            final int action = findMax(outputArr);
            final GridworldGameService.ActionResult actionResult = runPlayerAction(board, action);

            // Train:
            final float[] expectedOutputArr = new float[4];
            final boolean runTrain;

            switch (actionResult) {
                case MovedGoal -> {
                    initializeExpectedOutput(expectedOutputArr, 0.0F); // All other actions are generating errors (+/-).
                    expectedOutputArr[action] = calcExpectedOutput(outputArr[action], 2.0F); // Great.
                    runTrain = true;
                }
                case Moved -> {
                    System.arraycopy(outputArr, 0, expectedOutputArr, 0, 4); // All other actions are also OK.
                    //randomizeExpectedOutput(expectedOutputArr, rnd);
                    //normalizeExpectedOutput(expectedOutputArr, 0.5F);
                    expectedOutputArr[action] = calcExpectedOutput(outputArr[action], 1.00F); // Good.
                    //runTrain = true;
                    runTrain = false;
                }
                case HitWall -> {
                    System.arraycopy(outputArr, 0, expectedOutputArr, 0, 4); // All other actions are also OK.
                    //randomizeExpectedOutput(expectedOutputArr, rnd);
                    expectedOutputArr[action] = calcUnexpectedOutput(outputArr[action], -1.5F); // Not so good.
                    runTrain = true;
                    //runTrain = false;
                }
                case MovedPit -> {
                    System.arraycopy(outputArr, 0, expectedOutputArr, 0, 4); // All other actions are also OK.
                    //randomizeExpectedOutput(expectedOutputArr, rnd);
                    expectedOutputArr[action] = calcUnexpectedOutput(outputArr[action], -2.75F); // Bad.
                    runTrain = true;
                }
                default -> throw new RuntimeException("Unexpected actionResult \"%s\".".formatted(actionResult));
            }
            if (runTrain) {
                //normalizeExpectedOutput(expectedOutputArr);

                //MlpSaveService.trainWithOutput(net, expectedOutputArr, outputArr, 0.3F, 0.6F);
                mse += MlpService.trainWithOutput(net, expectedOutputArr, outputArr, gameStatistic.learningRate, gameStatistic.momentum);
                trainCount++;
            }
            move++;

            if (actionResult == GridworldGameService.ActionResult.MovedGoal) {
                gameStatistic.hitGoalCounter++;
                gameStatistic.actionResult = actionResult;
                retHitGoalCounter++;
                break;
            } else {
                if (actionResult == GridworldGameService.ActionResult.MovedPit) {
                    gameStatistic.hitPitCounter++;
                    gameStatistic.actionResult = actionResult;
                    retHitGoalCounter = 0;
                    break;
                } else {
                    if (move > MAX_MOVE_COUNT) {
                        gameStatistic.maxMoveCounter++;
                        gameStatistic.actionResult = actionResult;
                        retHitGoalCounter = 0;
                        break;
                    } else {
                        if (actionResult == ActionResult.HitWall) {
                            gameStatistic.hitWallCounter++;
                            gameStatistic.actionResult = actionResult;
                        }
                    }
                }
            }
        }
        gameStatistic.moveCounter += move;
        gameStatistic.mse = mse / trainCount;
        return retHitGoalCounter;
    }

    private static float calcExpectedOutput(final float output, final float expectedOutput) {
        return output > expectedOutput ? output : expectedOutput;
    }

    private static float calcUnexpectedOutput(final float output, final float unexpectedOutput) {
        return output < unexpectedOutput ? output : unexpectedOutput;
    }

    private static void initializeExpectedOutput(final float[] expectedOutputArr, final float value) {
        for (int pos = 0; pos < expectedOutputArr.length; pos++) {
            expectedOutputArr[pos] = value;
        }
    }

    private static void normalizeExpectedOutput(final float[] expectedOutputArr, final float value) {
        for (int pos = 0; pos < expectedOutputArr.length; pos++) {
            expectedOutputArr[pos] *= value;
        }
    }

    private static void randomizeExpectedOutput(final float[] expectedOutputArr, final Random rnd) {
        for (int pos = 0; pos < expectedOutputArr.length; pos++) {
            expectedOutputArr[pos] = (rnd.nextFloat(0.5F));
        }
    }

    private static void randomizeExpectedOutput1(final float[] expectedOutputArr, final Random rnd) {
        for (int pos = 0; pos < expectedOutputArr.length; pos++) {
            expectedOutputArr[pos] = (rnd.nextFloat(0.5F) - 0.25F);
        }
    }

    private static void randomizeExpectedOutput2(final float[] expectedOutputArr, final Random rnd) {
        for (int pos = 0; pos < expectedOutputArr.length; pos++) {
            expectedOutputArr[pos] += (rnd.nextFloat(0.25F) - 0.125F);
        }
    }

    private static void normalizeExpectedOutput(final float[] expectedOutputArr) {
        for (int pos = 0; pos < expectedOutputArr.length; pos++) {
            if (expectedOutputArr[pos] < 0.0F) {
                expectedOutputArr[pos] = 0.0F;
            } else {
                if (expectedOutputArr[pos] > 1.0F) {
                    expectedOutputArr[pos] = 1.0F;
                }
            }
        }
    }

    private static void initInputArr(final float[] inputArr, final Board board) {
        for (int yBoardPos = 0; yBoardPos < 4; yBoardPos++) {
            for (int xBoardPos = 0; xBoardPos < 4; xBoardPos++) {
                for (int elementPos = 0; elementPos < 4; elementPos++) {
                    inputArr[(yBoardPos * 4 * 4) + (xBoardPos * 4) + elementPos] = board.board[yBoardPos][xBoardPos][elementPos] ? 1.0F : 0.0F;
                }
            }
        }
    }

    static GridworldGameService.ActionResult runPlayerAction(final Board board, final int actionPos) {
        final GridworldGameService.ActionResult actionResult;
        final GridworldGameService.Pos playerPos = calcPos(board, ElementPosPlayer);

        final int playerPosX;
        final int playerPosY;
        final boolean wallField;

        if (useEndlessWorld) {
            switch (actionPos) {
                case MovePosUp -> {
                    playerPosX = playerPos.x;
                    playerPosY = wrapBoardPos(playerPos.y - 1);
                }
                case MovePosDown -> {
                    playerPosX = playerPos.x;
                    playerPosY = wrapBoardPos(playerPos.y + 1);
                }
                case MovePosLeft -> {
                    playerPosX = wrapBoardPos(playerPos.x - 1);
                    playerPosY = playerPos.y;
                }
                case MovePosRight -> {
                    playerPosX = wrapBoardPos(playerPos.x + 1);
                    playerPosY = playerPos.y;
                }
                default -> throw new RuntimeException("Unexpected actionPos \"%d\".".formatted(actionPos));
            }
            wallField = board.board[playerPosY][playerPosX][ElementPosWall];
        } else {
            switch (actionPos) {
                case MovePosUp -> {
                    playerPosX = playerPos.x;
                    playerPosY = playerPos.y - 1;
                }
                case MovePosDown -> {
                    playerPosX = playerPos.x;
                    playerPosY = playerPos.y + 1;
                }
                case MovePosLeft -> {
                    playerPosX = playerPos.x - 1;
                    playerPosY = playerPos.y;
                }
                case MovePosRight -> {
                    playerPosX = playerPos.x + 1;
                    playerPosY = playerPos.y;
                }
                default -> throw new RuntimeException("Unexpected actionPos \"%d\".".formatted(actionPos));
            }
            if (checkBoardPosEnd(playerPosX) || checkBoardPosEnd(playerPosY)) {
                wallField = true;
            } else {
                wallField = board.board[playerPosY][playerPosX][ElementPosWall];
            }
        }
        if (wallField) {
            actionResult = GridworldGameService.ActionResult.HitWall;
        } else {
            board.board[playerPos.y][playerPos.x][ElementPosPlayer] = false;
            board.board[playerPosY][playerPosX][ElementPosPlayer] = true;

            final boolean pitField = board.board[playerPosY][playerPosX][ElementPosPit];
            if (pitField) {
                actionResult = GridworldGameService.ActionResult.MovedPit;
            } else {
                final boolean goalField = board.board[playerPosY][playerPosX][ElementPosGoal];
                if (goalField) {
                    actionResult = GridworldGameService.ActionResult.MovedGoal;
                } else {
                    actionResult = GridworldGameService.ActionResult.Moved;
                }
            }
        }
        return actionResult;
    }

    private static GridworldGameService.Pos calcPos(final Board board, final int elementPos) {
        for (int yBoardPos = 0; yBoardPos < 4; yBoardPos++) {
            for (int xBoardPos = 0; xBoardPos < 4; xBoardPos++) {
                final boolean field = board.board[yBoardPos][xBoardPos][elementPos];

                if (field) {
                    return new GridworldGameService.Pos(xBoardPos, yBoardPos);
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

    static int wrapBoardPos(final int pos) {
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

    static boolean checkBoardPosEnd(final int pos) {
        final boolean ret;

        if (pos >= 4) {
            ret = true;
        } else {
            if (pos < 0) {
                ret = true;
            } else {
                ret = false;
            }
        }
        return ret;
    }
}
