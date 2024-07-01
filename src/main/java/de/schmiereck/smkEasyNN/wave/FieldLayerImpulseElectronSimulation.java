package de.schmiereck.smkEasyNN.wave;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * https://chatgpt.com/c/20509527-0f81-4457-b7a7-d869fc25e129
 * elektrische Potential eines Elektrons:
 * https://chatgpt.com/c/2386af98-aa80-4bb0-8dce-c623e8645dfb
 */
public class FieldLayerImpulseElectronSimulation extends JPanel {
    private static final int VIEW_WIDTH = 1400;
    private static final int VIEW_HEIGHT = 600;
    private static final int VIEW_EXTRA_HEIGHT = VIEW_HEIGHT / 8;

    private static final double DT = 0.00000001;
    private static final int TIMESTEPS = (int) (1.0D / DT);

    private static final int PsiArrSize = 100;//200;

    private final static int DIR_SIZE = 2;
    private final static int MAX_DIV = 64/4;
    private final static int MAX_SPEED_C = 8;//12;
    private final static int PSI_LAYER_SIZE = 2;
    private final static int MAX_SPIN = 6;
    private final static int TYPE_SIZE = 2;

    private static class SourceEvent {
        SourceEvent parentSourceEvent = null;
    }

    // TODO Field als Typ hinzufügen mit eigenem Count.
    // Die Speed wird als Impuls interpretiert.
    // Felder haben auch einen Spin.

    private static class Node {
        long count = 0;
        long eFieldCount;
        SourceEvent sourceEvent = null;

        //long[] speedArr = new long[DIR_SIZE];
        //long speed = 0;
        //long[] speedCntArr = new long[DIR_SIZE];

        //int spin;
        //int spinCnt = 0;

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

    private static class DivNode {
        final SpinSpeedDirNode[] spinSpeedDirNodeArr = SpinSpeedDirNode.createSpinSpeedDirNodeArr();

        public static DivNode[] createDivNodeArr() {
            final DivNode[] divNodeArr = new DivNode[MAX_DIV];

            for (int divPos = 0; divPos < MAX_DIV; divPos++) {
                divNodeArr[divPos] = new DivNode();
            }
            return divNodeArr;
        }
    }

    private static class SpinDirNode {
        final DivNode[] divNodeArr = DivNode.createDivNodeArr();

        public static SpinDirNode[] createSpinDirNodeArr() {
            final SpinDirNode[] spinDirNodeArr = new SpinDirNode[DIR_SIZE];

            for (int dirPos = 0; dirPos < DIR_SIZE; dirPos++) {
                spinDirNodeArr[dirPos] = new SpinDirNode();
            }
            return spinDirNodeArr;
        }
    }

    private static class SpinCntNode {
        /**
         * Pos of actual calculated direction.
         */
        final SpinDirNode[] spinDirNodeArr = SpinDirNode.createSpinDirNodeArr();

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

    private static class PsiNode {
        final SpinNode[] spinNodeArr = SpinNode.createSpinNodeArr();

        /**
         * Count of divisions.
         * div = div/2, div = div/2, div = div/2, ...
         * Position 0 is div = 1.
         */
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

    private static class RealityNode {
        int eQuant = 0;
        double[] eFieldArray = new double[DIR_SIZE];
    }

    private final RealityNode[] realityNodeArr;


    public static void main(String[] args) {
        final JFrame frame = new JFrame("Node Layer Impulse Electron Simulation");

        final FieldLayerImpulseElectronSimulation simulation = new FieldLayerImpulseElectronSimulation();

        frame.add(simulation);
        frame.setSize(VIEW_WIDTH, VIEW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        new Thread(simulation::simulate).start();
    }

    public FieldLayerImpulseElectronSimulation() {
        this.psiLayerArr = new Layer[PSI_LAYER_SIZE];
        for (int layerPos = 0; layerPos < PSI_LAYER_SIZE; layerPos++) {
            this.psiLayerArr[layerPos] = new Layer();
        }

        this.realityNodeArr = new RealityNode[PsiArrSize];
        for (int pos = 0; pos < PsiArrSize; pos++) {
            this.realityNodeArr[pos] = new RealityNode();
        }

        {
            // to right, very fast:
            final int nextArrPos = ((PsiArrSize / 4) * 1);
            final Node node = retrieveNode(this.psiLayerArr, this.psiPos, nextArrPos,
                    1, 0, 0,
                    0,
                    0, 0, 1, MAX_SPEED_C - ((MAX_SPEED_C / 4)));
            node.count = 0;
            node.sourceEvent = new SourceEvent();
        }
        {
            // stay in middle:
            final int nextArrPos = ((PsiArrSize / 4) * 2);
            final Node node = retrieveNode(this.psiLayerArr, this.psiPos, nextArrPos,
                    3, 0, 0,
                    0,
                    0, 0, 1, 0);
            node.count = 1;
            node.sourceEvent = new SourceEvent();
        }
        {
            // to left, slowly:
            final int nextArrPos = ((PsiArrSize / 4) * 3);
            final Node node = retrieveNode(this.psiLayerArr, this.psiPos, nextArrPos,
                    1, 0, 1,
                    0,
                    0, 0, 0, MAX_SPEED_C - ((MAX_SPEED_C / 4) * 3));
            node.count = 0;
            node.sourceEvent = new SourceEvent();
        }
    }

    public void simulate() {
        System.out.println("START: Simulating...");

        for (int t = 0; t < TIMESTEPS * 1000; t++) {
            final int actPsiPos = this.psiPos;
            final int nextPsiPos = (actPsiPos + 1) % 2;

            for (int psiArrPos = 0; psiArrPos < PsiArrSize; psiArrPos++) {
                final PsiNode psiNode = this.psiLayerArr[actPsiPos].psiNodeArr[psiArrPos];
                this.realityNodeArr[psiArrPos].eQuant = 0;
                this.realityNodeArr[psiArrPos].eFieldArray[0] = 0.0D;
                this.realityNodeArr[psiArrPos].eFieldArray[1] = 0.0D;
                for (int spinPos = 0; spinPos < MAX_SPIN; spinPos++) {
                    for (int spinCntPos = 0; spinCntPos < MAX_SPIN; spinCntPos++) {
                        for (int spinDirPos = 0; spinDirPos < DIR_SIZE; spinDirPos++) {
                            for (int divPos = MAX_DIV - 1; divPos >= 0; divPos--) {
                                for (int spinSpeedDirPos = 0; spinSpeedDirPos < DIR_SIZE; spinSpeedDirPos++) {
                                    for (int speedCntPos = 0; speedCntPos <= MAX_SPEED_C; speedCntPos++) {
                                        for (int speedDirPos = 0; speedDirPos < DIR_SIZE; speedDirPos++) {
                                            for (int speedPos = 0; speedPos <= MAX_SPEED_C; speedPos++) {
                                                final Node node = psiNode.spinNodeArr[spinPos].spinCntNodeArr[spinCntPos].spinDirNodeArr[spinDirPos].
                                                        divNodeArr[divPos].spinSpeedDirNodeArr[spinSpeedDirPos].speedCntNodeArr[speedCntPos].dirSpeedNodeArr[speedDirPos][speedPos];

                                                // Renorm div counts to lower divisions.
                                                if ((node.count > 1) && (divPos > 0)) {
                                                    final long upperNodeCount = node.count / 2;
                                                    final long letNodeCount = node.count % 2;

                                                    calcNextNode2(node, this.psiLayerArr, actPsiPos, psiArrPos,
                                                            spinPos, spinCntPos, spinDirPos,
                                                            divPos - 1,
                                                            spinSpeedDirPos, speedCntPos, speedDirPos, speedPos,
                                                            upperNodeCount);
                                                    node.count = letNodeCount;
                                                    this.realityNodeArr[psiArrPos].eQuant += letNodeCount;
                                                }
                                                if (node.eFieldCount > 1) {
                                                    final long upperNodeCount = node.eFieldCount / 2;
                                                    final long letNodeCount = node.eFieldCount % 2;

                                                    final Node upperNode = retrieveNode(this.psiLayerArr, actPsiPos, psiArrPos,
                                                            spinPos, spinCntPos, spinDirPos,
                                                            divPos,
                                                            spinSpeedDirPos, speedCntPos, speedDirPos, speedPos);

                                                    upperNode.eFieldCount += upperNodeCount;
                                                    node.eFieldCount = letNodeCount;
                                                }
                                                if (node.eFieldCount > 0) {
                                                    final double eField = (node.eFieldCount * (1.0D / (Math.pow(2.0D, divPos + 1))));
                                                    this.realityNodeArr[psiArrPos].eFieldArray[speedDirPos] += eField;
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
            for (int psiArrPos = 0; psiArrPos < PsiArrSize; psiArrPos++) {
                final PsiNode psiNode = this.psiLayerArr[nextPsiPos].psiNodeArr[psiArrPos];
                for (int spinPos = 0; spinPos < MAX_SPIN; spinPos++) {
                    for (int spinCntPos = 0; spinCntPos < MAX_SPIN; spinCntPos++) {
                        for (int spinDirPos = 0; spinDirPos < DIR_SIZE; spinDirPos++) {
                            for (int divPos = 0; divPos < MAX_DIV; divPos++) {
                                for (int spinSpeedDirPos = 0; spinSpeedDirPos < DIR_SIZE; spinSpeedDirPos++) {
                                    for (int speedCntPos = 0; speedCntPos <= MAX_SPEED_C; speedCntPos++) {
                                        for (int speedDirPos = 0; speedDirPos < DIR_SIZE; speedDirPos++) {
                                            for (int speedPos = 0; speedPos <= MAX_SPEED_C; speedPos++) {
                                                final Node node = psiNode.spinNodeArr[spinPos].spinCntNodeArr[spinCntPos].spinDirNodeArr[spinDirPos].
                                                        divNodeArr[divPos].spinSpeedDirNodeArr[spinSpeedDirPos].speedCntNodeArr[speedCntPos].dirSpeedNodeArr[speedDirPos][speedPos];
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
            }
            for (int psiArrPos = 0; psiArrPos < PsiArrSize; psiArrPos++) {
                final PsiNode psiNode = this.psiLayerArr[this.psiPos].psiNodeArr[psiArrPos];
                for (int spinPos = 0; spinPos < MAX_SPIN; spinPos++) {
                    for (int spinCntPos = 0; spinCntPos < MAX_SPIN; spinCntPos++) {
                        for (int spinDirPos = 0; spinDirPos < DIR_SIZE; spinDirPos++) {
                            for (int divPos = 0; divPos < MAX_DIV; divPos++) {
                                for (int spinSpeedDirPos = 0; spinSpeedDirPos < DIR_SIZE; spinSpeedDirPos++) {
                                    for (int speedCntPos = 0; speedCntPos <= MAX_SPEED_C; speedCntPos++) {
                                        for (int speedDirPos = 0; speedDirPos < DIR_SIZE; speedDirPos++) {
                                            for (int speedPos = 0; speedPos <= MAX_SPEED_C; speedPos++) {
                                                final Node node = psiNode.spinNodeArr[spinPos].spinCntNodeArr[spinCntPos].spinDirNodeArr[spinDirPos].
                                                        divNodeArr[divPos].spinSpeedDirNodeArr[spinSpeedDirPos].speedCntNodeArr[speedCntPos].dirSpeedNodeArr[speedDirPos][speedPos];

                                                final int actDivPos = divPos;
                                                final int actSpinSpeedDirPos = spinSpeedDirPos;
                                                final int actSpeedDirPos = speedDirPos;
                                                final int actSpeedCntPos = speedCntPos;
                                                final int actSpeedPos = speedPos;

                                                if (node.count > 0) {
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

                                                        // Use nextNode for the next node in the direction of the speed.
                                                        final int nextPsiArrPos = (psiArrPos + moveDir + PsiArrSize) % PsiArrSize;
                                                        calcNextNode(node, this.psiLayerArr, nextPsiPos, nextPsiArrPos,
                                                                spinPos, nextSpinCntPos, nextSpinDirPos,
                                                                divPos,
                                                                nextSpinSpeedDirPos, nextSpeedCntPos, nextSpeedDirPos, nextSpeedPos,
                                                                actSpeedDirPos, nextSpeedCntPos);
                                                    } else {
                                                        nextSpeedCntPos = calcSpeedCntPos;
                                                        final int moveDir = actSpinDirPos == 0 ? -1 : 1;

                                                        // If reaching MAX_DIV: Use nextNode for the next node to stay in position.
                                                        final int nextDivPos = divPos + 1;
                                                        if (nextDivPos < MAX_DIV) {
                                                            calcNextNode(node, this.psiLayerArr, nextPsiPos, psiArrPos,
                                                                    spinPos, nextSpinCntPos, nextSpinDirPos,
                                                                    nextDivPos,
                                                                    nextSpinSpeedDirPos, nextSpeedCntPos, nextSpeedDirPos, nextSpeedPos,
                                                                    actSpeedDirPos, nextSpeedCntPos);

                                                            final int nextPsiArrPos = (psiArrPos + moveDir + PsiArrSize) % PsiArrSize;
                                                            calcNextNode(node, this.psiLayerArr, nextPsiPos, nextPsiArrPos,
                                                                    spinPos, nextSpinCntPos, nextSpinDirPos,
                                                                    nextDivPos,
                                                                    nextSpinSpeedDirPos, nextSpeedCntPos, nextSpeedDirPos, nextSpeedPos,
                                                                    actSpeedDirPos, nextSpeedCntPos);
                                                        } else {
                                                            calcNextNode(node, this.psiLayerArr, nextPsiPos, psiArrPos,
                                                                    spinPos, nextSpinCntPos, nextSpinDirPos,
                                                                    divPos,
                                                                    nextSpinSpeedDirPos, nextSpeedCntPos, nextSpeedDirPos, nextSpeedPos,
                                                                    actSpeedDirPos, nextSpeedCntPos);
                                                        }
                                                    }
                                                    {
                                                        // Wenn das Feld-Potential an dieser Stelle niedriger als 1 ist, dann hochsetzen (in beide Richtungen).
                                                        for (int dirPos = 0; dirPos < DIR_SIZE; dirPos++) {
                                                            final double eField = Math.min(1.0D, this.realityNodeArr[psiArrPos].eFieldArray[dirPos]);
                                                            final double eFieldDiff = 1.0D - eField;
                                                            if (eFieldDiff > 0.0D) {
                                                                int fieldDivPos = (int) (MAX_DIV - (Math.sqrt(eFieldDiff) * (MAX_DIV - 1)));
                                                                int nextFieldDivPos = fieldDivPos;//(fieldDivPos * divPos) / MAX_DIV;
                                                                int fieldSpinSpeedDirPos = dirPos;
                                                                int fieldSpeedCntPos = 0;
                                                                int fieldSpeedDirPos = dirPos;
                                                                int fieldSpeedPos = MAX_SPEED_C - 1;
                                                                final Node fieldNode = retrieveNode(this.psiLayerArr, nextPsiPos, psiArrPos,
                                                                        spinPos, spinCntPos, spinDirPos,
                                                                        nextFieldDivPos,
                                                                        fieldSpinSpeedDirPos, fieldSpeedCntPos, fieldSpeedDirPos, fieldSpeedPos);
                                                                fieldNode.eFieldCount += 1;
                                                            } else {
                                                                final Node fieldNode = retrieveNode(this.psiLayerArr, nextPsiPos, psiArrPos,
                                                                        spinPos, spinCntPos, spinDirPos,
                                                                        actDivPos,
                                                                        actSpinSpeedDirPos, actSpeedCntPos, actSpeedDirPos, actSpeedPos);
                                                                fieldNode.eFieldCount += node.eFieldCount;
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    // Wenn das Feld in Richtung des Feldes niedriger höher als 1/2 des eigenes Feldes,
                                                    // dann Node in Richtung des Feldes auf 1/2 hochsetzen (und im anderen Node abziehen).
                                                    for (int dirPos = 0; dirPos < DIR_SIZE; dirPos++) {
                                                        final int fieldDir = dirPos == 0 ? -1 : 1;
                                                        final double eField = Math.min(1.0D, this.realityNodeArr[psiArrPos].eFieldArray[dirPos]);
                                                        final int nPos = (psiArrPos + fieldDir + PsiArrSize) % PsiArrSize;
                                                        final double neField = Math.min(1.0D, this.realityNodeArr[nPos].eFieldArray[dirPos]);
                                                        final double eFieldDiff = neField - eField;
                                                        if (eFieldDiff > 0.0D) {
                                                            final double nextEFieldDiff = eFieldDiff / 2.0D;
                                                            int fieldDivPos = (int) (MAX_DIV - (Math.sqrt(eField + nextEFieldDiff) * (MAX_DIV - 1)));
                                                            int nextFieldDivPos = fieldDivPos;//(fieldDivPos * divPos) / MAX_DIV;
                                                            int fieldSpinSpeedDirPos = dirPos;
                                                            int fieldSpeedCntPos = 0;
                                                            int fieldSpeedDirPos = dirPos;
                                                            int fieldSpeedPos = MAX_SPEED_C - 1;
                                                            final Node fieldNode = retrieveNode(this.psiLayerArr, nextPsiPos, psiArrPos,
                                                                    spinPos, spinCntPos, spinDirPos,
                                                                    nextFieldDivPos,
                                                                    fieldSpinSpeedDirPos, fieldSpeedCntPos, fieldSpeedDirPos, fieldSpeedPos);
                                                            fieldNode.eFieldCount += 1;
                                                        } else {
                                                            final Node fieldNode = retrieveNode(this.psiLayerArr, nextPsiPos, psiArrPos,
                                                                    spinPos, spinCntPos, spinDirPos,
                                                                    actDivPos,
                                                                    actSpinSpeedDirPos, actSpeedCntPos, actSpeedDirPos, actSpeedPos);
                                                            fieldNode.eFieldCount += node.eFieldCount;
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
                }
            }

            this.psiPos = nextPsiPos;

            if (t % 1 == 0) {
                this.repaint();
                try {
                    Thread.sleep(25*15);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("END: Simulating.");
    }

    private static void calcNextNode(Node sourceNode, Layer[] psiLayerArr, int nextPsiPos, int nextPsiArrPos,
                                     int nextSpinPos, int nextSpinCntPos, int nextSpinDirPos,
                                     int nextDivPos,
                                     int nextSpinSpeedDirPos, int nextSpeedCntPos, int nextSpeedDirPos, int nextSpeedPos,
                                     int actSpeedDirPos, long nextSpeedCntPos2) {
        final Node nextNode = retrieveNode(psiLayerArr, nextPsiPos, nextPsiArrPos,
                nextSpinPos, nextSpinCntPos, nextSpinDirPos,
                nextDivPos,
                nextSpinSpeedDirPos, nextSpeedCntPos, nextSpeedDirPos, nextSpeedPos);

        calcNextNodeState(nextNode, //sourceNode.speedCntArr,
                sourceNode.count,
                sourceNode.sourceEvent
                //actSpeedDirPos, nextSpeedCnt
        );
    }

    private static void calcNextNode2(Node sourceNode, Layer[] psiLayerArr, int nextPsiPos, int nextPsiArrPos,
                                      int nextSpinPos, int nextSpinCntPos, int nextSpinDirPos,
                                      int nextDivPos,
                                      int nextSpinSpeedDirPos, int nextSpeedCntPos, int nextSpeedDirPos, int nextSpeedPos,
                                      long nextNodeCount) {
        final Node nextNode = retrieveNode(psiLayerArr, nextPsiPos, nextPsiArrPos,
                nextSpinPos, nextSpinCntPos, nextSpinDirPos,
                nextDivPos,
                nextSpinSpeedDirPos, nextSpeedCntPos, nextSpeedDirPos, nextSpeedPos);

        calcNextNodeState2(nextNode, //sourceNode.speedCntArr,
                nextNodeCount, sourceNode.sourceEvent);
    }

    private static Node retrieveNode(Layer[] psiLayerArr, int psiPos, int psiArrPos,
                                                                    int spinPos, int spinCntPos, int spinDirPos,
                                                                    int divPos,
                                                                    int spinSpeedDirPos, int speedCntPos, int speedDirPos, int speedPos) {
        return psiLayerArr[psiPos].psiNodeArr[psiArrPos].spinNodeArr[spinPos].spinCntNodeArr[spinCntPos].spinDirNodeArr[spinDirPos].
                divNodeArr[divPos].spinSpeedDirNodeArr[spinSpeedDirPos].speedCntNodeArr[speedCntPos].dirSpeedNodeArr[speedDirPos][speedPos];
    }

    private static void calcNextNodeState(final Node nextNode,
                                          //final long[] sourceSpeedCntArr,
                                          final long count,
                                          final SourceEvent sourceEvent
                                          //final int actSpeedDirPos, final long nextSpeedCnt
    ) {
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
                                           //long[] speedCntArr,
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

            long ys1 = 0;
            long ys2 = 0;
            long yc1 = 0;
            long yc2 = 0;
            double yp1 = 0.0D;
            double yp2 = 0.0D;
            for (int spinPos = 0; spinPos < MAX_SPIN; spinPos++) {
                for (int spinCntPos = 0; spinCntPos < MAX_SPIN; spinCntPos++) {
                    for (int spinDirPos = 0; spinDirPos < DIR_SIZE; spinDirPos++) {
                        for (int divPos = 0; divPos < MAX_DIV; divPos++) {
                            for (int spinSpeedDirPos = 0; spinSpeedDirPos < DIR_SIZE; spinSpeedDirPos++) {
                                for (int speedCntPos = 0; speedCntPos <= MAX_SPEED_C; speedCntPos++) {
                                    for (int speedDirPos = 0; speedDirPos < DIR_SIZE; speedDirPos++) {
                                        for (int speedPos = 0; speedPos <= MAX_SPEED_C; speedPos++) {
                                            final Node node1 = psiLayerArr[nextPsiPos].psiNodeArr[xp1].spinNodeArr[spinPos].spinCntNodeArr[spinCntPos].spinDirNodeArr[spinDirPos].
                                                    divNodeArr[divPos].spinSpeedDirNodeArr[spinSpeedDirPos].speedCntNodeArr[speedCntPos].dirSpeedNodeArr[speedDirPos][speedPos];
                                            final Node node2 = psiLayerArr[nextPsiPos].psiNodeArr[xp2].spinNodeArr[spinPos].spinCntNodeArr[spinCntPos].spinDirNodeArr[spinDirPos].
                                                    divNodeArr[divPos].spinSpeedDirNodeArr[spinSpeedDirPos].speedCntNodeArr[speedCntPos].dirSpeedNodeArr[speedDirPos][speedPos];

                                            yp1 += calcNodePobability(node1, divPos + 1);
                                            yp2 += calcNodePobability(node2, divPos + 1);
                                            if (node1.count > 0) {
                                                yc1 += node1.count;

                                                {
                                                    double yd1 = 0.0D;
                                                    double yd2 = (divPos * node1.count) / Math.pow(2.0D, MAX_DIV / (MAX_DIV / 4.0D));
                                                    int y1 = (int) (VIEW_EXTRA_HEIGHT * yd1);
                                                    int y2 = (int) (VIEW_EXTRA_HEIGHT * yd2);
                                                    g.setColor(Color.RED);
                                                    g.drawLine(x1, VIEW_EXTRA_HEIGHT * 5 - (VIEW_EXTRA_HEIGHT - y2), x2, VIEW_EXTRA_HEIGHT * 5 - (VIEW_EXTRA_HEIGHT - y2));
                                                }
                                                ys1 += spinCntPos;

                                                {
                                                    double yd1 = 0.0D;
                                                    double yd2 = (spinCntPos * node1.count) / (double) MAX_SPIN;
                                                    int y1 = (int) (VIEW_HEIGHT * yd1 / 10.0D);
                                                    int y2 = (int) (VIEW_HEIGHT * yd2 / 10.0D);
                                                    g.setColor(Color.ORANGE);
                                                    g.drawLine(x1, VIEW_EXTRA_HEIGHT * 2 - y2, x2, VIEW_EXTRA_HEIGHT * 2 - y2);
                                                }
                                            }
                                            if (node2.count > 0) {
                                                yc2 += node2.count;
                                                ys2 += spinCntPos;
                                            }
                                        }
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
                int y1 = (int) (VIEW_EXTRA_HEIGHT * (yc1 / 10.0D));
                int y2 = (int) (VIEW_EXTRA_HEIGHT * (yc2 / 10.0D));
                g.setColor(Color.GREEN);
                g.drawLine(x1, VIEW_EXTRA_HEIGHT * 2 - y1, x2, VIEW_EXTRA_HEIGHT * 2 - y2);
            }
            {
                int y1 = (int) (VIEW_EXTRA_HEIGHT * (ys1 / 10.0D));
                int y2 = (int) (VIEW_EXTRA_HEIGHT * (ys2 / 10.0D));
                g.setColor(Color.ORANGE);
                g.drawLine(x1, VIEW_EXTRA_HEIGHT * 2 - y1, x2, VIEW_EXTRA_HEIGHT * 2 - y2);
            }
            for (int dirPos = 0; dirPos < DIR_SIZE; dirPos++) {
                double ye1 = this.realityNodeArr[xp1].eFieldArray[dirPos];
                double ye2 = this.realityNodeArr[xp2].eFieldArray[dirPos];
                int y1 = (int) (VIEW_EXTRA_HEIGHT * (ye1)) + dirPos;
                int y2 = (int) (VIEW_EXTRA_HEIGHT * (ye2)) + dirPos;
                g.setColor(Color.BLUE);
                g.drawLine(x1, VIEW_EXTRA_HEIGHT * 6 - y1, x2, VIEW_EXTRA_HEIGHT * 6 - y2);
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
}
