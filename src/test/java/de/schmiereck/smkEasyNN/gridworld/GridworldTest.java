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

        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[]{
                    new TestData(MovePosUp, GridworldGameService.ActionResult.MovedPit),
                    new TestData(MovePosDown, GridworldGameService.ActionResult.Moved),
                    new TestData(MovePosLeft, GridworldGameService.ActionResult.Moved),
                    new TestData(MovePosLeft, GridworldGameService.ActionResult.HitWall),
                    new TestData(MovePosDown, GridworldGameService.ActionResult.Moved),
                    new TestData(MovePosRight, GridworldGameService.ActionResult.MovedGoal),
            });
        }
    }

    @Test
    void GIVEN_level_1_THEN_test_Gridworld() {
        final Random rnd = new Random(12345);

        final Board board = new Board();

        final int level = 1;
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[]{
                    new TestData(MovePosRight, GridworldGameService.ActionResult.MovedGoal),
            });
        }
        System.out.println();
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[]{
                    new TestData(MovePosDown, GridworldGameService.ActionResult.MovedGoal),
            });
        }
        System.out.println();
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[]{
                    new TestData(MovePosDown, GridworldGameService.ActionResult.MovedGoal),
            });
        }
        System.out.println();
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[]{
                    new TestData(MovePosDown, GridworldGameService.ActionResult.MovedGoal),
            });
        }
        System.out.println();
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[]{
                    new TestData(MovePosRight, GridworldGameService.ActionResult.MovedGoal),
            });
        }
        System.out.println();
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[]{
                    new TestData(MovePosRight, GridworldGameService.ActionResult.MovedGoal),
            });
        }
        System.out.println();
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[]{
                    new TestData(MovePosUp, GridworldGameService.ActionResult.MovedGoal),
            });
        }
        System.out.println();
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[]{
                    new TestData(MovePosDown, GridworldGameService.ActionResult.MovedGoal),
            });
        }
        System.out.println();
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[]{
                    new TestData(MovePosLeft, GridworldGameService.ActionResult.MovedGoal),
            });
        }
        System.out.println();
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[]{
                    new TestData(MovePosUp, GridworldGameService.ActionResult.MovedGoal),
            });
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
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[]{
                    new TestData(MovePosRight, GridworldGameService.ActionResult.Moved),
                    new TestData(MovePosRight, GridworldGameService.ActionResult.MovedGoal),
            });
        }
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[]{
                    new TestData(MovePosUp, GridworldGameService.ActionResult.MovedGoal),
            });
        }
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[]{
                    new TestData(MovePosRight, GridworldGameService.ActionResult.Moved),
                    new TestData(MovePosRight, GridworldGameService.ActionResult.MovedGoal),
            });
        }
    }

    @Test
    void GIVEN_level_3_THEN_test_Gridworld() {
        final Random rnd = new Random(12345);

        final Board board = new Board();

        final int level = 3;
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[] { new TestData(MovePosDown, GridworldGameService.ActionResult.MovedGoal) });
        }
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[] { new TestData(MovePosRight, GridworldGameService.ActionResult.MovedGoal) });
        }
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[] {
                    new TestData(MovePosLeft, GridworldGameService.ActionResult.MovedGoal)});
        }
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[] {
                    new TestData(MovePosUp, GridworldGameService.ActionResult.MovedGoal)
            });
        }
    }

    @Test
    void GIVEN_level_4_THEN_test_Gridworld() {
        final Random rnd = new Random(12345);

        final Board board = new Board();

        final int level = 4;
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[]{
                    new TestData(MovePosRight, GridworldGameService.ActionResult.Moved),
                    new TestData(MovePosDown, GridworldGameService.ActionResult.MovedGoal)
            });
        }
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[]{
                    new TestData(MovePosDown, GridworldGameService.ActionResult.MovedGoal)
            });
        }
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[]{
                    new TestData(MovePosDown, GridworldGameService.ActionResult.MovedGoal)
            });
        }
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[]{
                    new TestData(MovePosDown, GridworldGameService.ActionResult.Moved),
                    new TestData(MovePosLeft, GridworldGameService.ActionResult.MovedGoal),
            });
        }
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[]{
                    new TestData(MovePosLeft, GridworldGameService.ActionResult.MovedGoal),
            });
        }
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[]{
                    new TestData(MovePosLeft, GridworldGameService.ActionResult.MovedGoal),
            });
        }
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[]{
                    new TestData(MovePosLeft, GridworldGameService.ActionResult.Moved),
                    new TestData(MovePosDown, GridworldGameService.ActionResult.MovedGoal),
            });
        }
    }

    @Test
    void GIVEN_level_5_THEN_test_Gridworld() {
        final Random rnd = new Random(12345);

        final Board board = new Board();

        final int level = 5;
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[]{
                    new TestData(MovePosDown, GridworldGameService.ActionResult.Moved),
                    new TestData(MovePosDown, GridworldGameService.ActionResult.Moved),
                    new TestData(MovePosRight, GridworldGameService.ActionResult.MovedGoal)
            });
        }
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[]{
                    new TestData(MovePosUp, GridworldGameService.ActionResult.Moved),
                    new TestData(MovePosRight, GridworldGameService.ActionResult.MovedGoal)
            });
        }
    }

    @Test
    void GIVEN_level_7_THEN_test_Gridworld() {
        final Random rnd = new Random(12345);

        final Board board = new Board();

        final int level = 7;
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[]{
                    new TestData(MovePosRight, GridworldGameService.ActionResult.MovedGoal)
            });
        }
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[]{
                    new TestData(MovePosUp, GridworldGameService.ActionResult.MovedGoal)
            });
        }
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[]{
                    new TestData(MovePosUp, GridworldGameService.ActionResult.MovedGoal)
            });
        }
    }

    @Test
    void GIVEN_level_8_THEN_test_Gridworld() {
        final Random rnd = new Random(12345);

        final Board board = new Board();

        final int level = 8;
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[]{
                    new TestData(MovePosUp, GridworldGameService.ActionResult.Moved),
                    new TestData(MovePosUp, GridworldGameService.ActionResult.MovedGoal)
            });
        }
    }

    @Test
    void GIVEN_level_10_THEN_test_Gridworld() {
        final Random rnd = new Random(12345);

        final Board board = new Board();

        final int level = 10;
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[]{
                    new TestData(MovePosUp, GridworldGameService.ActionResult.Moved),
                    new TestData(MovePosUp, GridworldGameService.ActionResult.Moved),
                    new TestData(MovePosUp, GridworldGameService.ActionResult.Moved),
                    new TestData(MovePosRight, GridworldGameService.ActionResult.Moved),
                    new TestData(MovePosRight, GridworldGameService.ActionResult.Moved),
                    new TestData(MovePosRight, GridworldGameService.ActionResult.MovedGoal)
            });
        }
    }

    @Test
    void GIVEN_level_1_THEN_test_Gridworld_End() {
        final Random rnd = new Random(12345);

        final Board board = new Board();

        final int level = 1;
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[]{
                    new TestData(MovePosRight, GridworldGameService.ActionResult.MovedGoal),
            });
        }
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[]{
                    new TestData(MovePosDown, GridworldGameService.ActionResult.MovedGoal),
            });
        }
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[]{
                    new TestData(MovePosDown, GridworldGameService.ActionResult.MovedGoal),
            });
        }
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[]{
                    new TestData(MovePosDown, GridworldGameService.ActionResult.MovedGoal),
            });
        }
        {
            initBoard(board, level, rnd);
            assertActionResults(board, new TestData[]{
                    new TestData(MovePosRight, GridworldGameService.ActionResult.MovedGoal),
            });
        }
    }

    private static class TestData {
        final int movePos;
        final GridworldGameService.ActionResult actionResult;


        private TestData(int movePos, GridworldGameService.ActionResult actionResult) {
            this.movePos = movePos;
            this.actionResult = actionResult;
        }
    }

    private void assertActionResults(final Board board, final TestData[] testDataArr) {
        System.out.println();
        printBoard(board);

        GridworldGameService.ActionResult lastActionResult = null;

        for (int pos = 0; pos < testDataArr.length; pos++) {
            final GridworldGameService.ActionResult actionResult = GridworldGameService.runPlayerAction(board, testDataArr[pos].movePos);
            printBoard(board);
            Assertions.assertEquals(testDataArr[pos].actionResult, actionResult);
            lastActionResult = actionResult;
        }
        Assertions.assertEquals(GridworldGameService.ActionResult.MovedGoal, lastActionResult);
    }
}
