package de.schmiereck.smkEasyNN.wave;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.util.FastMath;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Zweidimensionale Arrays:
 *
 * Die Wellenfunktion psiArr und psiArrImpuls sind jetzt zweidimensionale Arrays.
 * Initialisierung der Wellenfunktion:
 *
 * Die Wellenfunktion wird als Gauß-Paket in 2D initialisiert.
 * Laplace-Operator:
 *
 * Der Laplace-Operator wird in 2D berechnet und angewendet.
 * Fourier-Transformation:
 *
 * Die Methode fourierTransform2D führt die 2D-Fourier-Transformationen durch.
 */
public class ImpulseElectronFourier2DSimulation extends JPanel {
    private static final int VIEW_WIDTH = 800;
    private static final int VIEW_HEIGHT = 800;

    private static final double DT = 0.00005;
    private static final int TIMESTEPS = (int) (1.0D / DT);

    private static final double L = 10.0;
    private static final double K0 = 2.0D * FastMath.PI / L;
    //private static final int PsiArrSize = 200; // Size in one dimension
    private static final int PsiArrSize = 50; // Size in one dimension
    private static final double DX = L / PsiArrSize;

    private static final double HBAR = 1.0D;
    private static final double M = 1.0D;
    private static final double ALPHA = HBAR * DT / (2.0D * M * DX * DX);
    private static final Complex AlphaComplex = new Complex(0, ALPHA);

    private static final double SIGMA = 0.1;

    private Complex[][] psiArr;
    private Complex[][] psiArrImpuls;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Impulse Electron Fourier 2D Simulation");
        ImpulseElectronFourier2DSimulation simulation = new ImpulseElectronFourier2DSimulation();
        frame.add(simulation);
        frame.setSize(VIEW_WIDTH, VIEW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        new Thread(simulation::simulate).start();
    }

    public ImpulseElectronFourier2DSimulation() {
        this.psiArr = new Complex[PsiArrSize][PsiArrSize];
        this.psiArrImpuls = new Complex[PsiArrSize][PsiArrSize];

        // Initialize the wave packet with a specific momentum
        for (int x = 0; x < PsiArrSize; x++) {
            for (int y = 0; y < PsiArrSize; y++) {
                double xPos = x * DX;
                double yPos = y * DX;
                double realPart = FastMath.exp(-0.5 * (FastMath.pow((xPos - L / 2) / SIGMA, 2) + FastMath.pow((yPos - L / 2) / SIGMA, 2)))
                        * FastMath.cos(K0 * xPos);
                double imaginaryPart = FastMath.exp(-0.5 * (FastMath.pow((xPos - L / 2) / SIGMA, 2) + FastMath.pow((yPos - L / 2) / SIGMA, 2)))
                        * FastMath.sin(K0 * xPos);
                this.psiArr[x][y] = new Complex(realPart, imaginaryPart);
            }
        }

        normalize(psiArr);
    }

    public void simulate() {
        System.out.println("START: Simulating...");

        Complex[][] laplacian = new Complex[PsiArrSize][PsiArrSize];

        for (int t = 0; t < TIMESTEPS * 1000; t++) {
            System.out.println("step: %d".formatted(t));

            // Fourier transform: position -> momentum
            fourierTransform2D(psiArr, psiArrImpuls, 1);

            // Time evolution in momentum space
            for (int px = 0; px < PsiArrSize; px++) {
                for (int py = 0; py < PsiArrSize; py++) {
                    double momentumX = px * 2 * Math.PI / (PsiArrSize * DX);
                    double momentumY = py * 2 * Math.PI / (PsiArrSize * DX);
                    double energy = HBAR * HBAR * (momentumX * momentumX + momentumY * momentumY) / (2 * M);
                    psiArrImpuls[px][py] = psiArrImpuls[px][py].multiply(new Complex(Math.cos(-energy * DT / HBAR), Math.sin(-energy * DT / HBAR)));
                }
            }

            // Inverse Fourier transform: momentum -> position
            fourierTransform2D(psiArrImpuls, psiArr, -1);

            // Apply Laplace operator in position space
            for (int x = 0; x < PsiArrSize; x++) {
                for (int y = 0; y < PsiArrSize; y++) {
                    Complex left = this.psiArr[(x - 1 + PsiArrSize) % PsiArrSize][y];
                    Complex right = this.psiArr[(x + 1) % PsiArrSize][y];
                    Complex up = this.psiArr[x][(y - 1 + PsiArrSize) % PsiArrSize];
                    Complex down = this.psiArr[x][(y + 1) % PsiArrSize];
                    Complex middle = this.psiArr[x][y];

                    laplacian[x][y] = (right.add(left).add(up).add(down).
                            subtract(middle.multiply(4.0))).divide(DX * DX);
                }
            }

            for (int x = 0; x < PsiArrSize; x++) {
                for (int y = 0; y < PsiArrSize; y++) {
                    this.psiArr[x][y] = this.psiArr[x][y].subtract(laplacian[x][y].multiply(AlphaComplex));
                }
            }

            normalize(psiArr);

            //if (t % 10 == 0) {
            if (t % 1 == 0) {
                this.repaint();
                try {
                    Thread.sleep(50);
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

        int cellWidth = VIEW_WIDTH / PsiArrSize;
        int cellHeight = VIEW_HEIGHT / PsiArrSize;
        for (int x = 0; x < PsiArrSize; x++) {
            for (int y = 0; y < PsiArrSize; y++) {
                int xPos = x * cellWidth;
                int yPos = y * cellHeight;

                int colorValue = (int) (psiArr[x][y].abs() * 255);
                colorValue = Math.min(255, Math.max(0, colorValue)); // Clamp the value to [0, 255]

                //g.setColor(new Color(colorValue, 0, 255 - colorValue, 125));
                g.setColor(new Color(colorValue, colorValue, colorValue));
                g.fillRect(xPos, yPos, cellWidth, cellHeight);
            }
        }
    }

    private static void normalize(Complex[][] psi) {
        double sum = Arrays.stream(psi).flatMap(Arrays::stream).mapToDouble(Complex::abs).map(x -> x * x).sum();
        double normFactor = FastMath.sqrt(sum);
        for (int x = 0; x < psi.length; x++) {
            for (int y = 0; y < psi[x].length; y++) {
                psi[x][y] = psi[x][y].divide(normFactor);
            }
        }
    }

    private static void fourierTransform2D(Complex[][] input, Complex[][] output, int direction) {
        int n = input.length;
        for (int kx = 0; kx < n; kx++) {
            for (int ky = 0; ky < n; ky++) {
                output[kx][ky] = Complex.ZERO;
                for (int x = 0; x < n; x++) {
                    for (int y = 0; y < n; y++) {
                        double phase = direction * -2.0 * Math.PI * (kx * x + ky * y) / n;
                        output[kx][ky] = output[kx][ky].add(input[x][y].multiply(new Complex(Math.cos(phase), Math.sin(phase))));
                    }
                }
                if (direction == -1) {
                    output[kx][ky] = output[kx][ky].divide(n * n);
                }
            }
        }
    }
}
