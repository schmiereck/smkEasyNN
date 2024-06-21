package de.schmiereck.smkEasyNN.wave;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.util.FastMath;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

public class ImpulseElectronSimulation extends JPanel {
    private static final int VIEW_WIDTH = 1400;
    private static final int VIEW_HEIGHT = 600;

    private static final double DT = 0.00001;
    private static final int TIMESTEPS = (int) (1.0D / DT);

    private static final double L = 10.0;
    private static final double K0 = 2.0D * FastMath.PI / L;
    private static final int PsiArrSize = 700; // 700/2 as in your code
    private static final double DX = L / PsiArrSize;

    private static final double HBAR = 1.0D;
    private static final double M = 1.0D;
    private static final double ALPHA = HBAR * DT / (2.0D * M * DX * DX);
    private static final Complex AlphaComplex = new Complex(0, ALPHA);

    private static final double SIGMA = 0.025;

    private Complex[] psiArr;
    private Complex[] psiArrImpuls;
    private Complex[] laplacian;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Impulse Electron Simulation");
        ImpulseElectronSimulation simulation = new ImpulseElectronSimulation();
        frame.add(simulation);
        frame.setSize(VIEW_WIDTH, VIEW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        new Thread(simulation::simulate).start();
    }

    public ImpulseElectronSimulation() {
        this.psiArr = new Complex[PsiArrSize];
        this.psiArrImpuls = new Complex[PsiArrSize];
        this.laplacian = new Complex[PsiArrSize];

        // Initialize the wave packet with a specific momentum
        for (int psiPos = 0; psiPos < PsiArrSize; psiPos++) {
            final double x = psiPos * DX;
            double realPart = FastMath.exp(-0.5 * FastMath.pow((x - L / 2) / SIGMA, 2)) * FastMath.cos(K0 * x);
            double imaginaryPart = FastMath.exp(-0.5 * FastMath.pow((x - L / 2) / SIGMA, 2)) * FastMath.sin(K0 * x);
            this.psiArr[psiPos] = new Complex(realPart, imaginaryPart);
        }

        normalize(psiArr);
    }

    private static final boolean useLaplaceOperator = true;

    public void simulate() {
        System.out.println("START: Simulating...");

        for (int t = 0; t < TIMESTEPS * 1000; t++) {
            // Fourier transform: position -> momentum
            fourierTransform(psiArr, psiArrImpuls, 1);

            // Time evolution in momentum space
            for (int p = 0; p < PsiArrSize; p++) {
                double momentum = p * 2 * Math.PI / (PsiArrSize * DX);
                double energy = HBAR * HBAR * momentum * momentum / (2 * M);
                psiArrImpuls[p] = psiArrImpuls[p].multiply(new Complex(Math.cos(-energy * DT / HBAR), Math.sin(-energy * DT / HBAR)));
            }

            // Inverse Fourier transform: momentum -> position
            fourierTransform(psiArrImpuls, psiArr, -1);

            if (useLaplaceOperator) {
                // Apply Laplace operator in position space (optional)
                for (int j = 0; j < PsiArrSize; j++) {
                    Complex left = this.psiArr[(j - 1 + PsiArrSize) % PsiArrSize];
                    Complex middle = this.psiArr[j];
                    Complex right = this.psiArr[(j + 1) % PsiArrSize];

                    laplacian[j] = (right.subtract(middle.multiply(2.0D)).add(left)).divide(DX * DX);
                }

                for (int j = 0; j < PsiArrSize; j++) {
                    this.psiArr[j] = this.psiArr[j].subtract(laplacian[j].multiply(AlphaComplex));
                }
            }

            normalize(psiArr);

            //if (t % 10 == 0) {
            if (t % 1 == 0) {
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
    public static final Color IMP_IMG_COLOR = new Color(255, 200, 0, 125);
    public static final Color IMP_REAL_COLOR = new Color(0, 200, 255, 125);
    public static final Color LP_IMG_COLOR = new Color(255, 0, 200, 25);
    public static final Color LP_REAL_COLOR = new Color(200, 0, 255, 25);

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int midY = VIEW_HEIGHT / 2;
        for (int j = 0; j < PsiArrSize - 1; j++) {
            int x1 = (int) (j * (VIEW_WIDTH / (double) PsiArrSize));
            int x2 = (int) ((j + 1) * (VIEW_WIDTH / (double) PsiArrSize));
            {
                int y1 = (int) (midY - psiArr[j].abs() * VIEW_HEIGHT);
                int y2 = (int) (midY - psiArr[j + 1].abs() * VIEW_HEIGHT);
                g.setColor(Color.BLACK);
                g.drawLine(x1, y1, x2, y2);
            }
            {
                int y1Real = (int) (midY - psiArr[j].getReal() * VIEW_HEIGHT);
                int y2Real = (int) (midY - psiArr[j + 1].getReal() * VIEW_HEIGHT);
                g.setColor(REAL_COLOR);
                g.drawLine(x1, y1Real, x2, y2Real);

                int y1Imag = (int) (midY - psiArr[j].getImaginary() * VIEW_HEIGHT);
                int y2Imag = (int) (midY - psiArr[j + 1].getImaginary() * VIEW_HEIGHT);
                g.setColor(IMG_COLOR);
                g.drawLine(x1, y1Imag, x2, y2Imag);
            }
            {
                int y1Real = (int) (midY - psiArrImpuls[j].getReal() * VIEW_HEIGHT / 20);
                int y2Real = (int) (midY - psiArrImpuls[j + 1].getReal() * VIEW_HEIGHT / 20);
                g.setColor(IMP_REAL_COLOR);
                g.drawLine(x1, y1Real, x2, y2Real);

                int y1Imag = (int) (midY - psiArrImpuls[j].getImaginary() * VIEW_HEIGHT / 20);
                int y2Imag = (int) (midY - psiArrImpuls[j + 1].getImaginary() * VIEW_HEIGHT / 20);
                g.setColor(IMP_IMG_COLOR);
                g.drawLine(x1, y1Imag, x2, y2Imag);
            }
            if (useLaplaceOperator && Objects.nonNull(laplacian[j])) {
                int y1Real = (int) (midY - laplacian[j].getReal() * VIEW_HEIGHT / 5000);
                int y2Real = (int) (midY - laplacian[j + 1].getReal() * VIEW_HEIGHT / 5000);
                g.setColor(LP_REAL_COLOR);
                g.drawLine(x1, y1Real, x2, y2Real);

                int y1Imag = (int) (midY - laplacian[j].getImaginary() * VIEW_HEIGHT / 5000);
                int y2Imag = (int) (midY - laplacian[j + 1].getImaginary() * VIEW_HEIGHT / 5000);
                g.setColor(LP_IMG_COLOR);
                g.drawLine(x1, y1Imag, x2, y2Imag);
            }
        }
    }

    private static void normalize(Complex[] psi) {
        double sum = Arrays.stream(psi).mapToDouble(Complex::abs).map(x -> x * x).sum();
        double normFactor = FastMath.sqrt(sum);
        for (int j = 0; j < psi.length; j++) {
            psi[j] = psi[j].divide(normFactor);
        }
    }

    private static void fourierTransform(Complex[] input, Complex[] output, int direction) {
        int n = input.length;
        for (int k = 0; k < n; k++) {
            output[k] = Complex.ZERO;
            for (int j = 0; j < n; j++) {
                double phase = direction * -2.0 * Math.PI * k * j / n;
                output[k] = output[k].add(input[j].multiply(new Complex(Math.cos(phase), Math.sin(phase))));
            }
            if (direction == -1) {
                output[k] = output[k].divide(n);
            }
        }
    }
}
