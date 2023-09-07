package de.schmiereck.smkEasyNN.gridworld;

import static de.schmiereck.smkEasyNN.gridworld.GridworldGameService.ElementPosGoal;
import static de.schmiereck.smkEasyNN.gridworld.GridworldGameService.ElementPosPit;
import static de.schmiereck.smkEasyNN.gridworld.GridworldGameService.ElementPosPlayer;
import static de.schmiereck.smkEasyNN.gridworld.GridworldGameService.ElementPosWall;
import static de.schmiereck.smkEasyNN.gridworld.GridworldGameService.wrapBoardPos;

import de.schmiereck.smkEasyNN.gridworld.GridworldGameService.Pos;

import java.util.Random;

public class GridworldBoardService {

    static void printBoard(final Board board) {
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

    static void initBoard(final Board board, final int level, final Random rnd) {
        for (int yBoardPos = 0; yBoardPos < 4; yBoardPos++) {
            for (int xBoardPos = 0; xBoardPos < 4; xBoardPos++) {
                board.board[yBoardPos][xBoardPos][ElementPosPlayer] = false;
                board.board[yBoardPos][xBoardPos][ElementPosWall] = false;
                board.board[yBoardPos][xBoardPos][ElementPosPit] = false;
                board.board[yBoardPos][xBoardPos][ElementPosGoal] = false;
            }
        }

        if (level == 0) {
            board.board[2][3][ElementPosPlayer] = true;
            board.board[2][1][ElementPosWall] = true;
            board.board[1][3][ElementPosPit] = true;
            board.board[3][3][ElementPosGoal] = true;
        } else {
            if (level == 1) {
                // init randomly player pos with distance 1
                board.board[2][1][ElementPosWall] = true;
                board.board[1][3][ElementPosPit] = true;
                board.board[3][3][ElementPosGoal] = true;
                initBoardField(board, ElementPosPlayer, new Pos(3, 3), 1, true, rnd);
            } else {
                if (level == 2) {
                    // init randomly player pos with distance 2
                    board.board[2][1][ElementPosWall] = true;
                    board.board[1][3][ElementPosPit] = true;
                    board.board[3][3][ElementPosGoal] = true;
                    initBoardField(board, ElementPosPlayer, new Pos(3, 3), 2, true, rnd);
                } else {
                    if (level == 3) {
                        // init randomly player pos with distance 3
                        board.board[2][1][ElementPosWall] = true;
                        board.board[1][3][ElementPosPit] = true;
                        board.board[3][3][ElementPosGoal] = true;
                        initBoardField(board, ElementPosPlayer, new Pos(3, 3), 3, true, rnd);
                    } else {
                        if (level == 4) {
                            // init randomly player on a not so easy pos with distance 1
                            board.board[2][1][ElementPosWall] = true;
                            board.board[1][3][ElementPosPit] = true;
                            board.board[3][3][ElementPosGoal] = true;
                            initBoardField(board, ElementPosPlayer, new Pos(3, 3), 1, false, rnd);
                        } else {
                            if (level == 5) {
                                // init randomly player on a not so easy pos with distance 2
                                board.board[2][1][ElementPosWall] = true;
                                board.board[1][3][ElementPosPit] = true;
                                board.board[3][3][ElementPosGoal] = true;
                                initBoardField(board, ElementPosPlayer, new Pos(3, 3), 2, false, rnd);
                            } else {
                                if (level == 6) {
                                    // init randomly player  on a not so easypos with distance 3
                                    board.board[2][1][ElementPosWall] = true;
                                    board.board[1][3][ElementPosPit] = true;
                                    board.board[3][3][ElementPosGoal] = true;
                                    initBoardField(board, ElementPosPlayer, new Pos(3, 3), 3, false, rnd);
                                } else {
                                    if (level <= 9) {
                                        // init randomly fields with increasing distance between player and goal
                                        initBoardField(board, ElementPosWall, rnd);
                                        initBoardField(board, ElementPosPit, rnd);
                                        final Pos goalPos = initBoardField(board, ElementPosGoal, rnd);
                                        final int distance = Math.min(level - 6, 4);
                                        initBoardField(board, ElementPosPlayer, goalPos, distance, true, rnd);
                                    } else {
                                        // init randomly fields with increasing distance between player and goal
                                        initBoardField(board, ElementPosWall, rnd);
                                        initBoardField(board, ElementPosPit, rnd);
                                        final Pos goalPos = initBoardField(board, ElementPosGoal, rnd);
                                        final int distance = Math.min(level - 9, 4);
                                        initBoardField(board, ElementPosPlayer, goalPos, distance, false, rnd);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static Pos initBoardField(final Board board, final int elementPos, final Pos pos, final int distance, final boolean useEasy, final Random rnd) {
        while (true) {
            final int posX;
            final int posY;
            if (useEasy) {
                if (rnd.nextBoolean()) {
                    posX = wrapBoardPos(pos.x + rnd.nextInt(distance * 2 + 1) - distance);
                    posY = pos.y;
                } else {
                    posX = pos.x;
                    posY = wrapBoardPos(pos.y + rnd.nextInt(distance * 2 + 1) - distance);
                }
            } else {
                posX = wrapBoardPos(pos.x + rnd.nextInt(distance * 2 + 1) - distance);
                posY = wrapBoardPos(pos.y + rnd.nextInt(distance * 2 + 1) - distance);
            }

            if (checkFieldEmpty(board, posX, posY)) {
                board.board[posY][posX][elementPos] = true;
                return new Pos(posX, posY);
            }
        }
    }

    private static Pos initBoardField(final Board board, final int elementPos, final Random rnd) {
        while (true) {
            final int posX = rnd.nextInt(4);
            final int posY = rnd.nextInt(4);

            if (checkFieldEmpty(board, posX, posY)) {
                board.board[posY][posX][elementPos] = true;
                return new Pos(posX, posY);
            }
        }
    }

    private static boolean checkFieldEmpty(final Board board, final int posX, final int posY) {
        boolean foundElement = false;
        for (int elementPos = 0; elementPos < 4; elementPos++) {
            if (board.board[posY][posX][elementPos]) {
                foundElement = true;
                break;
            }
        }
        return !foundElement;
    }

    private static int calcFieldElementPos(final Board board, final int posX, final int posY) {
        int retElementPos = -1;
        for (int elementPos = 0; elementPos < 4; elementPos++) {
            if (board.board[posY][posX][elementPos]) {
                retElementPos = elementPos;
                break;
            }
        }
        return retElementPos;
    }
}
