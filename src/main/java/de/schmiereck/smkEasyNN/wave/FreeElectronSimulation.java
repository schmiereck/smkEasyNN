package de.schmiereck.smkEasyNN.wave;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.util.FastMath;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class FreeElectronSimulation extends JPanel {
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

    private Complex[] psiArr;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Free Electron Simulation");
        FreeElectronSimulation simulation = new FreeElectronSimulation();
        frame.add(simulation);
        frame.setSize(VIEW_WIDTH, VIEW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        new Thread(simulation::simulate).start();
    }

    public FreeElectronSimulation() {
        this.psiArr = new Complex[PsiArrSize];

        for (int psiPos = 0; psiPos < PsiArrSize; psiPos++) {
            final double x = psiPos * DX;
            double realPart = FastMath.exp(-0.5 * FastMath.pow((x - L / 2) / SIGMA, 2)) * FastMath.cos(K0 * x);
            double imaginaryPart = FastMath.exp(-0.5 * FastMath.pow((x - L / 2) / SIGMA, 2)) * FastMath.sin(K0 * x);
            this.psiArr[psiPos] = new Complex(realPart, imaginaryPart);
        }

        normalize(psiArr);
    }

    public void simulate() {
        System.out.println("START: Simulating...");

        Complex[] laplacian = new Complex[PsiArrSize];

        for (int t = 0; t < TIMESTEPS * 1000; t++) {
            for (int j = 0; j < PsiArrSize; j++) {
                Complex left = this.psiArr[(j - 1 + PsiArrSize) % PsiArrSize];
                Complex middle = this.psiArr[j];
                Complex right = this.psiArr[(j + 1) % PsiArrSize];

                laplacian[j] = (right.subtract(middle.multiply(2.0D)).add(left)).divide(DX * DX);
            }

            for (int j = 0; j < PsiArrSize; j++) {
                this.psiArr[j] = this.psiArr[j].subtract(laplacian[j].multiply(AlphaComplex));
            }

            normalize(psiArr);

            if (t % 500 == 0) {
                this.repaint();
                try {
                    Thread.sleep(25);
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

        int midY = VIEW_HEIGHT / 2;
        for (int j = 0; j < PsiArrSize - 1; j++) {
            int x1 = (int) (j * (VIEW_WIDTH / (double) PsiArrSize));
            int x2 = (int) ((j + 1) * (VIEW_WIDTH / (double) PsiArrSize));

            int y1 = (int) (midY - psiArr[j].abs() * VIEW_HEIGHT);
            int y2 = (int) (midY - psiArr[j + 1].abs() * VIEW_HEIGHT);
            g.setColor(Color.BLACK);
            g.drawLine(x1, y1, x2, y2);

            int y1Real = (int) (midY - psiArr[j].getReal() * VIEW_HEIGHT);
            int y2Real = (int) (midY - psiArr[j + 1].getReal() * VIEW_HEIGHT);
            g.setColor(REAL_COLOR);
            g.drawLine(x1, y1Real, x2, y2Real);

            int y1Imag = (int) (midY - psiArr[j].getImaginary() * VIEW_HEIGHT);
            int y2Imag = (int) (midY - psiArr[j + 1].getImaginary() * VIEW_HEIGHT);
            g.setColor(IMG_COLOR);
            g.drawLine(x1, y1Imag, x2, y2Imag);
        }
    }

    private static void normalize(Complex[] psi) {
        double sum = Arrays.stream(psi).mapToDouble(Complex::abs).map(x -> x * x).sum();
        double normFactor = FastMath.sqrt(sum);
        for (int j = 0; j < PsiArrSize; j++) {
            psi[j] = psi[j].divide(normFactor);
        }
    }
}
