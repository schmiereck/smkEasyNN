package de.schmiereck.smkEasyNN.wave;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class CellImpulseElectronSimulation extends JPanel {
        private static final int VIEW_WIDTH = 1400;
        private static final int VIEW_HEIGHT = 600;

        private static final double DT = 0.00000001;
        private static final int TIMESTEPS = (int) (1.0D / DT);

        private static final int PsiArrSize = 700/2;

        private final static int DIR_SIZE = 2;
        private final static long MAX_DIV = 64;
        private final static long MAX_SPEED_C = 16;

        private class Cell {
            int dirPos = 1;
            long count;
            /**
             * Count of divisions.
             * div = div/2, div = div/2, div = div/2, ...
             */
            long div = 1;
            
            long[] speedArr = new long[DIR_SIZE];
            long[] speedCntArr = new long[DIR_SIZE];
            int speedDirPos = 0;
        }

        private final Cell[][] psiArr;
        private int psiPos = 0;

        public static void main(String[] args) {
            final JFrame frame = new JFrame("Cell Free Electron Simulation");

            final CellImpulseElectronSimulation simulation = new CellImpulseElectronSimulation();

            frame.add(simulation);
            frame.setSize(VIEW_WIDTH, VIEW_HEIGHT);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

            new Thread(simulation::simulate).start();
        }

        public CellImpulseElectronSimulation() {
            this.psiArr = new Cell[2][PsiArrSize];

            for (int nextArrPos = 0; nextArrPos < PsiArrSize; nextArrPos++) {
                this.psiArr[this.psiPos][nextArrPos] = new Cell();
                this.psiArr[1][nextArrPos] = new Cell();

                if (nextArrPos == ((PsiArrSize / 4) * 1)) {
                    this.psiArr[this.psiPos][nextArrPos].count = 1;
                    this.psiArr[this.psiPos][nextArrPos].div = 1;
                    this.psiArr[this.psiPos][nextArrPos].speedArr[0] = 0;
                    this.psiArr[this.psiPos][nextArrPos].speedArr[1] = 8;
                    this.psiArr[this.psiPos][nextArrPos].speedCntArr[0] = 0;
                    this.psiArr[this.psiPos][nextArrPos].speedCntArr[1] = 0;
                } else {
                    if (nextArrPos == ((PsiArrSize / 4) * 3)) {
                        this.psiArr[this.psiPos][nextArrPos].count = 1;
                        this.psiArr[this.psiPos][nextArrPos].div = 1;
                        this.psiArr[this.psiPos][nextArrPos].speedArr[0] = 6;
                        this.psiArr[this.psiPos][nextArrPos].speedArr[1] = 0;
                        this.psiArr[this.psiPos][nextArrPos].speedCntArr[0] = 0;
                        this.psiArr[this.psiPos][nextArrPos].speedCntArr[1] = 0;
                    } else {
                        this.psiArr[this.psiPos][nextArrPos].count = 0;
                    }
                }
            }
        }

        public void simulate() {
            System.out.println("START: Simulating...");

            for (int t = 0; t < TIMESTEPS * 1000; t++) {
                final int nextPsiPos = (this.psiPos + 1) % 2;

                for (int psiArrPos = 0; psiArrPos < PsiArrSize; psiArrPos++) {
                    final Cell nextCell = this.psiArr[nextPsiPos][psiArrPos];
                    nextCell.dirPos = 0;
                    nextCell.count = 0;
                    nextCell.div = 1;
                }
                for (int psiArrPos = 0; psiArrPos < PsiArrSize; psiArrPos++) {
                    final Cell cell = this.psiArr[this.psiPos][psiArrPos];

                    if (cell.count > 0) {
                        final Cell nextNCell;
                        final Cell nextCell;

                        final int actSpeedDirPos = cell.speedDirPos;
                        final long actSpeedCnt = cell.speedCntArr[actSpeedDirPos] + cell.speedArr[actSpeedDirPos];
                        final long nextSpeedCnt;
                        if (actSpeedCnt >= MAX_SPEED_C) {
                            nextSpeedCnt = actSpeedCnt - MAX_SPEED_C;
                            final int moveDir = actSpeedDirPos == 0 ? -1 : 1;

                            // If reaching MAX_DIV: Use nextCell for the next cell in the direction of the speed.
                            nextNCell = this.psiArr[nextPsiPos][psiArrPos];
                            nextCell = this.psiArr[nextPsiPos][(psiArrPos + moveDir + PsiArrSize) % PsiArrSize];
                        } else {
                            nextSpeedCnt = actSpeedCnt;
                            final int moveDir = cell.dirPos == 0 ? -1 : 1;

                            // If reaching MAX_DIV: Use nextCell for the next cell to stay in position.
                            nextCell = this.psiArr[nextPsiPos][psiArrPos];
                            nextNCell = this.psiArr[nextPsiPos][(psiArrPos + moveDir + PsiArrSize) % PsiArrSize];
                        }
                        final int nextDirPos = (cell.dirPos + 1) % DIR_SIZE;
                        final int nextSpeedDirPos = (actSpeedDirPos + 1) % DIR_SIZE;

                        if (cell.div <= MAX_DIV) {
                            if (Objects.nonNull(nextNCell)) {
                                calcNextCell(nextCell, nextDirPos, cell.speedCntArr, cell.speedArr, cell.count, cell.div + 1, nextSpeedDirPos, actSpeedDirPos, nextSpeedCnt);
                                calcNextCell(nextNCell, nextDirPos, cell.speedCntArr, cell.speedArr, cell.count, cell.div + 1, nextSpeedDirPos, actSpeedDirPos, nextSpeedCnt);
                            } else {
                                calcNextCell(nextCell, nextDirPos, cell.speedCntArr, cell.speedArr, cell.count * 2, cell.div + 1, nextSpeedDirPos, actSpeedDirPos, nextSpeedCnt);
                            }
                        } else {
                            calcNextCell(nextCell, nextDirPos, cell.speedCntArr, cell.speedArr, cell.count, cell.div, nextSpeedDirPos, actSpeedDirPos, nextSpeedCnt);
                        }
                    }
                }
                this.psiPos = nextPsiPos;

                if (t % 1 == 0) {
                    this.repaint();
                    try {
                        Thread.sleep(25*9);
                    } catch (final InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("END: Simulating.");
        }

    private static void calcNextCell(Cell nextCell, int nextDirPos,
                                     long[] speedCntArr, long[] speedArr, long count, long div,
                                     int nextSpeedDirPos, int actSpeedDirPos, long nextSpeedCnt) {
        nextCell.dirPos = nextDirPos;
        //nextCell.count += count;
        if (Long.MAX_VALUE - count >= nextCell.count) {
            nextCell.count += count;
        } else {
            // Das Hinzufügen von count zu nextCell.count würde den Wertebereich überschreiten
            throw new RuntimeException("Das Ergebnis von nextCell.count %d + count %d würde den Wertebereich überschreiten".formatted(nextCell.count, count));
        }
        nextCell.div = div;
        nextCell.speedDirPos = nextSpeedDirPos;
        for (int speedDirPos = 0; speedDirPos < DIR_SIZE; speedDirPos++) {
            if (speedDirPos == actSpeedDirPos) {
                nextCell.speedCntArr[speedDirPos] = nextSpeedCnt;
            } else {
                nextCell.speedCntArr[speedDirPos] = speedCntArr[speedDirPos];
            }
            nextCell.speedArr[speedDirPos] = speedArr[speedDirPos];
        }
    }

    public static final Color IMG_COLOR = new Color(255, 0, 0, 125);
        public static final Color REAL_COLOR = new Color(0, 0, 255, 125);

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int nextPsiPos = (this.psiPos + 1) % 2;

            int midY = VIEW_HEIGHT / 2;
            for (int psiArrPos = 0; psiArrPos < PsiArrSize; psiArrPos++) {
                int xp1 = (psiArrPos + PsiArrSize) % PsiArrSize;
                int xp2 = (psiArrPos + 1 + PsiArrSize) % PsiArrSize;
                int x1 = (int) (xp1 * (VIEW_WIDTH / (double) PsiArrSize));
                int x2 = (int) ((xp2) * (VIEW_WIDTH / (double) PsiArrSize));

//            int y1 = (int) (midY - psiArr[psiArrPos].abs() * VIEW_HEIGHT);
//            int y2 = (int) (midY - psiArr[psiArrPos + 1].abs() * VIEW_HEIGHT);
//            g.setColor(Color.BLACK);
//            g.drawLine(x1, y1, x2, y2);
//
//            int y1Real = (int) (midY - psiArr[psiArrPos].getReal() * VIEW_HEIGHT);
//            int y2Real = (int) (midY - psiArr[psiArrPos + 1].getReal() * VIEW_HEIGHT);
//            g.setColor(REAL_COLOR);
//            g.drawLine(x1, y1Real, x2, y2Real);
//
//            int y1Imag = (int) (midY - psiArr[psiArrPos].getImaginary() * VIEW_HEIGHT);
//            int y2Imag = (int) (midY - psiArr[psiArrPos + 1].getImaginary() * VIEW_HEIGHT);
//            g.setColor(IMG_COLOR);
//            g.drawLine(x1, y1Imag, x2, y2Imag);

                Cell cell1 = psiArr[nextPsiPos][xp1];
                Cell cell2 = psiArr[nextPsiPos][xp2];
                //int y1 = (int) (midY - VIEW_HEIGHT * (cell1.count  / (normFactor * Math.pow(2, cell1.div))));
                //int y2 = (int) (midY - VIEW_HEIGHT * (cell2.count / (normFactor * Math.pow(2, cell2.div))));
                int y1 = (int) (midY - VIEW_HEIGHT * calcCellPobability(cell1));
                int y2 = (int) (midY - VIEW_HEIGHT * calcCellPobability(cell2));
                g.setColor(Color.BLACK);
                g.drawLine(x1, y1, x2, y2);
            }
        }

        private static double calcCellPobability(final Cell cell) {
            final double retPobability;
            if (cell.count == 0) {
                retPobability = 0.0D;
            } else {
                //retPobability = cell.count * (1.0D / (Math.pow(2.0D, cell.div)));
                retPobability = cell.count * (1.0D / (mathPowChecked(2.0D, cell.div)));
            }
            return retPobability;
        }

        private static double mathPowChecked(double base, double exponent) {
            final double ret;
            if (exponent > Math.log(Double.MAX_VALUE) / Math.log(base)) {
                // Das Ergebnis von Math.pow(base, exponent) würde den Wertebereich überschreiten
                throw new RuntimeException("Das Ergebnis würde den Wertebereich überschreiten");
            } else {
                ret = Math.pow(base, exponent);
            }
            return ret;
        }
    }
