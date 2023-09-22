package de.schmiereck.smkEasyNN.gridworld;

import static de.schmiereck.smkEasyNN.gridworld.GridworldBoardService.initBoard;
import static de.schmiereck.smkEasyNN.mlp.MlpLayerService.addForwwardInputs;

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

        final Random rnd = new Random(12345);
        //final Random rnd = new Random();

        final int netCount = 6;
        // final MlpConfiguration config = new MlpConfiguration(true, false, 4.0F); -> Infinite Error/Weight Sum.
        final MlpConfiguration config = new MlpConfiguration(true, false, 1.0F);

        //final MlpNet[] netArr = new MlpNet[netCount];
        final HashMap<GameStatistic, MlpNet> netArr = new HashMap<>();
        //final GameStatistic[] gameStatisticArr = new GameStatistic[netCount];
        //final int[] levelArr = new int[netCount];
        //final int[] fittnesCounterArr = new int[netCount];
        //final int[] fittnesHitGoalCounterArr = new int[netCount];
        //final int[] epocheArr = new int[netCount];

        for (int netPos = 0; netPos < netCount; netPos++) {
            final MlpLayerConfig[] layerConfigArr = new MlpLayerConfig[9];
            layerConfigArr[0] = new MlpLayerConfig(64 + 1);
            layerConfigArr[1] = new MlpLayerConfig(64);
            layerConfigArr[2] = new MlpLayerConfig(32);
            layerConfigArr[3] = new MlpLayerConfig(32);

            layerConfigArr[4] = new MlpLayerConfig(32);
            layerConfigArr[5] = new MlpLayerConfig(32);
            layerConfigArr[6] = new MlpLayerConfig(32);
            layerConfigArr[7] = new MlpLayerConfig(32);
            layerConfigArr[8] = new MlpLayerConfig(4);

            layerConfigArr[0].setIsArray(true);
            layerConfigArr[1].setIsArray(true);
            layerConfigArr[2].setIsArray(true);
            layerConfigArr[3].setIsArray(true);

            final MlpNet net = MlpNetService.createNet(config, layerConfigArr, rnd);
            //gameStatisticArr[netPos] = new GameStatistic(netPos);
            final GameStatistic gameStatistic = new GameStatistic(netPos);

                    //addForwwardInputs(netArr[netPos], 2, 1, rnd);
            //addForwwardInputs(netArr[netPos], 3, 2, rnd);
            addForwwardInputs(net, 5, 4, false, false, true, rnd);
            addForwwardInputs(net, 6, 5, false, false, true, rnd);

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
                final float m = (gameStatistic.hitGoalCounter +
                        gameStatistic.hitPitCounter +
                        gameStatistic.maxMoveCounter +
                        gameStatistic.hitWallCounter) / gameStatistic.moveCounter;
                // 1: more of these results, 0: no results
                // moves / goal
                final float mg = calcFit(gameStatistic.hitGoalCounter, gameStatistic.moveCounter);
                // moves / pit
                final float mp = calcFit(gameStatistic.hitPitCounter, gameStatistic.moveCounter);
                // moves / maxMove
                final float mm = calcFit(gameStatistic.maxMoveCounter, gameStatistic.moveCounter);
                // moves / wall
                final float mw = calcFit(gameStatistic.hitWallCounter, gameStatistic.moveCounter);

                // bigger is fitter.
                gameStatistic.fitness = m + (mg * 2.0F) + (2.0F - (mp * 2.0F)) + (0.5F - (mm * 0.5F)) + (0.25F - (mw * 0.25F));

                System.out.printf(" fit:%.6f", gameStatistic.fitness);
                System.out.println();
            }
            // Found fittest (fittest first):
            final List<GameStatistic> sortedGameStatisticList =
                    netArr.keySet().stream()
                            .sorted((aGameStatistic, bGameStatistic) -> Float.compare(bGameStatistic.fitness, aGameStatistic.fitness)).toList();

            final GameStatistic fittestGameStatistic = sortedGameStatisticList.get(0);
            final GameStatistic worstGameStatistic = sortedGameStatisticList.get(sortedGameStatisticList.size() - 1);

            final MlpNet fittestNet = netArr.get(fittestGameStatistic);
            final MlpNet newNet = copyNet(fittestNet);
            final GameStatistic newGameStatistic = copyGameStatistic(newNetPos, fittestGameStatistic);
            newNetPos++;

            netArr.remove(worstGameStatistic);
            netArr.put(newGameStatistic, newNet);
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
