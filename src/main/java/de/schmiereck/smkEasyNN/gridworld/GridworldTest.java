package de.schmiereck.smkEasyNN.gridworld;

import static de.schmiereck.smkEasyNN.gridworld.GridworldBoardService.initBoard;
import static de.schmiereck.smkEasyNN.gridworld.GridworldBoardService.printBoard;
import static de.schmiereck.smkEasyNN.gridworld.GridworldGameService.MovePosDown;
import static de.schmiereck.smkEasyNN.gridworld.GridworldGameService.MovePosLeft;
import static de.schmiereck.smkEasyNN.gridworld.GridworldGameService.MovePosRight;
import static de.schmiereck.smkEasyNN.gridworld.GridworldGameService.MovePosUp;

import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GridworldTest {
    @Test
    void GIVEN_level_0_THEN_test_Gridworld() {
        final Random rnd = new Random(12345);

        final Board board = new Board();

        final int level = 0;
        initBoard(board, level, rnd);

        printBoard(board);

        {
            final GridworldGameService.ActionResult actionResult = GridworldGameService.runPlayerAction(board, MovePosUp);
            printBoard(board);
            Assertions.assertEquals(GridworldGameService.ActionResult.MovedPit, actionResult);
        }
        {
            final GridworldGameService.ActionResult actionResult = GridworldGameService.runPlayerAction(board, MovePosDown);
            printBoard(board);
            Assertions.assertEquals(GridworldGameService.ActionResult.Moved, actionResult);
        }
        {
            final GridworldGameService.ActionResult actionResult = GridworldGameService.runPlayerAction(board, MovePosLeft);
            printBoard(board);
            Assertions.assertEquals(GridworldGameService.ActionResult.Moved, actionResult);
        }
        {
            final GridworldGameService.ActionResult actionResult = GridworldGameService.runPlayerAction(board, MovePosLeft);
            printBoard(board);
            Assertions.assertEquals(GridworldGameService.ActionResult.HitWall, actionResult);
        }
        {
            final GridworldGameService.ActionResult actionResult = GridworldGameService.runPlayerAction(board, MovePosDown);
            printBoard(board);
            Assertions.assertEquals(GridworldGameService.ActionResult.Moved, actionResult);
        }
        {
            final GridworldGameService.ActionResult actionResult = GridworldGameService.runPlayerAction(board, MovePosRight);
            printBoard(board);
            Assertions.assertEquals(GridworldGameService.ActionResult.MovedGoal, actionResult);
        }
        {
            final GridworldGameService.ActionResult actionResult = GridworldGameService.runPlayerAction(board, MovePosRight);
            printBoard(board);
            Assertions.assertEquals(GridworldGameService.ActionResult.Moved, actionResult);
        }
        {
            final GridworldGameService.ActionResult actionResult = GridworldGameService.runPlayerAction(board, MovePosRight);
            printBoard(board);
            Assertions.assertEquals(GridworldGameService.ActionResult.Moved, actionResult);
        }
        {
            final GridworldGameService.ActionResult actionResult = GridworldGameService.runPlayerAction(board, MovePosUp);
            printBoard(board);
            Assertions.assertEquals(GridworldGameService.ActionResult.HitWall, actionResult);
        }
        {
            final GridworldGameService.ActionResult actionResult = GridworldGameService.runPlayerAction(board, MovePosDown);
            printBoard(board);
            Assertions.assertEquals(GridworldGameService.ActionResult.Moved, actionResult);
        }
        {
            final GridworldGameService.ActionResult actionResult = GridworldGameService.runPlayerAction(board, MovePosDown);
            printBoard(board);
            Assertions.assertEquals(GridworldGameService.ActionResult.Moved, actionResult);
        }
        {
            final GridworldGameService.ActionResult actionResult = GridworldGameService.runPlayerAction(board, MovePosDown);
            printBoard(board);
            Assertions.assertEquals(GridworldGameService.ActionResult.HitWall, actionResult);
        }
        {
            final GridworldGameService.ActionResult actionResult = GridworldGameService.runPlayerAction(board, MovePosLeft);
            printBoard(board);
            Assertions.assertEquals(GridworldGameService.ActionResult.Moved, actionResult);
        }
        {
            final GridworldGameService.ActionResult actionResult = GridworldGameService.runPlayerAction(board, MovePosLeft);
            printBoard(board);
            Assertions.assertEquals(GridworldGameService.ActionResult.MovedPit, actionResult);
        }
    }

    @Test
    void GIVEN_level_1_THEN_test_Gridworld() {
        final Random rnd = new Random(12345);

        final Board board = new Board();

        final int level = 1;
        {
            initBoard(board, level, rnd);

            printBoard(board);

            {
                final GridworldGameService.ActionResult actionResult = GridworldGameService.runPlayerAction(board, MovePosRight);
                printBoard(board);
                Assertions.assertEquals(GridworldGameService.ActionResult.MovedGoal, actionResult);
            }
        }
        System.out.println();
        {
            initBoard(board, level, rnd);

            printBoard(board);

            {
                final GridworldGameService.ActionResult actionResult = GridworldGameService.runPlayerAction(board, MovePosDown);
                printBoard(board);
                Assertions.assertEquals(GridworldGameService.ActionResult.MovedGoal, actionResult);
            }
        }
        System.out.println();
        {
            initBoard(board, level, rnd);

            printBoard(board);

            {
                final GridworldGameService.ActionResult actionResult = GridworldGameService.runPlayerAction(board, MovePosDown);
                printBoard(board);
                Assertions.assertEquals(GridworldGameService.ActionResult.MovedGoal, actionResult);
            }
        }
        System.out.println();
        {
            initBoard(board, level, rnd);

            printBoard(board);

            {
                final GridworldGameService.ActionResult actionResult = GridworldGameService.runPlayerAction(board, MovePosDown);
                printBoard(board);
                Assertions.assertEquals(GridworldGameService.ActionResult.MovedGoal, actionResult);
            }
        }
        System.out.println();
        {
            initBoard(board, level, rnd);

            printBoard(board);

            {
                final GridworldGameService.ActionResult actionResult = GridworldGameService.runPlayerAction(board, MovePosRight);
                printBoard(board);
                Assertions.assertEquals(GridworldGameService.ActionResult.MovedGoal, actionResult);
            }
        }
        System.out.println();
        {
            initBoard(board, level, rnd);

            printBoard(board);

            {
                final GridworldGameService.ActionResult actionResult = GridworldGameService.runPlayerAction(board, MovePosRight);
                printBoard(board);
                Assertions.assertEquals(GridworldGameService.ActionResult.MovedGoal, actionResult);
            }
        }
        System.out.println();
        {
            initBoard(board, level, rnd);

            printBoard(board);

            {
                final GridworldGameService.ActionResult actionResult = GridworldGameService.runPlayerAction(board, MovePosUp);
                printBoard(board);
                Assertions.assertEquals(GridworldGameService.ActionResult.MovedGoal, actionResult);
            }
        }
        System.out.println();
        {
            initBoard(board, level, rnd);

            printBoard(board);

            {
                final GridworldGameService.ActionResult actionResult = GridworldGameService.runPlayerAction(board, MovePosDown);
                printBoard(board);
                Assertions.assertEquals(GridworldGameService.ActionResult.MovedGoal, actionResult);
            }
            System.out.println();
            {
                initBoard(board, level, rnd);

                printBoard(board);

                {
                    final GridworldGameService.ActionResult actionResult = GridworldGameService.runPlayerAction(board, MovePosLeft);
                    printBoard(board);
                    Assertions.assertEquals(GridworldGameService.ActionResult.MovedGoal, actionResult);
                }
            }
        }
        System.out.println();
        {
            initBoard(board, level, rnd);

            printBoard(board);

            {
                final GridworldGameService.ActionResult actionResult = GridworldGameService.runPlayerAction(board, MovePosUp);
                printBoard(board);
                Assertions.assertEquals(GridworldGameService.ActionResult.MovedGoal, actionResult);
            }
        }
    }

    @Test
    void GIVEN_level_2_THEN_test_Gridworld() {
        final Random rnd = new Random(12345);

        final Board board = new Board();

        final int level = 2;
        {
            initBoard(board, level, rnd);

            printBoard(board);

            {
                final GridworldGameService.ActionResult actionResult = GridworldGameService.runPlayerAction(board, MovePosLeft);
                printBoard(board);
                Assertions.assertEquals(GridworldGameService.ActionResult.MovedGoal, actionResult);
            }
        }
        System.out.println();
        {
            initBoard(board, level, rnd);

            printBoard(board);

            {
                final GridworldGameService.ActionResult actionResult = GridworldGameService.runPlayerAction(board, MovePosRight);
                printBoard(board);
                Assertions.assertEquals(GridworldGameService.ActionResult.Moved, actionResult);
            }
            {
                final GridworldGameService.ActionResult actionResult = GridworldGameService.runPlayerAction(board, MovePosRight);
                printBoard(board);
                Assertions.assertEquals(GridworldGameService.ActionResult.MovedGoal, actionResult);
            }
        }
        System.out.println();
        {
            initBoard(board, level, rnd);

            printBoard(board);

            {
                final GridworldGameService.ActionResult actionResult = GridworldGameService.runPlayerAction(board, MovePosDown);
                printBoard(board);
                Assertions.assertEquals(GridworldGameService.ActionResult.MovedGoal, actionResult);
            }
        }
        System.out.println();
        {
            initBoard(board, level, rnd);

            printBoard(board);

            {
                final GridworldGameService.ActionResult actionResult = GridworldGameService.runPlayerAction(board, MovePosLeft);
                printBoard(board);
                Assertions.assertEquals(GridworldGameService.ActionResult.Moved, actionResult);
            }
            {
                final GridworldGameService.ActionResult actionResult = GridworldGameService.runPlayerAction(board, MovePosLeft);
                printBoard(board);
                Assertions.assertEquals(GridworldGameService.ActionResult.MovedGoal, actionResult);
            }
        }
        System.out.println();
        {
            initBoard(board, level, rnd);

            printBoard(board);

            {
                final GridworldGameService.ActionResult actionResult = GridworldGameService.runPlayerAction(board, MovePosUp);
                printBoard(board);
                Assertions.assertEquals(GridworldGameService.ActionResult.MovedGoal, actionResult);
            }
        }
    }
}
