package de.schmiereck.smkEasyNN.wave;

import javax.swing.*;
import java.awt.*;

public class LayerImpulseElectronSimulation extends JPanel {
    private static final int VIEW_WIDTH = 1400;
    private static final int VIEW_HEIGHT = 600;

    private static final double DT = 0.00000001;
    private static final int TIMESTEPS = (int) (1.0D / DT);

    private static final int PsiArrSize = 700/2;

    private final static int DIR_SIZE = 2;
    private final static int MAX_DIV = 64;
    private final static long MAX_SPEED_C = 16;
    private final static int PSI_LAYER_SIZE = 2;

    private class Cell {
        long count = 0;

        //long[] speedArr = new long[DIR_SIZE];
        //long speed = 0;
        long[] speedCntArr = new long[DIR_SIZE];
    }

    private class SpeedNode {
        /**
         */
        final Cell[][] speedCellArr = new Cell[DIR_SIZE][(int)MAX_SPEED_C + 1];

        public SpeedNode() {
            for (int dirPos = 0; dirPos < DIR_SIZE; dirPos++) {
                for (int speedPos = 0; speedPos <= MAX_SPEED_C; speedPos++) {
                    this.speedCellArr[dirPos][speedPos] = new Cell();
                }
            }
        }
    }

    private class SpinDirNode {
        /**
         * Count of divisions.
         * div = div/2, div = div/2, div = div/2, ...
         * Position 0 is div = 1.
         */
        final SpeedNode[] divCellArr = new SpeedNode[MAX_DIV];

        public SpinDirNode() {
            for (int divPos = 0; divPos < MAX_DIV; divPos++) {
                this.divCellArr[divPos] = new SpeedNode();
            }
        }
    }

    private class DivNode {
        /**
         * Pos of actual calculated direction.
         */
        final SpinDirNode[] spinDirCellArr = new SpinDirNode[DIR_SIZE];

        public DivNode() {
            for (int dirPos = 0; dirPos < DIR_SIZE; dirPos++) {
                this.spinDirCellArr[dirPos] = new SpinDirNode();
            }
        }
    }

    private class Layer {
        final DivNode[] dirNodeArr = new DivNode[PsiArrSize];

        public Layer() {
            for (int psiArrPos = 0; psiArrPos < PsiArrSize; psiArrPos++) {
                this.dirNodeArr[psiArrPos] = new DivNode();
            }
        }
    }

    private final Layer[] psiLayerArr;
    private int psiPos = 0;

    public static void main(String[] args) {
        final JFrame frame = new JFrame("Cell Free Electron Simulation");

        final LayerImpulseElectronSimulation simulation = new LayerImpulseElectronSimulation();

        frame.add(simulation);
        frame.setSize(VIEW_WIDTH, VIEW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        new Thread(simulation::simulate).start();
    }

    public LayerImpulseElectronSimulation() {
        this.psiLayerArr = new Layer[PSI_LAYER_SIZE];

        for (int layerPos = 0; layerPos < PSI_LAYER_SIZE; layerPos++) {
            this.psiLayerArr[layerPos] = new Layer();
        }
        {
            final int nextArrPos = ((PsiArrSize / 4) * 1);
            final Cell cell = retrieveCell(this.psiLayerArr, this.psiPos, nextArrPos, 0, 0, 1, 12);
            cell.count = 1;
            //cell.div = 1;
            //cell.speedArr[0] = 0;
            //cell.speedArr[1] = 16;
            //cell.speed = 16;
            cell.speedCntArr[0] = 0;
            cell.speedCntArr[1] = 0;
        }
        {
            final int nextArrPos = ((PsiArrSize / 4) * 3);
            final Cell cell = retrieveCell(this.psiLayerArr, this.psiPos, nextArrPos,  0, 1, 0, 6);
            cell.count = 1;
            //cell.div = 1;
            //cell.speedArr[0] = 6;
            //cell.speedArr[1] = 0;
            //cell.speed = 6;
            cell.speedCntArr[0] = 0;
            cell.speedCntArr[1] = 0;
        }
    }

    public void simulate() {
        System.out.println("START: Simulating...");

        for (int t = 0; t < TIMESTEPS * 1000; t++) {
            final int actPsiPos = this.psiPos;
            final int nextPsiPos = (actPsiPos + 1) % 2;

            for (int psiArrPos = 0; psiArrPos < PsiArrSize; psiArrPos++) {
                final DivNode divNode = this.psiLayerArr[actPsiPos].dirNodeArr[psiArrPos];
                for (int divPos = MAX_DIV - 1; divPos > 0; divPos--) {
                    for (int spinDirPos = 0; spinDirPos < DIR_SIZE; spinDirPos++) {
                        for (int speedDirPos = 0; speedDirPos < DIR_SIZE; speedDirPos++) {
                            for (int speedPos = 0; speedPos <= MAX_SPEED_C; speedPos++) {
                                final Cell cell = divNode.spinDirCellArr[spinDirPos].divCellArr[divPos].speedCellArr[speedDirPos][speedPos];

                                // Renorm div counts to lower divisions.
                                if (cell.count > 1) {
                                    final long upperCellCount = cell.count / 2;
                                    final long letCellCount = cell.count % 2;

                                    calcNextCell2(this.psiLayerArr, psiArrPos, cell, actPsiPos, divPos - 1, spinDirPos, speedDirPos, speedPos, upperCellCount);
                                    cell.count = letCellCount;
                                }
                            }
                        }
                    }
                }
            }
            for (int psiArrPos = 0; psiArrPos < PsiArrSize; psiArrPos++) {
                final DivNode divNode = this.psiLayerArr[nextPsiPos].dirNodeArr[psiArrPos];
                for (int divPos = 0; divPos < MAX_DIV; divPos++) {
                    for (int spinDirPos = 0; spinDirPos < DIR_SIZE; spinDirPos++) {
                        for (int speedDirPos = 0; speedDirPos < DIR_SIZE; speedDirPos++) {
                            for (int speedPos = 0; speedPos <= MAX_SPEED_C; speedPos++) {
                                final Cell cell = divNode.spinDirCellArr[spinDirPos].divCellArr[divPos].speedCellArr[speedDirPos][speedPos];
                                cell.count = 0;
                            }
                        }
                    }
                }
            }
            for (int psiArrPos = 0; psiArrPos < PsiArrSize; psiArrPos++) {
                final DivNode divNode = this.psiLayerArr[this.psiPos].dirNodeArr[psiArrPos];
                for (int divPos = 0; divPos < MAX_DIV; divPos++) {
                    for (int spinDirPos = 0; spinDirPos < DIR_SIZE; spinDirPos++) {
                        for (int speedDirPos = 0; speedDirPos < DIR_SIZE; speedDirPos++) {
                            for (int speedPos = 0; speedPos <= MAX_SPEED_C; speedPos++) {
                                final Cell cell = divNode.spinDirCellArr[spinDirPos].divCellArr[divPos].speedCellArr[speedDirPos][speedPos];

                                if (cell.count > 0) {
                                    final int actCalcDirPos = speedDirPos;
                                    final int nextSpinDirPos = (spinDirPos + 1) % DIR_SIZE;

                                    //final long actSpeedCnt = cell.speedCntArr[speedDirPos] + speedPos;
                                    final long actSpeedCnt = cell.speedCntArr[spinDirPos] + speedPos;
                                    final long nextSpeedCnt;
                                    if (actSpeedCnt >= MAX_SPEED_C) {
                                        nextSpeedCnt = actSpeedCnt - MAX_SPEED_C;
                                        final int moveDir = speedDirPos == 0 ? -1 : 1;

                                        // Use nextCell for the next cell in the direction of the speed.
                                        final int nextPsiArrPos = (psiArrPos + moveDir + PsiArrSize) % PsiArrSize;
                                        calcNextCell(this.psiLayerArr, nextPsiArrPos, cell, nextPsiPos, divPos, nextSpinDirPos, speedDirPos, speedPos, speedDirPos, nextSpeedCnt);
                                    } else {
                                        nextSpeedCnt = actSpeedCnt;
                                        final int moveDir = speedDirPos == 0 ? -1 : 1;

                                        // If reaching MAX_DIV: Use nextCell for the next cell to stay in position.
                                        final int nextDivPos = divPos + 1;
                                        if (nextDivPos < MAX_DIV) {
                                            calcNextCell(this.psiLayerArr, psiArrPos, cell, nextPsiPos, nextDivPos, nextSpinDirPos, speedDirPos, speedPos, speedDirPos, nextSpeedCnt);

                                            final int nextPsiArrPos = (psiArrPos + moveDir + PsiArrSize) % PsiArrSize;
                                            calcNextCell(this.psiLayerArr, nextPsiArrPos, cell, nextPsiPos, nextDivPos, nextSpinDirPos, speedDirPos, speedPos, speedDirPos, nextSpeedCnt);
                                        } else {
                                            calcNextCell(this.psiLayerArr, psiArrPos, cell, nextPsiPos, divPos, nextSpinDirPos, speedDirPos, speedPos, speedDirPos, nextSpeedCnt);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            this.psiPos = nextPsiPos;

            if (t % 1 == 0) {
                this.repaint();
                try {
                    Thread.sleep(25*4);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("END: Simulating.");
    }

    private static void calcNextCell(Layer[] psiLayerArr, int nextPsiArrPos, Cell cell, int nextPsiPos, int divPos, int spinDirPos, int speedDirPos, int speedPos, int actDirPos, long nextSpeedCnt) {
        final Cell nextCell = retrieveCell(psiLayerArr, nextPsiPos, nextPsiArrPos, divPos, spinDirPos, speedDirPos, speedPos);

        calcNextCell(nextCell, cell.speedCntArr,
                //cell.speed,
                cell.count, actDirPos, nextSpeedCnt);
    }

    private static void calcNextCell2(Layer[] psiLayerArr, int nextPsiArrPos, Cell cell, int nextPsiPos, int divPos, int spinDirPos, int speedDirPos, int speedPos, long nextCellCount) {
        final Cell nextCell = retrieveCell(psiLayerArr, nextPsiPos, nextPsiArrPos, divPos, spinDirPos, speedDirPos, speedPos);

        calcNextCell2(nextCell, cell.speedCntArr,
                //cell.speed,
                nextCellCount);
    }

    private static Cell retrieveCell(Layer[] psiLayerArr, int nextPsiPos, int nextPsiArrPos, int divPos, int spinDirPos, int speedDirPos, int speedPos) {
        return psiLayerArr[nextPsiPos].dirNodeArr[nextPsiArrPos].spinDirCellArr[spinDirPos].divCellArr[divPos].speedCellArr[speedDirPos][speedPos];
    }

    private static void calcNextCell(Cell nextCell,
                                     long[] speedCntArr,
                                     //long speed,
                                     long count,
                                     int actSpeedDirPos, long nextSpeedCnt) {
        //nextCell.count += count;
        if (Long.MAX_VALUE - count >= nextCell.count) {
            nextCell.count += count;
        } else {
            // Das Hinzufügen von count zu nextCell.count würde den Wertebereich überschreiten
            throw new RuntimeException("Das Ergebnis von nextCell.count %d + count %d würde den Wertebereich überschreiten".formatted(nextCell.count, count));
        }
        //nextCell.div = div;
        for (int speedDirPos = 0; speedDirPos < DIR_SIZE; speedDirPos++) {
            if (speedDirPos == actSpeedDirPos) {
                nextCell.speedCntArr[speedDirPos] = nextSpeedCnt;
            } else {
                nextCell.speedCntArr[speedDirPos] = speedCntArr[speedDirPos];
            }
            //nextCell.speedArr[speedDirPos] = speedArr[speedDirPos];
        }
        //nextCell.speed = speed;
    }

    private static void calcNextCell2(Cell nextCell,
                                     long[] speedCntArr,
                                      //long speed,
                                      long count) {
        //nextCell.count += count;
        if (Long.MAX_VALUE - count >= nextCell.count) {
            nextCell.count += count;
        } else {
            // Das Hinzufügen von count zu nextCell.count würde den Wertebereich überschreiten
            throw new RuntimeException("Das Ergebnis von nextCell.count %d + count %d würde den Wertebereich überschreiten".formatted(nextCell.count, count));
        }
        //nextCell.div = div;
        for (int speedDirPos = 0; speedDirPos < DIR_SIZE; speedDirPos++) {
            nextCell.speedCntArr[speedDirPos] = speedCntArr[speedDirPos];
            //nextCell.speedArr[speedDirPos] = speedArr[speedDirPos];
        }
        //nextCell.speed = speed;
    }

    public static final Color IMG_COLOR = new Color(255, 0, 0, 125);
    public static final Color REAL_COLOR = new Color(0, 0, 255, 125);

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int nextPsiPos = (this.psiPos + 1) % 2;

        int midY = VIEW_HEIGHT / 2;

        g.setColor(Color.GRAY);
        g.drawLine(0, midY, VIEW_WIDTH, midY);

        for (int psiArrPos = 0; psiArrPos < PsiArrSize - 1; psiArrPos++) {
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

            long yc1 = 0;
            long yc2 = 0;
            double yp1 = 0.0D;
            double yp2 = 0.0D;
            for (int divPos = 0; divPos < MAX_DIV; divPos++) {
                for (int speedDirPos = 0; speedDirPos < DIR_SIZE; speedDirPos++) {
                    for (int spinDirPos = 0; spinDirPos < DIR_SIZE; spinDirPos++) {
                        for (int speedPos = 0; speedPos <= MAX_SPEED_C; speedPos++) {
                            Cell cell1 = psiLayerArr[nextPsiPos].dirNodeArr[xp1].spinDirCellArr[spinDirPos].divCellArr[divPos].speedCellArr[speedDirPos][speedPos];
                            Cell cell2 = psiLayerArr[nextPsiPos].dirNodeArr[xp2].spinDirCellArr[spinDirPos].divCellArr[divPos].speedCellArr[speedDirPos][speedPos];
                            //int y1 = (int) (midY - VIEW_HEIGHT * (cell1.count  / (normFactor * Math.pow(2, cell1.div))));
                            //int y2 = (int) (midY - VIEW_HEIGHT * (cell2.count / (normFactor * Math.pow(2, cell2.div))));
                            yp1 += calcCellPobability(cell1, divPos + 1);
                            yp2 += calcCellPobability(cell2, divPos + 1);
                            if (cell1.count > 0) {
                                yc1 += cell1.count;

                                double yd1 = 0.0D;
                                double yd2 = (divPos * cell1.count) / Math.pow(2.0D, MAX_DIV / 8.0D);
                                int y1 = (int) (midY + VIEW_HEIGHT * yd1);
                                int y2 = (int) (midY + VIEW_HEIGHT * yd2);
                                g.setColor(Color.RED);
                                g.drawLine(x1, y2, x2, y2);
                            }
                            if (cell2.count > 0) {
                                yc2 += cell2.count;
                            }
                        }
                    }
                }
            }
            {
                int y1 = (int) (midY - VIEW_HEIGHT * yp1);
                int y2 = (int) (midY - VIEW_HEIGHT * yp2);
                g.setColor(Color.BLACK);
                g.drawLine(x1, y1, x2, y2);
            }
            {
                int y1 = (int) (midY - VIEW_HEIGHT * (yc1 / 300.0D));
                int y2 = (int) (midY - VIEW_HEIGHT * (yc2 / 300.0D));
                g.setColor(Color.BLUE);
                g.drawLine(x1, y1 - 125, x2, y2 - 125);
            }
        }
    }

    private static double calcCellPobability(final Cell cell, final int div) {
        final double retPobability;
        if (cell.count == 0) {
            retPobability = 0.0D;
        } else {
            //retPobability = cell.count * (1.0D / (Math.pow(2.0D, cell.div)));
            //retPobability = cell.count * (1.0D / (mathPowChecked(2.0D, cell.div)));
            retPobability = cell.count * (1.0D / (mathPowChecked(2.0D, div)));
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
