package de.schmiereck.smkEasyNN.wave;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.util.FastMath;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class ImpulseElectronFourier2DSimulation2 extends JPanel {
    private static final int VIEW_WIDTH = 900;
    private static final int VIEW_HEIGHT = 900;

    private static final double DT = 0.00005;
    private static final int TIMESTEPS = (int) (1.0D / DT);

    private static final double L = 10.0;
    private static final double K0 = 2.0D * FastMath.PI / L;
    private static final int PsiArrSize = 75; // Reduced for 2D simulation
    private static final double DX = L / PsiArrSize;

    private static final double HBAR = 1.0D;
    private static final double M = 1.0D;
    private static final double ALPHA = HBAR * DT / (2.0D * M * DX * DX);
    private static final Complex AlphaComplex = new Complex(0, ALPHA);

    private static final double SIGMA = 0.1;

    private Complex[][] psiArr;
    private Complex[][] psiArrImpuls;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Impulse Electron Simulation 2D");
        ImpulseElectronFourier2DSimulation2 simulation = new ImpulseElectronFourier2DSimulation2();
        frame.add(simulation);
        frame.setSize(VIEW_WIDTH, VIEW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        new Thread(simulation::simulate).start();
    }

    public ImpulseElectronFourier2DSimulation2() {
        this.psiArr = new Complex[PsiArrSize][PsiArrSize];
        this.psiArrImpuls = new Complex[PsiArrSize][PsiArrSize];

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

        for (int t = 0; t < TIMESTEPS; t++) {
            System.out.println("step: %d".formatted(t));

            // Fouriertransformation: Ortsdarstellung -> Impulsdarstellung
            for (int px = 0; px < PsiArrSize; px++) {
                for (int py = 0; py < PsiArrSize; py++) {
                    double momentumX = px * 2 * Math.PI / (PsiArrSize * DX);
                    double momentumY = py * 2 * Math.PI / (PsiArrSize * DX);
                    Complex sum = new Complex(0, 0);
                    for (int x = 0; x < PsiArrSize; x++) {
                        for (int y = 0; y < PsiArrSize; y++) {
                            double positionX = x * DX;
                            double positionY = y * DX;
                            double phase = (momentumX * positionX + momentumY * positionY) / HBAR;
                            sum = sum.add(psiArr[x][y].multiply(new Complex(Math.cos(phase), Math.sin(phase))));
                        }
                    }
                    psiArrImpuls[px][py] = sum.divide(PsiArrSize * PsiArrSize);
                }
            }

            // Zeitentwicklung in der Impulsdarstellung
            for (int px = 0; px < PsiArrSize; px++) {
                for (int py = 0; py < PsiArrSize; py++) {
                    double momentumX = px * 2 * Math.PI / (PsiArrSize * DX);
                    double momentumY = py * 2 * Math.PI / (PsiArrSize * DX);
                    double energy = HBAR * HBAR * (momentumX * momentumX + momentumY * momentumY) / (2 * M);

                    // Zeitentwicklung: psi_p(t+DT) = psi_p(t) * exp(-i * E / hbar * DT)
                    psiArrImpuls[px][py] = psiArrImpuls[px][py].multiply(new Complex(Math.cos(-energy * DT / HBAR), Math.sin(-energy * DT / HBAR)));
                }
            }

            // Inverse Fouriertransformation: Impulsdarstellung -> Ortsdarstellung
            for (int x = 0; x < PsiArrSize; x++) {
                for (int y = 0; y < PsiArrSize; y++) {
                    Complex sum = new Complex(0, 0);
                    for (int px = 0; px < PsiArrSize; px++) {
                        for (int py = 0; py < PsiArrSize; py++) {
                            double momentumX = px * 2 * Math.PI / (PsiArrSize * DX);
                            double momentumY = py * 2 * Math.PI / (PsiArrSize * DX);
                            double phase = -(momentumX * x * DX + momentumY * y * DX) / HBAR;
                            sum = sum.add(psiArrImpuls[px][py].multiply(new Complex(Math.cos(phase), Math.sin(phase))));
                        }
                    }
                    psiArr[x][y] = sum.divide(PsiArrSize * PsiArrSize);
                }
            }

            // Berechnung des Laplace-Operators für Ortsdarstellung
            for (int x = 0; x < PsiArrSize; x++) {
                for (int y = 0; y < PsiArrSize; y++) {
                    int xm1 = (x - 1 + PsiArrSize) % PsiArrSize;
                    int xp1 = (x + 1) % PsiArrSize;
                    int ym1 = (y - 1 + PsiArrSize) % PsiArrSize;
                    int yp1 = (y + 1) % PsiArrSize;
                    Complex left = this.psiArr[xm1][y];
                    Complex right = this.psiArr[xp1][y];
                    Complex up = this.psiArr[x][ym1];
                    Complex down = this.psiArr[x][yp1];
                    Complex middle = this.psiArr[x][y];
                    Complex leftUp = this.psiArr[xm1][ym1].divide(1.414D);
                    Complex leftDown = this.psiArr[xm1][yp1].divide(1.414D);
                    Complex rightUp = this.psiArr[xp1][ym1].divide(1.414D);
                    Complex rightDown = this.psiArr[xp1][yp1].divide(1.414D);

                    laplacian[x][y] = (right.add(left).add(up).add(down).
                            add(leftUp).add(leftDown).add(rightUp).add(rightDown).
                            subtract(middle.multiply(4.0))).divide(DX * DX);
                }
            }

            // Zeitentwicklung in der Ortsdarstellung
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

        int cellWidth = VIEW_WIDTH / PsiArrSize;
        int cellHeight = VIEW_HEIGHT / PsiArrSize;
        for (int x = 0; x < PsiArrSize - 1; x++) {
            for (int y = 0; y < PsiArrSize - 1; y++) {
                int xPos = x * cellWidth;
                int yPos = y * cellHeight;

                int x1 = (int) (x * (VIEW_WIDTH / (double) PsiArrSize));
                int x2 = (int) ((x + 1) * (VIEW_WIDTH / (double) PsiArrSize));
                int y1 = (int) (y * (VIEW_HEIGHT / (double) PsiArrSize));
                int y2 = (int) ((y + 1) * (VIEW_HEIGHT / (double) PsiArrSize));

                {
                    int colorValue = (int) (psiArr[x][y].abs() * 255);
                    colorValue = Math.min(255, Math.max(0, colorValue)); // Clamp the value to [0, 255]

                    //g.setColor(new Color(colorValue, 0, 255 - colorValue, 125));
                    g.setColor(new Color(colorValue, colorValue, colorValue));
                    g.fillRect(xPos, yPos, cellWidth, cellHeight);
                }
                {
                    int colorValue = (int) Math.abs(psiArr[x][y].getImaginary() * 128);
                    colorValue = Math.min(255, Math.max(0, colorValue)); // Clamp the value to [0, 255]

                    g.setColor(new Color(255, 0, 0, colorValue));
                    g.fillRect(xPos, yPos, cellWidth, cellHeight);
                }
                {
                    int colorValue = (int) Math.abs(psiArr[x][y].getReal() * 128);
                    colorValue = Math.min(255, Math.max(0, colorValue)); // Clamp the value to [0, 255]

                    g.setColor(new Color(0, 0, 255, colorValue));
                    g.fillRect(xPos, yPos, cellWidth, cellHeight);
                }
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
}
