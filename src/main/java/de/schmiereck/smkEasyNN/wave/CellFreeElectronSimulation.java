package de.schmiereck.smkEasyNN.wave;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.util.FastMath;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class CellFreeElectronSimulation extends JPanel {
    private static final int VIEW_WIDTH = 1400;
    private static final int VIEW_HEIGHT = 600;

    private static final double DT = 0.00000001;
    private static final int TIMESTEPS = (int) (1.0D / DT);

    private static final double L = 10.0;
    private static final double K0 = 2.0D * FastMath.PI / L;
    private static final int PsiArrSize = 700;
    private static final double DX = L / PsiArrSize;

    private static final double HBAR = 1.0D;
    private static final double M = 1.0D;
    private static final double ALPHA = HBAR * DT / (2.0D * M * DX * DX);
    private static final Complex AlphaComplex = new Complex(0, ALPHA);

    private static final double SIGMA = 0.1;

    private class Cell {
        int dir;
        double count;
        /**
         * Count of dividers.
         * div = div/2, div/4, div/8, div/16, ...
         */
        double div = 1;
    }

    private Cell[][] psiArr;
    private int psiPos = 0;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Cell Free Electron Simulation");
        CellFreeElectronSimulation simulation = new CellFreeElectronSimulation();
        frame.add(simulation);
        frame.setSize(VIEW_WIDTH, VIEW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        new Thread(simulation::simulate).start();
    }

    public CellFreeElectronSimulation() {
        this.psiArr = new Cell[2][PsiArrSize];

        for (int nextArrPos = 0; nextArrPos < PsiArrSize; nextArrPos++) {
//            final double x = nextArrPos * DX;
//            double realPart = FastMath.exp(-0.5 * FastMath.pow((x - L / 2) / SIGMA, 2)) * FastMath.cos(K0 * x);
//            double imaginaryPart = FastMath.exp(-0.5 * FastMath.pow((x - L / 2) / SIGMA, 2)) * FastMath.sin(K0 * x);
//            this.psiArr[nextArrPos] = new Cell(realPart, imaginaryPart);
            this.psiArr[this.psiPos][nextArrPos] = new Cell();
            this.psiArr[1][nextArrPos] = new Cell();
            if (nextArrPos == PsiArrSize / 2) {
                this.psiArr[this.psiPos][nextArrPos].dir = 1;
                this.psiArr[this.psiPos][nextArrPos].count = 1;
                this.psiArr[this.psiPos][nextArrPos].div = 1;
            } else {
                this.psiArr[this.psiPos][nextArrPos].count = 0;
            }
        }

        //normalize(psiArr);
    }

    public void simulate() {
        System.out.println("START: Simulating...");

        Complex[] laplacian = new Complex[PsiArrSize];

        for (int t = 0; t < TIMESTEPS * 1000; t++) {
            int nextPsiPos = (this.psiPos + 1) % 2;

            for (int psiArrPos = 0; psiArrPos < PsiArrSize; psiArrPos++) {
                Cell nextCell = this.psiArr[nextPsiPos][psiArrPos];
                nextCell.dir = 0;
                nextCell.count = 0;
                nextCell.div = 1;
            }
            for (int psiArrPos = 0; psiArrPos < PsiArrSize; psiArrPos++) {
                Cell cell = this.psiArr[this.psiPos][psiArrPos];

                if (cell.count > 0) {
                    Cell nextCell = this.psiArr[nextPsiPos][psiArrPos];
                    Cell nextNCell = this.psiArr[nextPsiPos][(psiArrPos + cell.dir + PsiArrSize) % PsiArrSize];

                    if (cell.count > 0) {
                        nextCell.dir = cell.dir == 1 ? -1 : 1;
                        nextCell.count += cell.count;
                        //nextCell.div = cell.div / 2;
                        nextCell.div = cell.div + 1;

                        nextNCell.dir = cell.dir == 1 ? -1 : 1;
                        nextNCell.count += cell.count;
                        //nextCell.div = cell.div / 2;
                        nextNCell.div = nextCell.div;
                    }
                }
            }
            this.psiPos = nextPsiPos;

            if (t % 1 == 0) {
                this.repaint();
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("END: Simulating.");
    }

    public static final Color IMG_COLOR = new Color(255, 0, 0, 125);
    public static final Color REAL_COLOR = new Color(0, 0, 255, 125);

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int nextPsiPos = (this.psiPos + 1) % 2;

//        int sum = Arrays.stream(psiArr[nextPsiPos]).mapToInt(cell -> cell.count).map(x -> x * x).sum();
//        double normFactor = FastMath.sqrt(sum);
        //int sum = Arrays.stream(psiArr[nextPsiPos]).mapToInt(cell -> cell.count).map(x -> x).sum();
        //double normFactor = sum;
        double normFactor = 100000000.0D;

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
            int y1 = (int) (midY - VIEW_HEIGHT * getaDouble(cell1));
            int y2 = (int) (midY - VIEW_HEIGHT * getaDouble(cell2));
            g.setColor(Color.BLACK);
            g.drawLine(x1, y1, x2, y2);
        }
    }

    private static double getaDouble(Cell cell1) {
        final double ret;
        if (cell1.count == 0) {
            ret = 0.0D;
        } else {
            //ret = 1.0D / (cell1.count * (Math.pow(2, cell1.div)));
            ret = cell1.count * (1.0D / (Math.pow(2, cell1.div)));
        }
        return ret;
    }

}
