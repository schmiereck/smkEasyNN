package de.schmiereck.smkEasyNN.gridworld;

import static de.schmiereck.smkEasyNN.gridworld.GridworldBoardService.initBoard;

import de.schmiereck.smkEasyNN.mlp.MlpConfiguration;
import de.schmiereck.smkEasyNN.mlp.MlpLayerConfig;
import de.schmiereck.smkEasyNN.mlp.MlpNet;
import de.schmiereck.smkEasyNN.mlp.MlpNetService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public static void main(String[] args) {
        // Elements (Player, Wall, Pit, Goal)

        // Input:
        // Board  Elements  Reset
        // 4x4    x4        1   = 65
        // Output:
        // Move-Dir (up, down, left, right)
        // 4                = 4

        //final int[] layerSizeArr = new int[]{ 64 + 1, 32, 32, 4 };
        //final int[] layerSizeArr = new int[]{ 64 + 1, 64, 64, 4 };
        //final int[] layerSizeArr = new int[]{ 64 + 1, 128, 64, 64, 64, 4 };
        //final int[] layerSizeArr = new int[]{ 64 + 1, 164, 150, 64, 64, 4 }; // Best.
        //final int[] layerSizeArr = new int[]{ 64 + 1, 64, 64, 32, 32, 4 }; // No: 582900: level:  5  moves: 15420375 [goal: 51847, pit:    11, wall:   142, max-move:467282]
        //final int[] layerSizeArr = new int[]{ 64 + 1, 64, 64, 64, 64, 64, 64, 4 }; // Best 2
        //final int[] layerSizeArr = new int[]{ 64 + 1, 64, 64, 64, 64, 64, 4 }; // Best 3
        //final int[] layerSizeArr = new int[]{ 64 + 1, 64, 32, 32, 32, 32, 4 }; // Best 4

        final Random rnd = new Random(123456);
        //final Random rnd = new Random();

        final int netCount = 10;
        final int killSize = 4;
        // final MlpConfiguration config = new MlpConfiguration(true, false, 4.0F); -> Infinite Error/Weight Sum.

        //final MlpNet[] netArr = new MlpNet[netCount];
        final HashMap<GameStatistic, MlpNet> netArr = new HashMap<>();
        //final GameStatistic[] gameStatisticArr = new GameStatistic[netCount];
        //final int[] levelArr = new int[netCount];
        //final int[] fittnesCounterArr = new int[netCount];
        //final int[] fittnesHitGoalCounterArr = new int[netCount];
        //final int[] epocheArr = new int[netCount];

        for (int netPos = 0; netPos < netCount; netPos++) {
            final MlpConfiguration config;
            final MlpLayerConfig[] layerConfigArr;
            switch (netPos % 2) {
                case 0 -> {
                    config = new MlpConfiguration(true, false, 0.2F, 0.0F);
                    layerConfigArr = new MlpLayerConfig[6];
                    layerConfigArr[0] = new MlpLayerConfig(64 + 1);
                    layerConfigArr[1] = new MlpLayerConfig(164 + 1);
                    layerConfigArr[2] = new MlpLayerConfig(64 * 2 + rnd.nextInt(64));

                    layerConfigArr[3] = new MlpLayerConfig(64 + rnd.nextInt(64));
                    layerConfigArr[4] = new MlpLayerConfig(32);

                    layerConfigArr[5] = new MlpLayerConfig(4);
                }
                default -> {
                    config = new MlpConfiguration(true, rnd.nextBoolean(), rnd.nextFloat(0.05F) + 0.1F, 0.0F);
                    layerConfigArr = new MlpLayerConfig[8];
                    layerConfigArr[0] = new MlpLayerConfig(64 + 1);

                    layerConfigArr[1] = new MlpLayerConfig(64 + 1);
                    layerConfigArr[2] = new MlpLayerConfig(64 + rnd.nextInt(32));

                    layerConfigArr[3] = new MlpLayerConfig(32 + rnd.nextInt(32));
                    layerConfigArr[4] = new MlpLayerConfig(32 + rnd.nextInt(32));

                    layerConfigArr[5] = new MlpLayerConfig(32);
                    layerConfigArr[6] = new MlpLayerConfig(16);

                    layerConfigArr[7] = new MlpLayerConfig(4);
                }
            }

            //layerConfigArr[0].setIsArray(true,4, 4, 16, 4, 1);
            //layerConfigArr[1].setIsArray(true,4, 4, 16, 4, 1);

            final MlpNet net = MlpNetService.createNet(config, layerConfigArr, rnd);
            //gameStatisticArr[netPos] = new GameStatistic(netPos);
            final GameStatistic gameStatistic = new GameStatistic(netPos);

            gameStatistic.learningRate = rnd.nextFloat( 0.6F) + 0.05F;
            gameStatistic.momentum = rnd.nextFloat( 0.9F) + 0.05F;
                    //addForwwardInputs(netArr[netPos], 2, 1, rnd);
            //addForwwardInputs(netArr[netPos], 3, 2, rnd);
            //addForwwardInputs(net, 5, 4, false, false, true, rnd);
            //addForwwardInputs(net, 6, 5, false, false, true, rnd);

            //MlpNetService.makeInternalInput(net, 4, inputLayerNr, 4);

            netArr.put(gameStatistic, net);
        }
        int newNetPos = netCount;

        for (int runPos = 0; runPos < 20_000_000; runPos++) {
            //for (int netPos = 0; netPos < netArr.length; netPos++) {
            for (final Map.Entry<GameStatistic, MlpNet> netEntry : netArr.entrySet()) {
                final GameStatistic gameStatistic = netEntry.getKey();
                final MlpNet net = netEntry.getValue();

                resetGameStatistic(gameStatistic);

                while (true) {
                    final Board board = new Board();
                    int oldLevel = gameStatistic.level;

                    if (gameStatistic.fittnesHitGoalCounter > 6) {
                        gameStatistic.fittnesHitGoalCounter = 0;
                        gameStatistic.fittnesCounter++;
                        if (gameStatistic.fittnesCounter > 4) {
                            gameStatistic.fittnesCounter = 0;
                            gameStatistic.level++;
                        }
                    }

                    initBoard(board, gameStatistic.level, rnd);

                    //printBoard(board);
                    final boolean newLevel = (oldLevel != gameStatistic.level);

                    if ((gameStatistic.epoche % 100 == 0) || newLevel) {
                        System.out.printf("%2d - %9d: level:%3d  moves:%9d [goal:%6d, pit:%6d, wall:%6d, max-move:%6d] mse:%.6f",
                                gameStatistic.netPos, gameStatistic.epoche, oldLevel, gameStatistic.moveCounter, gameStatistic.hitGoalCounter, gameStatistic.hitPitCounter, gameStatistic.hitWallCounter, gameStatistic.maxMoveCounter, gameStatistic.mse);
                        if (!newLevel) {
                            System.out.print('\r');
                        }
                    }

                    if (newLevel) {
                        break;
                    }

                    gameStatistic.fittnesHitGoalCounter = GridworldGameService.runPlayGame(net, board, gameStatistic.level, gameStatistic, gameStatistic.fittnesHitGoalCounter, rnd);

                    gameStatistic.epoche++;
                }

                // epoch <
                // moves (100 goals / 200 moves = 0.5, 100 goals / 100 moves = 1.0)
                final int ma = (gameStatistic.hitGoalCounter +
                        gameStatistic.hitPitCounter +
                        gameStatistic.maxMoveCounter +
                        gameStatistic.hitWallCounter);
                final float m = ((float)ma) / gameStatistic.moveCounter;
                // 1: more of these results, 0: no results
                // moves / goal
                final float mg = (calcFit(gameStatistic.hitGoalCounter, ma) * 10.0F);
                // moves / pit
                final float mp = (1.0F - (calcFit(gameStatistic.hitPitCounter, ma) * 1.0F));
                // moves / maxMove
                final float mm = (0.5F - (calcFit(gameStatistic.maxMoveCounter, ma) * 0.5F));
                // moves / wall
                final float mw = (0.25F - (calcFit(gameStatistic.hitWallCounter, ma) * 0.25F));

                // bigger is fitter.
                gameStatistic.fitness = m + (mg) + (mp) + (mm) + (mw);

                System.out.printf(" (fit:%.3f m:%.2f g:%.2f p:%.2f mm:%.2f w:%.2f)", gameStatistic.fitness, m, mg, mp, mm, mw);
                System.out.println();
            }
            // Found fittest (fittest first):
            final List<GameStatistic> sortedGameStatisticList =
                    netArr.keySet().stream()
                            .sorted((aGameStatistic, bGameStatistic) -> Float.compare(bGameStatistic.fitness, aGameStatistic.fitness)).toList();

            for (int killPos = 0; killPos < killSize; killPos++) {
                final GameStatistic fittestGameStatistic = sortedGameStatisticList.get(killPos);
                final GameStatistic worstGameStatistic = sortedGameStatisticList.get(sortedGameStatisticList.size() - (1 + killPos));

                final MlpNet fittestNet = netArr.get(fittestGameStatistic);
                final MlpNet newNet = copyNet(fittestNet);
                final GameStatistic newGameStatistic = copyGameStatistic(newNetPos, fittestGameStatistic);
                newNetPos++;

                netArr.remove(worstGameStatistic);
                netArr.put(newGameStatistic, newNet);
            }
        }
    }

    private static GameStatistic copyGameStatistic(final int newNetPos, final GameStatistic gameStatistic) {
        final GameStatistic newGameStatistic = new GameStatistic(newNetPos);

        newGameStatistic.moveCounter = gameStatistic.moveCounter;
        newGameStatistic.hitGoalCounter = gameStatistic.hitGoalCounter;
        newGameStatistic.hitPitCounter = gameStatistic.hitPitCounter;
        newGameStatistic.hitWallCounter = gameStatistic.hitWallCounter;
        newGameStatistic.maxMoveCounter = gameStatistic.maxMoveCounter;
        newGameStatistic.fitness = gameStatistic.fitness;

        newGameStatistic.actionResult = gameStatistic.actionResult;
        newGameStatistic.mse = gameStatistic.mse;
        newGameStatistic.epoche = gameStatistic.epoche;
        newGameStatistic.level = gameStatistic.level;

        newGameStatistic.fittnesCounter = gameStatistic.fittnesCounter;
        newGameStatistic.fittnesHitGoalCounter = gameStatistic.fittnesHitGoalCounter;

        newGameStatistic.learningRate = gameStatistic.learningRate;
        newGameStatistic.momentum = gameStatistic.momentum;

        return newGameStatistic;
    }

    private static MlpNet copyNet(final MlpNet fittestNet) {
        return MlpNetService.duplicateNet(fittestNet);
    }

    private static float calcFit(final int counter, final int moveCounter) {
        final float mg = moveCounter > 0 ? (float) counter / moveCounter : 0.0F;
        return mg;
    }

    private static void resetGameStatistic(final GameStatistic gameStatistic) {
        gameStatistic.moveCounter = 0;
        gameStatistic.hitGoalCounter = 0;
        gameStatistic.hitPitCounter = 0;
        gameStatistic.hitWallCounter = 0;
        gameStatistic.maxMoveCounter = 0;
        gameStatistic.fitness = 0.0F;
    }
}
