package de.schmiereck.smkEasyNN.gridworld;

import static de.schmiereck.smkEasyNN.gridworld.GridworldBoardService.initBoard;
import static de.schmiereck.smkEasyNN.mlp.MlpLayerService.addForwwardInputs;

import de.schmiereck.smkEasyNN.mlp.MlpConfiguration;
import de.schmiereck.smkEasyNN.mlp.MlpNet;
import de.schmiereck.smkEasyNN.mlp.MlpNetService;

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
        final int[] layerSizeArr = new int[]{ 64 + 1, 64, 32, 32, 32, 32, 4 }; // Best 4

        final Random rnd = new Random(12345);
        //final Random rnd = new Random();

        final int netCount = 3;
        // final MlpConfiguration config = new MlpConfiguration(true, false, 4.0F); -> Infinite Error/Weight Sum.
        final MlpConfiguration config = new MlpConfiguration(true, false, 1.0F);

        final MlpNet[] netArr = new MlpNet[netCount];
        final GameStatistic[] gameStatisticArr = new GameStatistic[netCount];
        final int[] levelArr = new int[netCount];
        final int[] fittnesCounterArr = new int[netCount];
        final int[] hitGoalCounterArr = new int[netCount];
        final int[] epocheArr = new int[netCount];

        for (int netPos = 0; netPos < netArr.length; netPos++) {
            netArr[netPos] = MlpNetService.createNet(config, layerSizeArr, rnd);
            gameStatisticArr[netPos] = new GameStatistic();

                    //addForwwardInputs(netArr[netPos], 2, 1, rnd);
            //addForwwardInputs(netArr[netPos], 3, 2, rnd);
            addForwwardInputs(netArr[netPos], 2, 4, rnd);
            addForwwardInputs(netArr[netPos], 3, 5, rnd);

            levelArr[netPos] = 0;
            fittnesCounterArr[netPos] = 0;
            hitGoalCounterArr[netPos] = 0;
            epocheArr[netPos] = 0;
        }

        for (int runPos = 0; runPos < 20_000_000; runPos++) {
            for (int netPos = 0; netPos < netArr.length; netPos++) {
                final MlpNet net = netArr[netPos];
                    final GameStatistic gameStatistic = gameStatisticArr[netPos];

                while (true) {
                    final Board board = new Board();
                    int oldLevel = levelArr[netPos];
                    if (hitGoalCounterArr[netPos] > 6) {
                        hitGoalCounterArr[netPos] = 0;
                        fittnesCounterArr[netPos]++;
                        if (fittnesCounterArr[netPos] > 4) {
                            fittnesCounterArr[netPos] = 0;
                            levelArr[netPos]++;
                        }
                    }

                    initBoard(board, levelArr[netPos], rnd);

                    //printBoard(board);
                    final boolean newLevel = (oldLevel != levelArr[netPos]);

                    if ((epocheArr[netPos] % 100 == 0) || newLevel) {
                        System.out.printf("%2d - %9d: level:%3d  moves:%9d [goal:%6d, pit:%6d, wall:%6d, max-move:%6d]\r", netPos, epocheArr[netPos], oldLevel, gameStatistic.moveCounter, gameStatistic.hitGoalCounter, gameStatistic.hitPitCounter, gameStatistic.hitWallCounter, gameStatistic.maxMoveCounter);
                        if (oldLevel != levelArr[netPos]) {
                            gameStatistic.moveCounter = 0;
                            gameStatistic.hitGoalCounter = 0;
                            gameStatistic.hitPitCounter = 0;
                            gameStatistic.hitWallCounter = 0;
                            gameStatistic.maxMoveCounter = 0;
                            System.out.println();
                        }
                    }

                    if (newLevel) {
                        break;
                    }

                    hitGoalCounterArr[netPos] = GridworldGameService.runPlayGame(net, board, levelArr[netPos], gameStatistic, hitGoalCounterArr[netPos], rnd);

                    epocheArr[netPos]++;
                }
            }
        }
    }
}
