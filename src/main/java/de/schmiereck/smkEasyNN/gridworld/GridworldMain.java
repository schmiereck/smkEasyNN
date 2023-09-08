package de.schmiereck.smkEasyNN.gridworld;

import static de.schmiereck.smkEasyNN.gridworld.GridworldBoardService.initBoard;
import static de.schmiereck.smkEasyNN.mlp.MlpService.addForwwardInputs;

import de.schmiereck.smkEasyNN.mlp.MlpConfiguration;
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

        // final MlpConfiguration config = new MlpConfiguration(true, false, 4.0F); -> Infinite Error/Weight Sum.
        final MlpConfiguration config = new MlpConfiguration(true, false, 1.0F);
        final MlpNet net = new MlpNet(config, layerSizeArr, rnd);

        //addForwwardInputs(net, 2, 1, rnd);
        //addForwwardInputs(net, 3, 2, rnd);
        addForwwardInputs(net, 2, 4, rnd);
        addForwwardInputs(net, 3, 5, rnd);

        final GameStatistic gameStatistic = new GameStatistic();
        int level = 0;
        int fittnesCounter = 0;
        int hitGoalCounter = 0;
        int epoche = 0;
        while (true) {
            final Board board = new Board();
            int oldLevel = level;
            if (hitGoalCounter > 6) {
                hitGoalCounter = 0;
                fittnesCounter++;
                if (fittnesCounter > 4) {
                    fittnesCounter = 0;
                    level++;
                }
            }

            initBoard(board, level, rnd);

            //printBoard(board);

            if ((epoche % 100 == 0) || (oldLevel != level)) {
                System.out.printf("%9d: level:%3d  moves:%9d [goal:%6d, pit:%6d, wall:%6d, max-move:%6d]\r", epoche, oldLevel, gameStatistic.moveCounter, gameStatistic.hitGoalCounter, gameStatistic.hitPitCounter, gameStatistic.hitWallCounter, gameStatistic.maxMoveCounter);
                if (oldLevel != level) {
                    gameStatistic.moveCounter = 0;
                    gameStatistic.hitGoalCounter = 0;
                    gameStatistic.hitPitCounter = 0;
                    gameStatistic.hitWallCounter = 0;
                    gameStatistic.maxMoveCounter = 0;
                    System.out.println();
                }
            }

            hitGoalCounter = GridworldGameService.runPlayGame(net, board, level, gameStatistic, hitGoalCounter, rnd);

            epoche++;
        }
    }
}
