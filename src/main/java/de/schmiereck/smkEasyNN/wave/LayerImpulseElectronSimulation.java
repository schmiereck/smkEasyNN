package de.schmiereck.smkEasyNN.wave;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class LayerImpulseElectronSimulation extends JPanel {
    private static final int VIEW_WIDTH = 1400;
    private static final int VIEW_HEIGHT = 600;

    private static final double DT = 0.00000001;
    private static final int TIMESTEPS = (int) (1.0D / DT);

    private static final int PsiArrSize = 700/2;

    private final static int DIR_SIZE = 2;
    private final static long MAX_DIV = 64;
    private final static long MAX_SPEED_C = 16;
    private final static int LAYER_SIZE = 2;

    private class Cell {
        long count = 0;
        /**
         * Count of divisions.
         * div = div/2, div = div/2, div = div/2, ...
         */
        long div = 1;

        long[] speedArr = new long[DIR_SIZE];
        long[] speedCntArr = new long[DIR_SIZE];
    }

    private class Node {
        final Cell[] dirCellArr = new Cell[DIR_SIZE];

        public Node() {
            for (int nextDirPos = 0; nextDirPos < DIR_SIZE; nextDirPos++) {
                this.dirCellArr[nextDirPos] = new Cell();
            }
        }
    }

    private class Layer {
        final Node[] psiArr = new Node[PsiArrSize];

        public Layer() {
            for (int nextArrPos = 0; nextArrPos < PsiArrSize; nextArrPos++) {
                this.psiArr[nextArrPos] = new Node();
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
        this.psiLayerArr = new Layer[LAYER_SIZE];

        for (int layerPos = 0; layerPos < LAYER_SIZE; layerPos++) {
            this.psiLayerArr[layerPos] = new Layer();
        }
        {
            final int nextArrPos = ((PsiArrSize / 4) * 1);
            final int dirPos = 0;
            final Cell cell = this.psiLayerArr[this.psiPos].psiArr[nextArrPos].dirCellArr[dirPos];
            cell.count = 1;
            cell.div = 1;
            cell.speedArr[0] = 0;
            cell.speedArr[1] = 16;
            cell.speedCntArr[0] = 0;
            cell.speedCntArr[1] = 0;
        }
        {
            final int nextArrPos = ((PsiArrSize / 4) * 3);
            final int dirPos = 0;
            final Cell cell = this.psiLayerArr[this.psiPos].psiArr[nextArrPos].dirCellArr[dirPos];
            //cell.count = 1;
            cell.div = 1;
            cell.speedArr[0] = 6;
            cell.speedArr[1] = 0;
            cell.speedCntArr[0] = 0;
            cell.speedCntArr[1] = 0;
        }
    }

    public void simulate() {
        System.out.println("START: Simulating...");

        for (int t = 0; t < TIMESTEPS * 1000; t++) {
            final int nextPsiPos = (this.psiPos + 1) % 2;

            for (int psiArrPos = 0; psiArrPos < PsiArrSize; psiArrPos++) {
                final Node node = this.psiLayerArr[nextPsiPos].psiArr[psiArrPos];
                for (int dirPos = 0; dirPos < DIR_SIZE; dirPos++) {
                    final Cell cell = node.dirCellArr[dirPos];
                    cell.count = 0;
                    cell.div = 1;
                }
            }
            for (int psiArrPos = 0; psiArrPos < PsiArrSize; psiArrPos++) {
                final Node node = this.psiLayerArr[this.psiPos].psiArr[psiArrPos];
                for (int dirPos = 0; dirPos < DIR_SIZE; dirPos++) {
                    final Cell cell = node.dirCellArr[dirPos];

                    if (cell.count > 0) {
                        final Cell nextNCell;
                        final Cell nextCell;

                        final int actDirPos = dirPos;
                        final int nextDirPos = (dirPos + 1) % DIR_SIZE;

                        final long actSpeedCnt = cell.speedCntArr[actDirPos] + cell.speedArr[actDirPos];
                        final long nextSpeedCnt;
                        if (actSpeedCnt >= MAX_SPEED_C) {
                            nextSpeedCnt = actSpeedCnt - MAX_SPEED_C;
                            final int moveDir = actDirPos == 0 ? -1 : 1;

                            // If reaching MAX_DIV: Use nextCell for the next cell in the direction of the speed.
                            //nextNCell = this.psiLayerArr[nextPsiPos].psiArr[psiArrPos].dirCellArr[nextDirPos];
                            nextNCell = this.psiLayerArr[nextPsiPos].psiArr[(psiArrPos + moveDir + PsiArrSize) % PsiArrSize].dirCellArr[nextDirPos];
                            nextCell = this.psiLayerArr[nextPsiPos].psiArr[(psiArrPos + moveDir + PsiArrSize) % PsiArrSize].dirCellArr[nextDirPos];
                        } else {
                            nextSpeedCnt = actSpeedCnt;
                            final int moveDir = dirPos == 0 ? -1 : 1;

                            // If reaching MAX_DIV: Use nextCell for the next cell to stay in position.
                            nextCell = this.psiLayerArr[nextPsiPos].psiArr[psiArrPos].dirCellArr[nextDirPos];
                            nextNCell = this.psiLayerArr[nextPsiPos].psiArr[(psiArrPos + moveDir + PsiArrSize) % PsiArrSize].dirCellArr[nextDirPos];
                        }

                        if (cell.div <= MAX_DIV) {
                            calcNextCell(nextCell, cell.speedCntArr, cell.speedArr, cell.count, cell.div + 1, actDirPos, nextSpeedCnt);
                            calcNextCell(nextNCell, cell.speedCntArr, cell.speedArr, cell.count, cell.div + 1, actDirPos, nextSpeedCnt);
                        } else {
                            calcNextCell(nextCell, cell.speedCntArr, cell.speedArr, cell.count, cell.div, actDirPos, nextSpeedCnt);
                        }
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

    private static void calcNextCell(Cell nextCell,
                                     long[] speedCntArr, long[] speedArr, long count, long div,
                                     int actSpeedDirPos, long nextSpeedCnt) {
        //nextCell.count += count;
        if (Long.MAX_VALUE - count >= nextCell.count) {
            nextCell.count += count;
        } else {
            // Das Hinzufügen von count zu nextCell.count würde den Wertebereich überschreiten
            throw new RuntimeException("Das Ergebnis von nextCell.count %d + count %d würde den Wertebereich überschreiten".formatted(nextCell.count, count));
        }
        nextCell.div = div;
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

            double yp1 = 0.0D;
            double yp2 = 0.0D;
            for (int dirPos = 0; dirPos < DIR_SIZE; dirPos++) {
                Cell cell1 = psiLayerArr[nextPsiPos].psiArr[xp1].dirCellArr[dirPos];
                Cell cell2 = psiLayerArr[nextPsiPos].psiArr[xp2].dirCellArr[dirPos];
                //int y1 = (int) (midY - VIEW_HEIGHT * (cell1.count  / (normFactor * Math.pow(2, cell1.div))));
                //int y2 = (int) (midY - VIEW_HEIGHT * (cell2.count / (normFactor * Math.pow(2, cell2.div))));
                yp1 += calcCellPobability(cell1);
                yp2 += calcCellPobability(cell2);
            }
            int y1 = (int) (midY - VIEW_HEIGHT * yp1);
            int y2 = (int) (midY - VIEW_HEIGHT * yp2);
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
