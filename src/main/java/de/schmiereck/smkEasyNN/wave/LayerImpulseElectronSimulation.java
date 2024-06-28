package de.schmiereck.smkEasyNN.wave;

import javax.swing.*;
import java.awt.*;

public class LayerImpulseElectronSimulation extends JPanel {
    private static final int VIEW_WIDTH = 1400;
    private static final int VIEW_HEIGHT = 600;

    private static final double DT = 0.00000001;
    private static final int TIMESTEPS = (int) (1.0D / DT);

    private static final int PsiArrSize = 200;

    private final static int DIR_SIZE = 2;
    private final static int MAX_DIV = 64/4;
    private final static int MAX_SPEED_C = 12;
    private final static int PSI_LAYER_SIZE = 2;
    private final static int MAX_SPIN = 6;

    private static class Cell {
        long count = 0;

        //long[] speedArr = new long[DIR_SIZE];
        //long speed = 0;
        long[] speedCntArr = new long[DIR_SIZE];

        //int spin;
        //int spinCnt = 0;
    }

    private static class SpeedNode {
        final Cell[][] speedCellArr = new Cell[DIR_SIZE][(int)MAX_SPEED_C + 1];

        public SpeedNode() {
            for (int dirPos = 0; dirPos < DIR_SIZE; dirPos++) {
                for (int speedPos = 0; speedPos <= MAX_SPEED_C; speedPos++) {
                    this.speedCellArr[dirPos][speedPos] = new Cell();
                }
            }
        }
    }

    private static class SpinDirNode {
        final SpeedNode[] divCellArr = new SpeedNode[MAX_DIV];
        //int spin;
        //int spinCnt = 0;

        public SpinDirNode() {
            for (int divPos = 0; divPos < MAX_DIV; divPos++) {
                this.divCellArr[divPos] = new SpeedNode();
            }
        }
    }

    private static class SpinCntNode {
        /**
         * Pos of actual calculated direction.
         */
        final SpinDirNode[] spinDirNodeArr = new SpinDirNode[DIR_SIZE];

        public SpinCntNode() {
            for (int dirPos = 0; dirPos < DIR_SIZE; dirPos++) {
                this.spinDirNodeArr[dirPos] = new SpinDirNode();
            }
        }

        public static SpinCntNode[] createSpinCntNodeArr() {
            final SpinCntNode[] spinCntNodeArr = new SpinCntNode[MAX_SPIN];

            for (int spinCntPos = 0; spinCntPos < MAX_SPIN; spinCntPos++) {
                spinCntNodeArr[spinCntPos] = new SpinCntNode();
            }
            return spinCntNodeArr;
        }
    }

    private static class SpinNode {
        final SpinCntNode[] spinCntNodeArr = SpinCntNode.createSpinCntNodeArr();

        public static SpinNode[] createSpinNodeArr() {
            final SpinNode[] spinNodeArr = new SpinNode[MAX_SPIN];

            for (int spinNodePos = 0; spinNodePos < MAX_SPIN; spinNodePos++) {
                spinNodeArr[spinNodePos] = new SpinNode();
            }
            return spinNodeArr;
        }
    }

    private static class DivNode {
        final SpinNode[] spinNodeArr = SpinNode.createSpinNodeArr();

        /**
         * Count of divisions.
         * div = div/2, div = div/2, div = div/2, ...
         * Position 0 is div = 1.
         */
        public static DivNode[] createDivNodeArr() {
            final DivNode[] dirNodeArr = new DivNode[PsiArrSize];

            for (int psiArrPos = 0; psiArrPos < PsiArrSize; psiArrPos++) {
                dirNodeArr[psiArrPos] = new DivNode();
            }
            return dirNodeArr;
        }
    }

    private class Layer {
        final DivNode[] dirNodeArr = DivNode.createDivNodeArr();
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
            // to right, verry fast:
            final int nextArrPos = ((PsiArrSize / 4) * 1);
            final Cell cell = retrieveCell(this.psiLayerArr, this.psiPos, nextArrPos, 3, 0, 0, 0, 1, MAX_SPEED_C - (MAX_SPEED_C / 4));
            cell.count = 0;//1;
            //cell.div = 1;
            //cell.speedArr[0] = 0;
            //cell.speedArr[1] = 16;
            //cell.speed = 16;
            cell.speedCntArr[0] = 0;
            cell.speedCntArr[1] = 0;
            //cell.spin = 3;
        }
        {
            // stay in middle:
            final int nextArrPos = ((PsiArrSize / 4) * 2);
            final Cell cell = retrieveCell(this.psiLayerArr, this.psiPos, nextArrPos, 1, 0, 0, 0, 1, 0);
            cell.count = 1;
            cell.speedCntArr[0] = 0;
            cell.speedCntArr[1] = 0;
        }
        {
            // to left, slowly:
            final int nextArrPos = ((PsiArrSize / 4) * 3);
            final Cell cell = retrieveCell(this.psiLayerArr, this.psiPos, nextArrPos,  5, 0, 1, 0, 0, MAX_SPEED_C - (MAX_SPEED_C / 2));
            cell.count = 0;//1;
            //cell.div = 1;
            //cell.speedArr[0] = 6;
            //cell.speedArr[1] = 0;
            //cell.speed = 6;
            cell.speedCntArr[0] = 0;
            cell.speedCntArr[1] = 0;
            //cell.spin = 5;
        }
    }

    public void simulate() {
        System.out.println("START: Simulating...");

        for (int t = 0; t < TIMESTEPS * 1000; t++) {
            final int actPsiPos = this.psiPos;
            final int nextPsiPos = (actPsiPos + 1) % 2;

            for (int psiArrPos = 0; psiArrPos < PsiArrSize; psiArrPos++) {
                final DivNode divNode = this.psiLayerArr[actPsiPos].dirNodeArr[psiArrPos];
                for (int spinPos = 0; spinPos < MAX_SPIN; spinPos++) {
                    for (int spinCntPos = 0; spinCntPos < MAX_SPIN; spinCntPos++) {
                        for (int divPos = MAX_DIV - 1; divPos > 0; divPos--) {
                            for (int spinDirPos = 0; spinDirPos < DIR_SIZE; spinDirPos++) {
                                for (int speedDirPos = 0; speedDirPos < DIR_SIZE; speedDirPos++) {
                                    for (int speedPos = 0; speedPos <= MAX_SPEED_C; speedPos++) {
                                        final Cell cell = divNode.spinNodeArr[spinPos].spinCntNodeArr[spinCntPos].spinDirNodeArr[spinDirPos].divCellArr[divPos].speedCellArr[speedDirPos][speedPos];

                                        // Renorm div counts to lower divisions.
                                        if (cell.count > 1) {
                                            final long upperCellCount = cell.count / 2;
                                            final long letCellCount = cell.count % 2;

                                            calcNextCell2(this.psiLayerArr, psiArrPos, cell,
                                                    actPsiPos, divPos - 1,
                                                    spinPos, spinCntPos, spinDirPos,
                                                    speedDirPos, speedPos, upperCellCount);
                                            cell.count = letCellCount;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            for (int psiArrPos = 0; psiArrPos < PsiArrSize; psiArrPos++) {
                final DivNode divNode = this.psiLayerArr[nextPsiPos].dirNodeArr[psiArrPos];
                for (int spinPos = 0; spinPos < MAX_SPIN; spinPos++) {
                    for (int spinCntPos = 0; spinCntPos < MAX_SPIN; spinCntPos++) {
                        for (int divPos = 0; divPos < MAX_DIV; divPos++) {
                            for (int spinDirPos = 0; spinDirPos < DIR_SIZE; spinDirPos++) {
                                for (int speedDirPos = 0; speedDirPos < DIR_SIZE; speedDirPos++) {
                                    for (int speedPos = 0; speedPos <= MAX_SPEED_C; speedPos++) {
                                        final Cell cell = divNode.spinNodeArr[spinPos].spinCntNodeArr[spinCntPos].spinDirNodeArr[spinDirPos].divCellArr[divPos].speedCellArr[speedDirPos][speedPos];
                                        cell.count = 0;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            for (int psiArrPos = 0; psiArrPos < PsiArrSize; psiArrPos++) {
                final DivNode divNode = this.psiLayerArr[this.psiPos].dirNodeArr[psiArrPos];
                for (int spinPos = 0; spinPos < MAX_SPIN; spinPos++) {
                    for (int spinCntPos = 0; spinCntPos < MAX_SPIN; spinCntPos++) {
                        for (int divPos = 0; divPos < MAX_DIV; divPos++) {
                            for (int spinDirPos = 0; spinDirPos < DIR_SIZE; spinDirPos++) {
                                for (int speedDirPos = 0; speedDirPos < DIR_SIZE; speedDirPos++) {
                                    for (int speedPos = 0; speedPos <= MAX_SPEED_C; speedPos++) {
                                        final Cell cell = divNode.spinNodeArr[spinPos].spinCntNodeArr[spinCntPos].spinDirNodeArr[spinDirPos].divCellArr[divPos].speedCellArr[speedDirPos][speedPos];

                                        if (cell.count > 0) {
                                            final int actCalcDirPos = speedDirPos;
                                            final int nextSpinDirPos = (spinDirPos + 1) % DIR_SIZE;
                                            final int actSpinCntPos = spinCntPos;//cell.spinCnt;
                                            final int nextSpinCntPos = (actSpinCntPos + 1) % spinPos;//cell.spin;

                                            final int actSpeedDirPos = speedDirPos;
                                            final int nextSpeedDirPos = (speedDirPos + 1) % DIR_SIZE;
                                            //final long actSpeedCnt = cell.speedCntArr[speedDirPos] + speedPos;
                                            final long actSpeedCnt = cell.speedCntArr[spinDirPos] + speedPos;
                                            final long nextSpeedCnt;
                                            if (actSpeedCnt >= MAX_SPEED_C) {
                                                nextSpeedCnt = actSpeedCnt - MAX_SPEED_C;
                                                final int moveDir = speedDirPos == 0 ? -1 : 1;

                                                // Use nextCell for the next cell in the direction of the speed.
                                                final int nextPsiArrPos = (psiArrPos + moveDir + PsiArrSize) % PsiArrSize;
                                                calcNextCell(this.psiLayerArr, nextPsiArrPos, cell,
                                                        nextPsiPos, divPos,
                                                        spinPos, nextSpinCntPos, nextSpinDirPos,
                                                        speedDirPos, speedPos,
                                                        speedDirPos, nextSpeedCnt);//, nextSpinCnt);
                                            } else {
                                                nextSpeedCnt = actSpeedCnt;
                                                if (nextSpinCntPos == 0) {
                                                    final int moveDir = speedDirPos == 0 ? -1 : 1;

                                                    // If reaching MAX_DIV: Use nextCell for the next cell to stay in position.
                                                    final int nextDivPos = divPos + 1;
                                                    if (nextDivPos < MAX_DIV) {
                                                        calcNextCell(this.psiLayerArr, psiArrPos, cell,
                                                                nextPsiPos, nextDivPos,
                                                                //spinPos, actSpinCntPos,//nextSpinCntPos,
                                                                spinPos, nextSpinCntPos, nextSpinDirPos,
                                                                speedDirPos, speedPos,
                                                                speedDirPos, nextSpeedCnt);//, nextSpinCnt);
                                                                //nextSpeedCnt, actSpinCnt);

                                                        final int nextPsiArrPos = (psiArrPos + moveDir + PsiArrSize) % PsiArrSize;
                                                        calcNextCell(this.psiLayerArr, nextPsiArrPos, cell,
                                                                nextPsiPos, nextDivPos,
                                                                spinPos, nextSpinCntPos, nextSpinDirPos,
                                                                speedDirPos, speedPos,
                                                                speedDirPos, nextSpeedCnt);//, nextSpinCnt);
                                                    } else {
                                                        calcNextCell(this.psiLayerArr, psiArrPos, cell,
                                                                nextPsiPos, divPos,
                                                                spinPos, nextSpinCntPos, nextSpinDirPos,
                                                                speedDirPos, speedPos,
                                                                speedDirPos, nextSpeedCnt);//, nextSpinCnt);
                                                    }
                                                } else {
                                                    calcNextCell(this.psiLayerArr, psiArrPos, cell,
                                                            nextPsiPos, divPos,
                                                            spinPos, nextSpinCntPos, nextSpinDirPos,
                                                            speedDirPos, speedPos,
                                                            speedDirPos, nextSpeedCnt);//, nextSpinCnt);
                                                }
                                            }
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
                    Thread.sleep(25*5);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("END: Simulating.");
    }

    private static void calcNextCell(Layer[] psiLayerArr, int nextPsiArrPos, Cell sourceCell,
                                     int nextPsiPos, int nextDivPos,
                                     int nextSpinPos, int nextSpinCntPos, int nextSpinDirPos,
                                     int nextSpeedDirPos, int nextSpeedPos,
                                     int actSpeedDirPos, long nextSpeedCnt) { //}, int nextSpinCnt) {
        final Cell nextCell = retrieveCell(psiLayerArr, nextPsiPos, nextPsiArrPos,
                                           nextSpinPos, nextSpinCntPos, nextSpinDirPos,
                                           nextDivPos, nextSpeedDirPos, nextSpeedPos);

        calcNextCellState(nextCell, sourceCell.speedCntArr,
                sourceCell.count, actSpeedDirPos, nextSpeedCnt);//, sourceCell.spin, nextSpinCnt);
    }

    private static void calcNextCell2(Layer[] psiLayerArr, int nextPsiArrPos, Cell sourceCell,
                                      int nextPsiPos, int nextDivPos,
                                      int nextSpinPos, int nextSpinCntPos, int nextSpinDirPos,
                                      int nextSpeedDirPos, int nextSpeedPos,
                                      long nextCellCount) {
        final Cell nextCell = retrieveCell(psiLayerArr, nextPsiPos, nextPsiArrPos,
                                           nextSpinPos, nextSpinCntPos, nextSpinDirPos,
                                           nextDivPos, nextSpeedDirPos, nextSpeedPos);

        calcNextCellState2(nextCell, sourceCell.speedCntArr,
                nextCellCount);//, sourceCell.spin, sourceCell.spinCnt);
    }

    private static Cell retrieveCell(Layer[] psiLayerArr, int psiPos, int psiArrPos,
                                     int spinPos, int spinCntPos, int spinDirPos,
                                     int divPos, int speedDirPos, int speedPos) {
        return psiLayerArr[psiPos].dirNodeArr[psiArrPos].spinNodeArr[spinPos].spinCntNodeArr[spinCntPos].spinDirNodeArr[spinDirPos].divCellArr[divPos].speedCellArr[speedDirPos][speedPos];
    }

    private static void calcNextCellState(final Cell nextCell,
                                          final long[] sourceSpeedCntArr,
                                          final long count,
                                          final int actSpeedDirPos, final long nextSpeedCnt
                                          //final int nextSpin, int nextSpinCnt
                                            ) {
        //nextCell.count += count;
        if ((Long.MAX_VALUE - count) >= nextCell.count) {
            nextCell.count += count;
        } else {
            throw new RuntimeException("Das Ergebnis von nextCell.count %d + count %d würde den Wertebereich überschreiten".formatted(nextCell.count, count));
        }
        for (int speedDirPos = 0; speedDirPos < DIR_SIZE; speedDirPos++) {
            if (speedDirPos == actSpeedDirPos) {
                nextCell.speedCntArr[speedDirPos] = nextSpeedCnt;
            } else {
                nextCell.speedCntArr[speedDirPos] = sourceSpeedCntArr[speedDirPos];
            }
        }
        //nextCell.spin = nextSpin;
        //nextCell.spinCnt = nextSpinCnt;
    }

    private static void calcNextCellState2(Cell nextCell,
                                           long[] speedCntArr,
                                           long count
                                           //final int nextSpin, int nextSpinCnt
                                            ) {
        //nextCell.count += count;
        if (Long.MAX_VALUE - count >= nextCell.count) {
            nextCell.count += count;
        } else {
            throw new RuntimeException("Das Ergebnis von nextCell.count %d + count %d würde den Wertebereich überschreiten".formatted(nextCell.count, count));
        }
        for (int speedDirPos = 0; speedDirPos < DIR_SIZE; speedDirPos++) {
            nextCell.speedCntArr[speedDirPos] = speedCntArr[speedDirPos];
        }
        //nextCell.spin = nextSpin;
        //nextCell.spinCnt = nextSpinCnt;
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

            long ys1 = 0;
            long ys2 = 0;
            long yc1 = 0;
            long yc2 = 0;
            double yp1 = 0.0D;
            double yp2 = 0.0D;
            for (int spinPos = 0; spinPos < MAX_SPIN; spinPos++) {
                for (int spinCntPos = 0; spinCntPos < MAX_SPIN; spinCntPos++) {
                    for (int divPos = 0; divPos < MAX_DIV; divPos++) {
                        for (int speedDirPos = 0; speedDirPos < DIR_SIZE; speedDirPos++) {
                            for (int spinDirPos = 0; spinDirPos < DIR_SIZE; spinDirPos++) {
                                for (int speedPos = 0; speedPos <= MAX_SPEED_C; speedPos++) {
                                    final Cell cell1 = psiLayerArr[nextPsiPos].dirNodeArr[xp1].spinNodeArr[spinPos].spinCntNodeArr[spinCntPos].spinDirNodeArr[spinDirPos].divCellArr[divPos].speedCellArr[speedDirPos][speedPos];
                                    final Cell cell2 = psiLayerArr[nextPsiPos].dirNodeArr[xp2].spinNodeArr[spinPos].spinCntNodeArr[spinCntPos].spinDirNodeArr[spinDirPos].divCellArr[divPos].speedCellArr[speedDirPos][speedPos];
                                    //int y1 = (int) (midY - VIEW_HEIGHT * (cell1.count  / (normFactor * Math.pow(2, cell1.div))));
                                    //int y2 = (int) (midY - VIEW_HEIGHT * (cell2.count / (normFactor * Math.pow(2, cell2.div))));
                                    yp1 += calcCellPobability(cell1, divPos + 1);
                                    yp2 += calcCellPobability(cell2, divPos + 1);
                                    if (cell1.count > 0) {
                                        yc1 += cell1.count;

                                        {
                                            double yd1 = 0.0D;
                                            double yd2 = (divPos * cell1.count) / Math.pow(2.0D, MAX_DIV / (MAX_DIV / 8.0D));
                                            int y1 = (int) (midY + VIEW_HEIGHT * yd1);
                                            int y2 = (int) (midY + VIEW_HEIGHT * yd2);
                                            g.setColor(Color.RED);
                                            g.drawLine(x1, y2, x2, y2);
                                        }
                                        ys1 += spinCntPos;//cell1.spinCnt;

                                        {
                                            double yd1 = 0.0D;
                                            double yd2 = (spinCntPos * cell1.count) / (double)MAX_SPIN;
                                            int y1 = (int) (midY - VIEW_HEIGHT * yd1 / 10.0D);
                                            int y2 = (int) (midY - VIEW_HEIGHT * yd2 / 10.0D);
                                            g.setColor(Color.ORANGE);
                                            g.drawLine(x1, y2 - 125, x2, y2 - 125);
                                        }
                                    }
                                    if (cell2.count > 0) {
                                        yc2 += cell2.count;
                                        ys2 += spinCntPos;//cell1.spinCnt;
                                    }
                                }
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
            {
                int y1 = (int) (midY - VIEW_HEIGHT * (ys1 / 600.0D));
                int y2 = (int) (midY - VIEW_HEIGHT * (ys2 / 600.0D));
                g.setColor(Color.ORANGE);
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
