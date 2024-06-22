package de.schmiereck.smkEasyNN.wave;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.util.FastMath;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Wenn Sie jedoch eine explizite Lösung des Schrödinger-Gleichung verwenden möchten, können Sie dies in der Ortsdarstellung direkt durch den Laplace-Operator und die zeitliche Ableitung tun. Hier ist ein überarbeiteter Ansatz, der die Fourier-Transformation vermeidet und direkt die Schrödinger-Gleichung in der Ortsdarstellung löst:
 *
 * Die Zeitentwicklung wird direkt in der Ortsdarstellung durchgeführt, ohne die Notwendigkeit einer Fourier-Transformation.
 *
 * Gesamter Ablauf:
 * Die Wellenfunktion wird initialisiert.
 * Der Laplace-Operator wird auf die Wellenfunktion angewendet.
 * Die Wellenfunktion wird gemäß der Schrödinger-Gleichung zeitlich weiterentwickelt.
 * Die Wellenfunktion wird normalisiert.
 * Die Resultate werden visualisiert.
 */
public class ImpulseElectronLocation2DSimulation extends JPanel {
    private static final int VIEW_WIDTH = 800;
    private static final int VIEW_HEIGHT = 800;

    private static final double DT = 0.00001;
    private static final int TIMESTEPS = (int) (1.0D / DT);

    private static final double L = 10.0;
    private static final double K0 = 2.0D * FastMath.PI / L;
    //private static final int PsiArrSize = 200; // Size in one dimension
    private static final int PsiArrSize = 75; // Size in one dimension
    private static final double DX = L / PsiArrSize;

    private static final double HBAR = 1.0D;
    private static final double M = 1.0D;
    private static final double ALPHA = HBAR * DT / (2.0D * M * DX * DX);
    private static final Complex AlphaComplex = new Complex(0, ALPHA);

    private static final double SIGMA = 0.1;

    private Complex[][] psiArr;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Impulse Electron Location 2D Simulation");
        ImpulseElectronLocation2DSimulation simulation = new ImpulseElectronLocation2DSimulation();
        frame.add(simulation);
        frame.setSize(VIEW_WIDTH, VIEW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        new Thread(simulation::simulate).start();
    }

    public ImpulseElectronLocation2DSimulation() {
        this.psiArr = new Complex[PsiArrSize][PsiArrSize];

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

            // Apply Laplace operator in position space
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

            // Anwendung der Zeitentwicklung:
            for (int x = 0; x < PsiArrSize; x++) {
                for (int y = 0; y < PsiArrSize; y++) {
                    this.psiArr[x][y] = this.psiArr[x][y].subtract(laplacian[x][y].multiply(AlphaComplex));
                }
            }

            normalize(psiArr);

            if (t % 5 == 0) {
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
