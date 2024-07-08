package de.schmiereck.smkEasyNN.wave;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * https://chatgpt.com/c/20509527-0f81-4457-b7a7-d869fc25e129
 * elektrische Potential eines Elektrons:
 * https://chatgpt.com/c/2386af98-aa80-4bb0-8dce-c623e8645dfb
 */
public class Field2LayerImpulseElectronSimulation extends JPanel {
    private static final int VIEW_WIDTH = 1400;
    private static final int VIEW_HEIGHT = 600;
    private static final int VIEW_EXTRA_HEIGHT = VIEW_HEIGHT / 8;

    private static final double DT = 0.00000001;
    private static final int TIMESTEPS = (int) (1.0D / DT);

    private static final int PsiArrSize = 200;

    private final static int DIR_SIZE = 2;
    private final static int MAX_DIV = 64/4;
    private final static int MAX_SPEED_C = 8;//12;
    private final static int PSI_LAYER_SIZE = 2;
    private final static int MAX_SPIN = 6;
    private final static int TYPE_SIZE = 2;

    private static class SourceEvent {
        SourceEvent parentSourceEvent = null;
    }

    private static class Node {
        long count = 0;
        long eFieldCount;
        SourceEvent sourceEvent = null;

        public static Node[][] createDirSpeedNodeArr() {
            final Node[][] dirSpeedNodeArr = new Node[DIR_SIZE][MAX_SPEED_C + 1];

            for (int dirPos = 0; dirPos < DIR_SIZE; dirPos++) {
                for (int speedPos = 0; speedPos <= MAX_SPEED_C; speedPos++) {
                    dirSpeedNodeArr[dirPos][speedPos] = new Node();
                }
            }
            return dirSpeedNodeArr;
        }
    }

    public static class SpeedCntNode {
        /**
         * Speed of the electron in the direction.
         */
        final Node[][] dirSpeedNodeArr = Node.createDirSpeedNodeArr();

        public static SpeedCntNode[] createSpeedCntNodeArr() {
            final SpeedCntNode[] speedCntNodeNodeArr = new SpeedCntNode[MAX_SPEED_C + 1];

            for (int dirPos = 0; dirPos <= MAX_SPEED_C; dirPos++) {
                speedCntNodeNodeArr[dirPos] = new SpeedCntNode();
            }
            return speedCntNodeNodeArr;
        }
    }

    public static class SpinSpeedDirNode {
        final SpeedCntNode[] speedCntNodeArr = SpeedCntNode.createSpeedCntNodeArr();

        public static SpinSpeedDirNode[] createSpinSpeedDirNodeArr() {
            final SpinSpeedDirNode[] spinSpeedDirNodeArr = new SpinSpeedDirNode[DIR_SIZE];

            for (int dirPos = 0; dirPos < DIR_SIZE; dirPos++) {
                spinSpeedDirNodeArr[dirPos] = new SpinSpeedDirNode();
            }
            return spinSpeedDirNodeArr;
        }
    }

    private static class SpinCntNode {
        final SpinSpeedDirNode[] spinSpeedDirNodeArr = SpinSpeedDirNode.createSpinSpeedDirNodeArr();

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

    private static class SpinDirNode {
        final SpinNode[] spinNodeArr = SpinNode.createSpinNodeArr();

        public static SpinDirNode[] createSpinDirNodeArr() {
            final SpinDirNode[] spinDirNodeArr = new SpinDirNode[DIR_SIZE];

            for (int dirPos = 0; dirPos < DIR_SIZE; dirPos++) {
                spinDirNodeArr[dirPos] = new SpinDirNode();
            }
            return spinDirNodeArr;
        }
    }

    private static class PsiNode {
        /**
         * Pos of actual calculated direction.
         */
        final SpinDirNode[] spinDirNodeArr = SpinDirNode.createSpinDirNodeArr();

        public static PsiNode[] createDivNodeArr() {
            final PsiNode[] psiNodeArr = new PsiNode[PsiArrSize];

            for (int psiArrPos = 0; psiArrPos < PsiArrSize; psiArrPos++) {
                psiNodeArr[psiArrPos] = new PsiNode();
            }
            return psiNodeArr;
        }
    }

    private class Layer {
        final PsiNode[] psiNodeArr = PsiNode.createDivNodeArr();
    }

    private final Layer[] psiLayerArr;
    private int psiPos = 0;

    public static void main(String[] args) {
        final JFrame frame = new JFrame("Node Layer Impulse Electron Simulation");

        final Field2LayerImpulseElectronSimulation simulation = new Field2LayerImpulseElectronSimulation();

        frame.add(simulation);
        frame.setSize(VIEW_WIDTH, VIEW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        new Thread(simulation::simulate).start();
    }

    public Field2LayerImpulseElectronSimulation() {
        this.psiLayerArr = new Layer[PSI_LAYER_SIZE];
        for (int layerPos = 0; layerPos < PSI_LAYER_SIZE; layerPos++) {
            this.psiLayerArr[layerPos] = new Layer();
        }

        {
            // to right, very fast:
            final int nextArrPos = ((PsiArrSize / 4) * 1);
            final Node node = retrieveNode(this.psiLayerArr, this.psiPos, nextArrPos,
                    1, 0, 0,
                    0, 0, 1, MAX_SPEED_C - ((MAX_SPEED_C / 4)));
            node.count = intPow2(MAX_DIV);
            //node.count = 0;//intPow2(MAX_DIV);
            node.sourceEvent = new SourceEvent();
        }
        {
            // stay in middle:
            final int nextArrPos = ((PsiArrSize / 4) * 2);
            final Node node = retrieveNode(this.psiLayerArr, this.psiPos, nextArrPos,
                    5, 0, 0,
                    0, 0, 1, 0);
            node.count = intPow2(MAX_DIV);
            //node.count = 0;//intPow2(MAX_DIV);
            node.sourceEvent = new SourceEvent();
        }
        {
            // to left, slowly:
            final int nextArrPos = ((PsiArrSize / 4) * 3);
            final Node node = retrieveNode(this.psiLayerArr, this.psiPos, nextArrPos,
                    1, 0, 1,
                    0, 0, 0, MAX_SPEED_C - ((MAX_SPEED_C / 4) * 3));
            node.count = intPow2(MAX_DIV);
            //node.count = 0;//intPow2(MAX_DIV);
            node.sourceEvent = new SourceEvent();
        }
    }

    public void simulate() {
        System.out.println("START: Simulating...");

        for (int t = 0; t < TIMESTEPS * 1000; t++) {
            final int actPsiPos = this.psiPos;
            final int nextPsiPos = (actPsiPos + 1) % 2;

            for (int psiArrPos = 0; psiArrPos < PsiArrSize; psiArrPos++) {
                final PsiNode nextPsiNode = this.psiLayerArr[nextPsiPos].psiNodeArr[psiArrPos];
                for (int spinDirPos = 0; spinDirPos < DIR_SIZE; spinDirPos++) {
                    final SpinDirNode nextSpinDirNode = nextPsiNode.spinDirNodeArr[spinDirPos];
                    for (int spinPos = 0; spinPos < MAX_SPIN; spinPos++) {
                        for (int spinCntPos = 0; spinCntPos < MAX_SPIN; spinCntPos++) {
                            final SpinCntNode nextSpinCntNode = nextSpinDirNode.spinNodeArr[spinPos].spinCntNodeArr[spinCntPos];
                            for (int spinSpeedDirPos = 0; spinSpeedDirPos < DIR_SIZE; spinSpeedDirPos++) {
                                for (int speedCntPos = 0; speedCntPos <= MAX_SPEED_C; speedCntPos++) {
                                    for (int speedDirPos = 0; speedDirPos < DIR_SIZE; speedDirPos++) {
                                        for (int speedPos = 0; speedPos <= MAX_SPEED_C; speedPos++) {
                                            final Node node = nextSpinCntNode.
                                                    spinSpeedDirNodeArr[spinSpeedDirPos].speedCntNodeArr[speedCntPos].dirSpeedNodeArr[speedDirPos][speedPos];
                                            node.count = 0;
                                            node.eFieldCount = 0;
                                            node.sourceEvent = null;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            for (int psiArrPos = 0; psiArrPos < PsiArrSize; psiArrPos++) {
                final PsiNode actPsiNode = this.psiLayerArr[this.psiPos].psiNodeArr[psiArrPos];
                for (int spinDirPos = 0; spinDirPos < DIR_SIZE; spinDirPos++) {
                    final SpinDirNode actSpinDirNode = actPsiNode.spinDirNodeArr[spinDirPos];
                    long nodeCount = 0;
                    for (int spinPos = 0; spinPos < MAX_SPIN; spinPos++) {
                        for (int spinCntPos = 0; spinCntPos < MAX_SPIN; spinCntPos++) {
                            final SpinCntNode actSpinCntNode = actSpinDirNode.spinNodeArr[spinPos].spinCntNodeArr[spinCntPos];
                            for (int spinSpeedDirPos = 0; spinSpeedDirPos < DIR_SIZE; spinSpeedDirPos++) {
                                for (int speedCntPos = 0; speedCntPos <= MAX_SPEED_C; speedCntPos++) {
                                    for (int speedDirPos = 0; speedDirPos < DIR_SIZE; speedDirPos++) {
                                        for (int speedPos = 0; speedPos <= MAX_SPEED_C; speedPos++) {
                                            final Node actNode = actSpinCntNode.
                                                    spinSpeedDirNodeArr[spinSpeedDirPos].speedCntNodeArr[speedCntPos].dirSpeedNodeArr[speedDirPos][speedPos];

                                            final int actSpinSpeedDirPos = spinSpeedDirPos;
                                            final int actSpeedDirPos = speedDirPos;
                                            final int actSpeedCntPos = speedCntPos;
                                            final int actSpeedPos = speedPos;

                                            if (actNode.count > 0) {
                                                final int actSpinDirPos = spinDirPos;
                                                final int nextSpinDirPos;
                                                final int actSpinCntPos = spinCntPos;
                                                final int nextSpinCntPos = (actSpinCntPos + 1) % spinPos;

                                                final int nextSpinSpeedDirPos = (actSpinSpeedDirPos + 1) % DIR_SIZE;
                                                final int nextSpeedDirPos = actSpeedDirPos;
                                                final int nextSpeedPos = actSpeedPos;
                                                final int nextSpeedCntPos;

                                                if (actSpinCntPos == 0) {
                                                    nextSpinDirPos = (actSpinDirPos + 1) % DIR_SIZE;
                                                } else {
                                                    nextSpinDirPos = actSpinDirPos;
                                                }

                                                final int calcSpeedCntPos = actSpeedCntPos + actSpeedPos;

                                                if (calcSpeedCntPos >= MAX_SPEED_C) {
                                                    nextSpeedCntPos = calcSpeedCntPos - MAX_SPEED_C;
                                                    final int moveDir = actSpeedDirPos == 0 ? -1 : 1;

                                                    // Use nextNode for the next actNode in the direction of the speed.
                                                    final int nextPsiArrPos = (psiArrPos + moveDir + PsiArrSize) % PsiArrSize;
                                                    calcNextNode(actNode, this.psiLayerArr, nextPsiPos, nextPsiArrPos,
                                                            spinPos, nextSpinCntPos, nextSpinDirPos,
                                                            nextSpinSpeedDirPos, nextSpeedCntPos, nextSpeedDirPos, nextSpeedPos);
                                                } else {
                                                    nextSpeedCntPos = calcSpeedCntPos;
                                                    final int moveDir = actSpinDirPos == 0 ? -1 : 1;

                                                    final long hNodeCount = actNode.count / 2;
                                                    final long lhNodeCount = actNode.count % 2;

                                                    // If reaching MAX_DIV: Use nextNode for the next actNode to stay in position.
                                                    //final int nextDivPos = divPos + 1;
                                                    //if (nextDivPos < MAX_DIV) {
                                                    if (hNodeCount > 0) {
                                                        calcNextNode2(actNode, this.psiLayerArr, nextPsiPos, psiArrPos,
                                                                spinPos, nextSpinCntPos, nextSpinDirPos,
                                                                nextSpinSpeedDirPos, nextSpeedCntPos, nextSpeedDirPos, nextSpeedPos,
                                                                hNodeCount + lhNodeCount);

                                                        final int nextPsiArrPos = (psiArrPos + moveDir + PsiArrSize) % PsiArrSize;
                                                        calcNextNode2(actNode, this.psiLayerArr, nextPsiPos, nextPsiArrPos,
                                                                spinPos, nextSpinCntPos, nextSpinDirPos,
                                                                nextSpinSpeedDirPos, nextSpeedCntPos, nextSpeedDirPos, nextSpeedPos,
                                                                hNodeCount);
                                                    } else {
                                                        calcNextNode(actNode, this.psiLayerArr, nextPsiPos, psiArrPos,
                                                                spinPos, nextSpinCntPos, nextSpinDirPos,
                                                                nextSpinSpeedDirPos, nextSpeedCntPos, nextSpeedDirPos, nextSpeedPos);
                                                    }
                                                }
                                                nodeCount += actNode.count;
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
                    Thread.sleep(25*3);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("END: Simulating.");
    }

    private static void calcNextNode(Node sourceNode, Layer[] psiLayerArr, int nextPsiPos, int nextPsiArrPos,
                                     int nextSpinPos, int nextSpinCntPos, int nextSpinDirPos,
                                     int nextSpinSpeedDirPos, int nextSpeedCntPos, int nextSpeedDirPos, int nextSpeedPos) {
        final Node nextNode = retrieveNode(psiLayerArr, nextPsiPos, nextPsiArrPos,
                nextSpinPos, nextSpinCntPos, nextSpinDirPos,
                nextSpinSpeedDirPos, nextSpeedCntPos, nextSpeedDirPos, nextSpeedPos);

        calcNextNodeState(nextNode,
                sourceNode.count,
                sourceNode.sourceEvent);

        //final int nFieldDir = (nextSpeedDirPos == 0 ? -1 : 1);
        for (int nFieldDir = -1; nFieldDir <= 1; nFieldDir++) {
            final int nPsiArrPos = (nextPsiArrPos + nFieldDir + PsiArrSize) % PsiArrSize;

            final Node nextEFieldNode = retrieveNode(psiLayerArr, nextPsiPos, nPsiArrPos,
                    0, 0, 0,
                    nextSpinSpeedDirPos, 0, nextSpeedDirPos, nextSpeedPos);

            nextEFieldNode.eFieldCount += sourceNode.count;
        }
    }

    private static void calcNextNode2(Node sourceNode, Layer[] psiLayerArr, int nextPsiPos, int nextPsiArrPos,
                                      int nextSpinPos, int nextSpinCntPos, int nextSpinDirPos,
                                      int nextSpinSpeedDirPos, int nextSpeedCntPos, int nextSpeedDirPos, int nextSpeedPos,
                                      long nextNodeCount) {
        final Node nextNode = retrieveNode(psiLayerArr, nextPsiPos, nextPsiArrPos,
                nextSpinPos, nextSpinCntPos, nextSpinDirPos,
                nextSpinSpeedDirPos, nextSpeedCntPos, nextSpeedDirPos, nextSpeedPos);

        calcNextNodeState2(nextNode,
                nextNodeCount,
                sourceNode.sourceEvent);

        //final int nFieldDir = (nextSpeedDirPos == 0 ? -1 : 1);
        for (int nFieldDir = -1; nFieldDir <= 1; nFieldDir++) {
            final int nPsiArrPos = (nextPsiArrPos + nFieldDir + PsiArrSize) % PsiArrSize;

            final Node nextEFieldNode = retrieveNode(psiLayerArr, nextPsiPos, nPsiArrPos,
                    0, 0, 0,
                    nextSpinSpeedDirPos, 0, nextSpeedDirPos, nextSpeedPos);

            nextEFieldNode.eFieldCount += nextNodeCount;
        }
    }

    private static Node retrieveNode(Layer[] psiLayerArr, int psiPos, int psiArrPos,
                                     int spinPos, int spinCntPos, int spinDirPos,
                                     int spinSpeedDirPos, int speedCntPos, int speedDirPos, int speedPos) {
        return psiLayerArr[psiPos].psiNodeArr[psiArrPos].spinDirNodeArr[spinDirPos].spinNodeArr[spinPos].spinCntNodeArr[spinCntPos].
                spinSpeedDirNodeArr[spinSpeedDirPos].speedCntNodeArr[speedCntPos].dirSpeedNodeArr[speedDirPos][speedPos];
    }

    private static SpinDirNode retrieveSpinDirNode(Layer[] psiLayerArr, int psiPos, int psiArrPos,
                                                   int spinDirPos) {
        return psiLayerArr[psiPos].psiNodeArr[psiArrPos].spinDirNodeArr[spinDirPos];
    }

    private static void calcNextNodeState(final Node nextNode,
                                          final long count,
                                          final SourceEvent sourceEvent) {
        if ((Integer.MAX_VALUE - count) >= nextNode.count) {
            nextNode.count += count;
        } else {
            throw new RuntimeException("Das Ergebnis von nextNode.count %d + count %d würde den Wertebereich überschreiten".formatted(nextNode.count, count));
        }
        if (Objects.nonNull(nextNode.sourceEvent) && (nextNode.sourceEvent != sourceEvent)) {
            throw new RuntimeException("nextNode.sourceEvent != sourceEvent");
        }
        nextNode.sourceEvent = sourceEvent;
    }

    private static void calcNextNodeState2(Node nextNode,
                                           long count,
                                           SourceEvent sourceEvent) {
        if (Integer.MAX_VALUE - count >= nextNode.count) {
            nextNode.count += count;
        } else {
            throw new RuntimeException("Das Ergebnis von nextNode.count %d + count %d würde den Wertebereich überschreiten".formatted(nextNode.count, count));
        }
        if (Objects.nonNull(nextNode.sourceEvent) && (nextNode.sourceEvent != sourceEvent)) {
            throw new RuntimeException("nextNode.sourceEvent != sourceEvent");
        }
        nextNode.sourceEvent = sourceEvent;
    }

    private static final Color BLUE_COLOR = new Color(80, 80, 255, 255);
    private static final Color DARK_BLUE_COLOR = new Color(125, 0, 155, 255);

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int nextPsiPos = (this.psiPos + 1) % 2;

        int partX = VIEW_WIDTH / 4;

        g.setColor(Color.GRAY);
        g.drawLine(0, VIEW_EXTRA_HEIGHT * 4, VIEW_WIDTH, VIEW_EXTRA_HEIGHT * 4);
        for (int part = 1; part < 4; part++) {
            g.drawLine(partX * part, 0, partX * part, VIEW_HEIGHT);
        }

        for (int psiArrPos = 0; psiArrPos < PsiArrSize - 1; psiArrPos++) {
            int xp1 = (psiArrPos + PsiArrSize) % PsiArrSize;
            int xp2 = (psiArrPos + 1 + PsiArrSize) % PsiArrSize;
            int x1 = (int) (xp1 * (VIEW_WIDTH / (double) PsiArrSize));
            int x2 = (int) ((xp2) * (VIEW_WIDTH / (double) PsiArrSize));

            double[] ye1 = new double[DIR_SIZE];
            double[] ye2 = new double[DIR_SIZE];
            long ys1 = 0;
            long ys2 = 0;
            long yc1 = 0;
            long yc2 = 0;
            double yp1 = 0.0D;
            double yp2 = 0.0D;
            for (int spinDirPos = 0; spinDirPos < DIR_SIZE; spinDirPos++) {
                final SpinDirNode spinDirNode1 = psiLayerArr[nextPsiPos].psiNodeArr[xp1].spinDirNodeArr[spinDirPos];
                final SpinDirNode spinDirNode2 = psiLayerArr[nextPsiPos].psiNodeArr[xp2].spinDirNodeArr[spinDirPos];
                for (int spinPos = 0; spinPos < MAX_SPIN; spinPos++) {
                    for (int spinCntPos = 0; spinCntPos < MAX_SPIN; spinCntPos++) {
                        final SpinCntNode spinCntNode1 = spinDirNode1.spinNodeArr[spinPos].spinCntNodeArr[spinCntPos];
                        final SpinCntNode spinCntNode2 = spinDirNode2.spinNodeArr[spinPos].spinCntNodeArr[spinCntPos];
                        for (int spinSpeedDirPos = 0; spinSpeedDirPos < DIR_SIZE; spinSpeedDirPos++) {
                            for (int speedCntPos = 0; speedCntPos <= MAX_SPEED_C; speedCntPos++) {
                                for (int speedDirPos = 0; speedDirPos < DIR_SIZE; speedDirPos++) {
                                    for (int speedPos = 0; speedPos <= MAX_SPEED_C; speedPos++) {
                                        final Node node1 = spinCntNode1.
                                                //divNodeArr[divPos].
                                                spinSpeedDirNodeArr[spinSpeedDirPos].speedCntNodeArr[speedCntPos].dirSpeedNodeArr[speedDirPos][speedPos];
                                        final Node node2 = spinCntNode2.
                                                //divNodeArr[divPos].
                                                spinSpeedDirNodeArr[spinSpeedDirPos].speedCntNodeArr[speedCntPos].dirSpeedNodeArr[speedDirPos][speedPos];

                                        final int divPos = MAX_DIV - 1;
                                        yp1 += calcNodePobability(node1, divPos + 1);
                                        yp2 += calcNodePobability(node2, divPos + 1);
                                        if (node1.count > 0) {
                                            yc1 += node1.count;

                                            ys1 += spinCntPos;

                                            {
                                                // Node count:
                                                double yd1 = 0.0D;
                                                double yd2 = (node1.count) / Math.pow(2.0D, MAX_DIV - 1);
                                                int y1 = (int) (VIEW_HEIGHT * yd1 / 10.0D);
                                                int y2 = (int) (VIEW_HEIGHT * yd2 / 10.0D);
                                                g.setColor(Color.RED);
                                                g.drawLine(x1, VIEW_EXTRA_HEIGHT * 2 - y2, x2, VIEW_EXTRA_HEIGHT * 2 - y2);
                                            }
                                        }
                                        ye1[speedDirPos] += node1.eFieldCount;

                                        if (node2.count > 0) {
                                            yc2 += node2.count;
                                            ys2 += spinCntPos;
                                        }
                                        ye2[speedDirPos] += node2.eFieldCount;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            {
                int y1 = (int) (VIEW_HEIGHT * yp1);
                int y2 = (int) (VIEW_HEIGHT * yp2);
                g.setColor(Color.BLACK);
                g.drawLine(x1, VIEW_EXTRA_HEIGHT * 4 - y1, x2, VIEW_EXTRA_HEIGHT * 4 - y2);
            }
            {
                // Node Count sum:
                int y1 = (int) ((VIEW_EXTRA_HEIGHT * yc1) / Math.pow(2.0D, MAX_DIV - 1));
                int y2 = (int) ((VIEW_EXTRA_HEIGHT * yc2) / Math.pow(2.0D, MAX_DIV - 1));
                g.setColor(Color.GREEN);
                g.drawLine(x1, VIEW_EXTRA_HEIGHT * 2 - y1, x2, VIEW_EXTRA_HEIGHT * 2 - y2);
            }
            {
                int y1 = (int) ((VIEW_EXTRA_HEIGHT * ys1) / MAX_SPIN);
                int y2 = (int) ((VIEW_EXTRA_HEIGHT * ys2) / MAX_SPIN);
                g.setColor(Color.ORANGE);
                g.drawLine(x1, VIEW_EXTRA_HEIGHT * 2 - y1, x2, VIEW_EXTRA_HEIGHT * 2 - y2);
            }
            {
                // e-Field sum:
                double lastYe1 = 0.0D;
                double lastYe2 = 0.0D;
                for (int dirPos = 0; dirPos < DIR_SIZE; dirPos++) {
                    lastYe1 = ye1[dirPos] + lastYe1;
                    lastYe2 = ye2[dirPos] + lastYe2;
                    int y1 = (int) (VIEW_EXTRA_HEIGHT * lastYe1) / intPow2(MAX_DIV - 1) + dirPos;
                    int y2 = (int) (VIEW_EXTRA_HEIGHT * lastYe2) / intPow2(MAX_DIV - 1) + dirPos;
                    if (dirPos == 0) {
                        g.setColor(DARK_BLUE_COLOR);
                    } else {
                        g.setColor(BLUE_COLOR);
                    }
                    g.drawLine(x1, VIEW_EXTRA_HEIGHT * 6 - y1, x2, VIEW_EXTRA_HEIGHT * 6 - y2);
                }
            }
        }
    }

    private static double calcNodePobability(final Node node, final int div) {
        final double retPobability;
        if (node.count == 0) {
            retPobability = 0.0D;
        } else {
            retPobability = node.count * (1.0D / (mathPowChecked(2.0D, div)));
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

    private static int intPow2(final int exponent) {
        return 1 << exponent;
    }
}
