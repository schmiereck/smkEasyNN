package de.schmiereck.smkEasyNN.wave;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.util.FastMath;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class StandingWaveSimulation extends JPanel {
    private static final int VIEW_WIDTH = 1400;
    private static final int VIEW_HEIGHT = 600;

    // Zeitdiskretisierung der Schrödingergleichung, Symbol: Δ𝑡:
    //private static final double DT = 0.001;
    //private static final double DT = 0.0001;
    //private static final double DT = 0.00005;
    //private static final double DT = 0.00001;
    private static final double DT = 0.00000001;
    private static final int TIMESTEPS = (int) (1.0D / DT);

    // physikalische Länge des Gitters
    private static final double L = 10.0;
    //private static final double L = 100.0;
    //private static final double L = FastMath.PI;
    // Wellenzahl des Teilchens, Symbol: 𝑘:
    private static final double K0 = 2.0D * FastMath.PI / L;
    //private static final double K0 = 0.0002D * FastMath.PI / L;

    //private static final int PsiArrSize = 1000;
    private static final int PsiArrSize = 700;
    // Diskretisierung des Gitters, Symbol: Δ𝑥:
    private static final double DX = L / PsiArrSize;

    // (Plancksches Wirkungsquantum durch 2π, Symbol: ℏ
    private static final double HBAR = 1.0D;
    // Masse des Teilchens, Symbol: 𝑚:
    private static final double M = 1.0D;
    // Die Breite des Gauss-Pakets, Symbol: 𝜎:
    // In der Simulation werden HBAR und M verwendet, um die zeitliche und räumliche Diskretisierung der Schrödingergleichung zu skalieren. Der Parameter ALPHA ist definiert als:
    // ALPHA = (ℏ⋅DT) / (2⋅𝑚⋅(DX*DX))
    // Dies skaliert die numerische Berechnung des Laplace-Operators und die zeitliche Integration der Wellenfunktion.
    private static final double ALPHA = HBAR * DT / (2.0D * M * DX * DX);
    private static final Complex AlphaComplex = new Complex(0, ALPHA);

    // Breite des Gauss-Pakets, Symbol: 𝜎:
    private static final double SIGMA = 0.1;
    //private static final double SIGMA = 0.5;

    private Complex[] psiArr;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Standing Wave Simulation");
        StandingWaveSimulation simulation = new StandingWaveSimulation();
        frame.add(simulation);
        frame.setSize(VIEW_WIDTH, VIEW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        new Thread(simulation::simulate).start();
    }

    public StandingWaveSimulation() {
        this.psiArr = new Complex[PsiArrSize];
        //final double[] x = new double[N];

        // Initialize x array and psi array with Gaussian packet
        for (int psiPos = 0; psiPos < PsiArrSize; psiPos++) {
            //x[psiPos] = psiPos * DX;
            final double x = psiPos * DX;
            //double realPart = FastMath.exp(-0.5 * FastMath.pow((x[psiPos] - L / 2) / SIGMA, 2)) * FastMath.cos(K0 * x[psiPos]);
            //double imaginaryPart = FastMath.exp(-0.5 * FastMath.pow((x[psiPos] - L / 2) / SIGMA, 2)) * FastMath.sin(K0 * x[psiPos]);
            double realPart = FastMath.exp(-0.5 * FastMath.pow((x - L / 2) / SIGMA, 2)) * FastMath.cos(K0 * x);
            double imaginaryPart = FastMath.exp(-0.5 * FastMath.pow((x - L / 2) / SIGMA, 2)) * FastMath.sin(K0 * x);
            this.psiArr[psiPos] = new Complex(realPart, imaginaryPart);
        }

        normalize(psiArr);
    }

    public void simulate() {
        System.out.println("START: Simulating...");

        Complex[] laplacian = new Complex[PsiArrSize];
        // Die Schrödingergleichung wird iterativ gelöst.
        for (int t = 0; t < TIMESTEPS * 1000; t++) {
        //for (int t = 0; t < 50; t++) {
        //int t = 0; {

            for (int j = 1; j < PsiArrSize - 1; j++) {
                Complex left = this.psiArr[j - 1];
                Complex middle = this.psiArr[j];
                Complex right = this.psiArr[j + 1];

                // Laplace-Operator: (psi[j+1] - 2*psi[j] + psi[j-1]) / dx^2
                // m = r - 2*m + l
                laplacian[j] = (right.subtract(middle.multiply(2.0D)).add(left)).
                        divide(DX * DX);
            }

            for (int j = 1; j < PsiArrSize - 1; j++) {
                if (withHarmonischenOszillator) {
                    final double x = j * DX;
                    double v = potential(x);
                    Complex potentialTerm = this.psiArr[j].multiply(new Complex(0, DT * v / HBAR));
                    this.psiArr[j] = this.psiArr[j].subtract(laplacian[j].multiply(AlphaComplex)).subtract(potentialTerm);
                } else {
                    // Die Wellenfunktion wird mit dem Laplace-Operator und dem Potential multipliziert.
                    this.psiArr[j] = this.psiArr[j].subtract(laplacian[j].multiply(AlphaComplex));
                }
            }

            normalize(psiArr);

            //if (t % 1 == 0) {
            //if (t % 100 == 0) {
            //if (t % 10 == 0) {
            if (t % 500 == 0) {
                this.repaint();
                try {
                    //Thread.sleep(250);
                    //Thread.sleep(50);
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("END: Simulating.");
    }

    private static final boolean withHarmonischenOszillator = true;
    // Frequenz des harmonischen Oszillators, Symbol: 𝜔:
    private static final double OMEGA = 1.0;
    //private static final double X0 = L / 2; // Zentrum des Oszillators
    private static final double X0 = L * 0.3D; // Zentrum des Oszillators
    //private static final double OFAKTOR = 0.5;
    private static final double OFAKTOR = (0.0001D / DT);

    private double potential(double x) {
        // Harmonischer Oszillator
        //return 0.1 * M * OMEGA * OMEGA * x * x;
        // Harmonischer Oszillator zentriert bei X0
        //return 0.5 * M * OMEGA * OMEGA * (x - X0) * (x - X0);
        //return 50000.0 * M * OMEGA * OMEGA * (x - X0) * (x - X0);
        return OFAKTOR * M * OMEGA * OMEGA * (x - X0) * (x - X0);
    }

    public static final Color IMG_COLOR =  new Color(255, 0, 0, 125);
    public static final Color REAL_COLOR =  new Color(0, 0, 255, 125);
    public static final Color POT_COLOR =  new Color(0, 255, 0, 125);

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int midY = VIEW_HEIGHT / 2;
        for (int j = 0; j < PsiArrSize - 1; j++) {
            int x1 = (int) (j * (VIEW_WIDTH / (double) PsiArrSize));
            int x2 = (int) ((j + 1) * (VIEW_WIDTH / (double) PsiArrSize));

            // Die Wahrscheinlichkeitsdichte der Wellenfunktion ∣𝜓∣2∣ψ∣2 geplottet, die immer positiv ist.
            int y1 = (int) (midY - psiArr[j].abs() * VIEW_HEIGHT);
            int y2 = (int) (midY - psiArr[j + 1].abs() * VIEW_HEIGHT);
            g.setColor(Color.BLACK);
            g.drawLine(x1, y1, x2, y2);

            // Die blaue Linie zeigt den realen Teil der Wellenfunktion.
            int y1Real = (int) (midY - psiArr[j].getReal() * VIEW_HEIGHT);
            int y2Real = (int) (midY - psiArr[j + 1].getReal() * VIEW_HEIGHT);
            g.setColor(REAL_COLOR);
            g.drawLine(x1, y1Real, (int) ((j + 1) * (VIEW_WIDTH / (double) PsiArrSize)), y2Real);

            //  Die rote Linie zeigt den imaginären Teil der Wellenfunktion.
            int y1Imag = (int) (midY - psiArr[j].getImaginary() * VIEW_HEIGHT);
            int y2Imag = (int) (midY - psiArr[j + 1].getImaginary() * VIEW_HEIGHT);
            g.setColor(IMG_COLOR);
            g.drawLine(x1, y1Imag, x2, y2Imag);

            // Potential
            final double xPot = j * DX;
            final double x2Pot = (j + 1) * DX;
            int y1Pot = (int) (midY - (potential(xPot) / OFAKTOR) * VIEW_HEIGHT);
            int y2Pot = (int) (midY - (potential(x2Pot) / OFAKTOR) * VIEW_HEIGHT);
            g.setColor(POT_COLOR);
            g.drawLine(x1, y1Pot, x2, y2Pot);
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
