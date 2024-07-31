package de.schmiereck.smkEasyNN.wave;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
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

    //private static boolean UseProbability = true;
    private static boolean UseProbability = false;

    private static long eventIdCounter = 0;

    private static class SourceEvent {
        final long eventId;
        final SourceEvent parentSourceEvent;
        HashMap<SourceEvent, SourceEvent> childSourceEventMap = new HashMap<>();

        public SourceEvent(final SourceEvent parentSourceEvent) {
            this.parentSourceEvent = parentSourceEvent;
            this.eventId = eventIdCounter++;
        }

        public SourceEvent retrieveChildSourceEvent(final SourceEvent otherSourceEvent, final boolean useParentChain) {
            return Objects.requireNonNullElseGet(this.childSourceEventMap.get(otherSourceEvent),
                () -> {
                    final SourceEvent sourceEvent = retrieveChildSourceEvent(this, otherSourceEvent, useParentChain);
                    return Objects.requireNonNullElseGet(sourceEvent,
                        () -> {
                            //final SourceEvent newSourceEvent = new SourceEvent(this);
                            //this.childSourceEventMap.put(otherSourceEvent, newSourceEvent);
                            //return newSourceEvent;
                            return createSourceEvent(this, otherSourceEvent);
                        });
                });
        }

        private static SourceEvent retrieveChildSourceEvent(final SourceEvent thisSourceEvent, final SourceEvent otherSourceEvent,
                                                            final boolean useParentChain) {
            final SourceEvent retSourceEvent;
            if ((thisSourceEvent == otherSourceEvent.parentSourceEvent)) {
                //TODO in Paranet-Chain größere Source-ID heraussuchen
                if (thisSourceEvent.eventId > otherSourceEvent.eventId) {
                    //if (thisSourceEvent.parentSourceEvent.eventId > otherSourceEvent.eventId) {
                    retSourceEvent = thisSourceEvent;
                } else {
                    retSourceEvent = otherSourceEvent;
                }
                //retSourceEvent = otherSourceEvent;
            } else {
                if ((thisSourceEvent.parentSourceEvent == otherSourceEvent)) {
                    //TODO in Paranet-Chain größere Source-ID heraussuchen
                    if (thisSourceEvent.eventId > otherSourceEvent.eventId) {
                    //if (thisSourceEvent.parentSourceEvent.eventId > otherSourceEvent.eventId) {
                        retSourceEvent = thisSourceEvent;
                    } else {
                        retSourceEvent = otherSourceEvent;
                    }
                    //retSourceEvent = thisSourceEvent;
                    //retSourceEvent = otherSourceEvent;
                } else {
                    if (useParentChain && Objects.nonNull(otherSourceEvent.parentSourceEvent)) {
                        retSourceEvent = retrieveChildSourceEvent(thisSourceEvent, otherSourceEvent.parentSourceEvent, useParentChain);
                    } else {
                        retSourceEvent = null;
                    }
                }
            }
            return retSourceEvent;
        }
    }

    private static SourceEvent createSourceEvent(final SourceEvent aSourceSourceEvent) {
        return createSourceEvent(BigBangSourceEvent, aSourceSourceEvent);
    }

    private static SourceEvent createSourceEvent(final SourceEvent thisSourceEvent, final SourceEvent otherSourceEvent) {
        final SourceEvent aOtherSourceEvent = new SourceEvent(otherSourceEvent);
        thisSourceEvent.childSourceEventMap.put(otherSourceEvent, aOtherSourceEvent);

        System.out.println("createSourceEvent - newSourceEvent:%d <- (this(%d, parrent:%d), other(%d, parrent:%d))".formatted(
                aOtherSourceEvent.eventId,
                thisSourceEvent.eventId, Objects.nonNull(thisSourceEvent.parentSourceEvent) ? thisSourceEvent.parentSourceEvent.eventId : -1,
                otherSourceEvent.eventId, Objects.nonNull(otherSourceEvent.parentSourceEvent) ? otherSourceEvent.parentSourceEvent.eventId : -1));
        return aOtherSourceEvent;
    }

    private static final SourceEvent BigBangSourceEvent = new SourceEvent(null);

    private static class Node {
        long count = 0;
        long eFieldCount;
        SourceEvent sourceEvent = null;
        SourceEvent eFieldSourceEvent = null;

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

        //initTest1();
        initTest2(false, false);
        //initTestMain(true);
        //initTestMain(false);
    }

    private void initTest1() {
        UseProbability = false;

        //--------------------------------------------------------------------------------------------------------------
        final SourceEvent aSourceSourceEvent = createSourceEvent(BigBangSourceEvent);

        final SourceEvent bSourceSourceEvent = createSourceEvent(BigBangSourceEvent);

        //--------------------------------------------------------------------------------------------------------------
        //final SourceEvent aOtherSourceEvent = BigBangSourceEvent.retrieveChildSourceEvent(new SourceEvent(BigBangSourceEvent));
        final SourceEvent aOtherSourceEvent = createSourceEvent(aSourceSourceEvent);

        //final SourceEvent bOtherSourceEvent = BigBangSourceEvent.retrieveChildSourceEvent(new SourceEvent(BigBangSourceEvent));
        final SourceEvent bOtherSourceEvent = createSourceEvent(bSourceSourceEvent);
        //--------------------------------------------------------------------------------------------------------------
        {
            // to right, very fast (1/4):
            final int nextArrPos = ((PsiArrSize / 4) * 1) - 1;
            final Node node = retrieveNode(this.psiLayerArr, this.psiPos, nextArrPos,
                    1, 0, 0,
                    0, 0, 1, MAX_SPEED_C);// - ((MAX_SPEED_C / 4)));
            node.count = intPow2(MAX_DIV) / 4;
            //node.count = 0;//intPow2(MAX_DIV);
            node.sourceEvent = BigBangSourceEvent.retrieveChildSourceEvent(aOtherSourceEvent, false);
        }
        {
            // to right, very fast (1/2):
            final int nextArrPos = ((PsiArrSize / 4) * 1);
            final Node node = retrieveNode(this.psiLayerArr, this.psiPos, nextArrPos,
                    1, 0, 0,
                    0, 0, 1, MAX_SPEED_C);// - ((MAX_SPEED_C / 4)));
            node.count = intPow2(MAX_DIV) / 2;
            //node.count = 0;//intPow2(MAX_DIV);
            node.sourceEvent = BigBangSourceEvent.retrieveChildSourceEvent(aOtherSourceEvent, false);
        }
        {
            // to right, very fast (1/3):
            final int nextArrPos = ((PsiArrSize / 4) * 1) + 1;
            final Node node = retrieveNode(this.psiLayerArr, this.psiPos, nextArrPos,
                    1, 0, 0,
                    0, 0, 1, MAX_SPEED_C);// - ((MAX_SPEED_C / 4)));
            node.count = intPow2(MAX_DIV) / 3;
            //node.count = 0;//intPow2(MAX_DIV);
            node.sourceEvent = BigBangSourceEvent.retrieveChildSourceEvent(aOtherSourceEvent, false);
        }
        //--------------------------------------------------------------------------------------------------------------
        {
            // to left, very fast (1/3):
            final int nextArrPos = ((PsiArrSize / 4) * 3) - 1;
            final Node node = retrieveNode(this.psiLayerArr, this.psiPos, nextArrPos,
                    1, 0, 1,
                    0, 0, 0, MAX_SPEED_C);// - ((MAX_SPEED_C / 4)));
            node.count = intPow2(MAX_DIV) / 3;
            //node.count = 0;//intPow2(MAX_DIV);
            node.sourceEvent = BigBangSourceEvent.retrieveChildSourceEvent(bOtherSourceEvent, false);
        }
        {
            // to left, very fast (1/2):
            final int nextArrPos = ((PsiArrSize / 4) * 3);
            final Node node = retrieveNode(this.psiLayerArr, this.psiPos, nextArrPos,
                    1, 0, 1,
                    0, 0, 0, MAX_SPEED_C);// - ((MAX_SPEED_C / 4)));
            node.count = intPow2(MAX_DIV) / 2;
            //node.count = 0;//intPow2(MAX_DIV);
            node.sourceEvent = BigBangSourceEvent.retrieveChildSourceEvent(bOtherSourceEvent, false);
        }
        {
            // to left, very fast (1/4):
            final int nextArrPos = ((PsiArrSize / 4) * 3) + 1;
            final Node node = retrieveNode(this.psiLayerArr, this.psiPos, nextArrPos,
                    1, 0, 1,
                    0, 0, 0, MAX_SPEED_C);// - ((MAX_SPEED_C / 4)));
            node.count = intPow2(MAX_DIV) / 4;
            //node.count = 0;//intPow2(MAX_DIV);
            node.sourceEvent = BigBangSourceEvent.retrieveChildSourceEvent(bOtherSourceEvent, false);
        }
        //--------------------------------------------------------------------------------------------------------------
    }

    private void initTest2(final boolean useProbability, final boolean use3) {
        UseProbability = useProbability;

        //--------------------------------------------------------------------------------------------------------------
        final SourceEvent aSourceSourceEvent = createSourceEvent(BigBangSourceEvent);

        final SourceEvent bSourceSourceEvent = createSourceEvent(BigBangSourceEvent);

        final SourceEvent cSourceSourceEvent;
        if (use3) {
            cSourceSourceEvent = new SourceEvent(BigBangSourceEvent);
            BigBangSourceEvent.childSourceEventMap.put(BigBangSourceEvent, cSourceSourceEvent);
        } else {
            cSourceSourceEvent = null;
        }
        //--------------------------------------------------------------------------------------------------------------
        final SourceEvent aOtherSourceEvent = createSourceEvent(aSourceSourceEvent);

        final SourceEvent bOtherSourceEvent = createSourceEvent(bSourceSourceEvent);

        final SourceEvent cOtherSourceEvent;
        if (use3) {
            cOtherSourceEvent = new SourceEvent(cSourceSourceEvent);
            BigBangSourceEvent.childSourceEventMap.put(cSourceSourceEvent, cOtherSourceEvent);
        } else {
            cOtherSourceEvent = null;
        }
        //--------------------------------------------------------------------------------------------------------------
        // P1: Left -> to right, Fast:
        final int p1NextArrPos = ((PsiArrSize / 4) * 1);
        final int p1SpeedDirPos = 1;
        final int p1Speed = MAX_SPEED_C - ((MAX_SPEED_C / 4));
        final int p1SpinPos = 1;
        final int p1SpinDirPos = 0;
        {
            // to right, very fast (1/4):
            final int nextArrPos = p1NextArrPos - 1;
            final Node node = retrieveNode(this.psiLayerArr, this.psiPos, nextArrPos,
                    p1SpinPos, 0, p1SpinDirPos,
                    0, 0, p1SpeedDirPos, p1Speed);
            node.count = intPow2(MAX_DIV) / 4;
            node.sourceEvent = BigBangSourceEvent.retrieveChildSourceEvent(aOtherSourceEvent, false);
        }
        {
            // to right, very fast (1/2):
            final int nextArrPos = p1NextArrPos;
            final Node node = retrieveNode(this.psiLayerArr, this.psiPos, nextArrPos,
                    p1SpinPos, 0, p1SpinDirPos,
                    0, 0, p1SpeedDirPos, p1Speed);
            node.count = intPow2(MAX_DIV) / 2;
            node.sourceEvent = BigBangSourceEvent.retrieveChildSourceEvent(aOtherSourceEvent, false);
        }
        {
            // to right, very fast (1/3):
            final int nextArrPos = p1NextArrPos + 1;
            final Node node = retrieveNode(this.psiLayerArr, this.psiPos, nextArrPos,
                    p1SpinPos, 0, p1SpinDirPos,
                    0, 0, p1SpeedDirPos, p1Speed);
            node.count = intPow2(MAX_DIV) / 3;
            node.sourceEvent = BigBangSourceEvent.retrieveChildSourceEvent(aOtherSourceEvent, false);
        }
        //--------------------------------------------------------------------------------------------------------------
        // P2: Middle, Stay:
        final int p2NextArrPos = ((PsiArrSize / 4) * 2);
        final int p2SpeedDirPos = 1;
        final int p2Speed = 0;
        final int p2SpinPos = 5;
        final int p2SpinDirPos = 0;
        {
            // to right, very fast (1/4):
            final int nextArrPos = p2NextArrPos - 1;
            final Node node = retrieveNode(this.psiLayerArr, this.psiPos, nextArrPos,
                    p2SpinPos, 0, p2SpinDirPos,
                    0, 0, p2SpeedDirPos, p2Speed);
            node.count = intPow2(MAX_DIV) / 4;
            node.sourceEvent = BigBangSourceEvent.retrieveChildSourceEvent(bOtherSourceEvent, false);
        }
        {
            // to right, very fast (1/2):
            final int nextArrPos = p2NextArrPos;
            final Node node = retrieveNode(this.psiLayerArr, this.psiPos, nextArrPos,
                    p2SpinPos, 0, p2SpinDirPos,
                    0, 0, p2SpeedDirPos, p2Speed);
            node.count = intPow2(MAX_DIV) / 2;
            node.sourceEvent = BigBangSourceEvent.retrieveChildSourceEvent(bOtherSourceEvent, false);
        }
        {
            // to right, very fast (1/3):
            final int nextArrPos = p2NextArrPos + 1;
            final Node node = retrieveNode(this.psiLayerArr, this.psiPos, nextArrPos,
                    p2SpinPos, 0, p2SpinDirPos,
                    0, 0, p2SpeedDirPos, p2Speed);
            node.count = intPow2(MAX_DIV) / 4;
            node.sourceEvent = BigBangSourceEvent.retrieveChildSourceEvent(bOtherSourceEvent, false);
        }
        //--------------------------------------------------------------------------------------------------------------
        // P3: Right -> to left, Slowly:
        if (use3) {
            final int p3NextArrPos = ((PsiArrSize / 4) * 3);
            final int p3SpeedDirPos = 0;
            final int p3Speed = MAX_SPEED_C - ((MAX_SPEED_C / 4) * 3);
            final int p3SpinPos = 1;
            final int p3SpinDirPos = 1;
            {
                // to right, very fast (1/4):
                final int nextArrPos = p3NextArrPos + 1;
                final Node node = retrieveNode(this.psiLayerArr, this.psiPos, nextArrPos,
                        p3SpinPos, 0, p3SpinDirPos,
                        0, 0, p3SpeedDirPos, p3Speed);
                node.count = intPow2(MAX_DIV) / 4;
                node.sourceEvent = BigBangSourceEvent.retrieveChildSourceEvent(cOtherSourceEvent, false);
            }
            {
                // to right, very fast (1/2):
                final int nextArrPos = p3NextArrPos;
                final Node node = retrieveNode(this.psiLayerArr, this.psiPos, nextArrPos,
                        p3SpinPos, 0, p3SpinDirPos,
                        0, 0, p3SpeedDirPos, p3Speed);
                node.count = intPow2(MAX_DIV) / 2;
                node.sourceEvent = BigBangSourceEvent.retrieveChildSourceEvent(cOtherSourceEvent, false);
            }
            {
                // to right, very fast (1/3):
                final int nextArrPos = p3NextArrPos - 1;
                final Node node = retrieveNode(this.psiLayerArr, this.psiPos, nextArrPos,
                        p3SpinPos, 0, p3SpinDirPos,
                        0, 0, p3SpeedDirPos, p3Speed);
                node.count = intPow2(MAX_DIV) / 3;
                node.sourceEvent = BigBangSourceEvent.retrieveChildSourceEvent(cOtherSourceEvent, false);
            }
        }
    }

    private void initTestMain(final boolean useProbability) {
        UseProbability = useProbability;

        //--------------------------------------------------------------------------------------------------------------
        final SourceEvent aSourceSourceEvent = createSourceEvent(BigBangSourceEvent);

        final SourceEvent bSourceSourceEvent = createSourceEvent(BigBangSourceEvent);

        final SourceEvent cSourceSourceEvent = createSourceEvent(BigBangSourceEvent);

        //--------------------------------------------------------------------------------------------------------------
        //final SourceEvent aOtherSourceEvent = BigBangSourceEvent.retrieveChildSourceEvent(new SourceEvent(BigBangSourceEvent));
        final SourceEvent aOtherSourceEvent = createSourceEvent(aSourceSourceEvent);

        //final SourceEvent bOtherSourceEvent = BigBangSourceEvent.retrieveChildSourceEvent(new SourceEvent(BigBangSourceEvent));
        final SourceEvent bOtherSourceEvent = createSourceEvent(bSourceSourceEvent);

        //final SourceEvent cOtherSourceEvent = BigBangSourceEvent.retrieveChildSourceEvent(new SourceEvent(BigBangSourceEvent));
        final SourceEvent cOtherSourceEvent = createSourceEvent(cSourceSourceEvent);
        //--------------------------------------------------------------------------------------------------------------
        {
            // to right, very fast:
            final int nextArrPos = ((PsiArrSize / 4) * 1);
            final Node node = retrieveNode(this.psiLayerArr, this.psiPos, nextArrPos,
                    1, 0, 0,
                    0, 0, 1, MAX_SPEED_C - ((MAX_SPEED_C / 4)));
            node.count = intPow2(MAX_DIV);
            //node.count = 0;//intPow2(MAX_DIV);
            node.sourceEvent = BigBangSourceEvent.retrieveChildSourceEvent(aOtherSourceEvent, false);
        }
        {
            // stay in middle:
            final int nextArrPos = ((PsiArrSize / 4) * 2);
            final Node node = retrieveNode(this.psiLayerArr, this.psiPos, nextArrPos,
                    5, 0, 0,
                    0, 0, 1, 0);
            node.count = intPow2(MAX_DIV);
            //node.count = 0;//intPow2(MAX_DIV);
            node.sourceEvent = BigBangSourceEvent.retrieveChildSourceEvent(bOtherSourceEvent, false);
        }
        {
            // to left, slowly:
            final int nextArrPos = ((PsiArrSize / 4) * 3);
            final Node node = retrieveNode(this.psiLayerArr, this.psiPos, nextArrPos,
                    1, 0, 1,
                    0, 0, 0, MAX_SPEED_C - ((MAX_SPEED_C / 4) * 3));
            node.count = intPow2(MAX_DIV);
            //node.count = 0;//intPow2(MAX_DIV);
            node.sourceEvent = BigBangSourceEvent.retrieveChildSourceEvent(cOtherSourceEvent, false);
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
                                            node.eFieldSourceEvent = null;
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
                                                    calcNextNode(this.psiLayerArr, actPsiPos, nextPsiPos, psiArrPos, nextPsiArrPos,
                                                            spinPos, nextSpinCntPos, nextSpinDirPos,
                                                            nextSpinSpeedDirPos, nextSpeedCntPos, nextSpeedDirPos, nextSpeedPos,
                                                            actNode.count, actNode.sourceEvent);
                                                } else {
                                                    nextSpeedCntPos = calcSpeedCntPos;
                                                    final int moveDir = actSpinDirPos == 0 ? -1 : 1;

                                                    final long hNodeCount = actNode.count / 2;
                                                    final long lhNodeCount = actNode.count % 2;

                                                    // If reaching MAX_DIV: Use nextNode for the next actNode to stay in position.
                                                    //final int nextDivPos = divPos + 1;
                                                    //if (nextDivPos < MAX_DIV) {
                                                    if (hNodeCount > 0) {
                                                        if (UseProbability) {
                                                            calcNextNode(this.psiLayerArr, actPsiPos, nextPsiPos, psiArrPos, psiArrPos,
                                                                    spinPos, nextSpinCntPos, nextSpinDirPos,
                                                                    nextSpinSpeedDirPos, nextSpeedCntPos, nextSpeedDirPos, nextSpeedPos,
                                                                    hNodeCount + lhNodeCount, actNode.sourceEvent);

                                                            final int nextPsiArrPos = (psiArrPos + moveDir + PsiArrSize) % PsiArrSize;
                                                            calcNextNode(this.psiLayerArr, actPsiPos, nextPsiPos, psiArrPos, nextPsiArrPos,
                                                                    spinPos, nextSpinCntPos, nextSpinDirPos,
                                                                    nextSpinSpeedDirPos, nextSpeedCntPos, nextSpeedDirPos, nextSpeedPos,
                                                                    hNodeCount, actNode.sourceEvent);
                                                        } else {
                                                            calcNextNode(this.psiLayerArr, actPsiPos, nextPsiPos, psiArrPos, psiArrPos,
                                                                    spinPos, nextSpinCntPos, nextSpinDirPos,
                                                                    nextSpinSpeedDirPos, nextSpeedCntPos, nextSpeedDirPos, nextSpeedPos,
                                                                    actNode.count, actNode.sourceEvent);
                                                        }
                                                    } else {
                                                        calcNextNode(this.psiLayerArr, actPsiPos, nextPsiPos, psiArrPos, psiArrPos,
                                                                spinPos, nextSpinCntPos, nextSpinDirPos,
                                                                nextSpinSpeedDirPos, nextSpeedCntPos, nextSpeedDirPos, nextSpeedPos,
                                                                actNode.count, actNode.sourceEvent);
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
                    Thread.sleep(25*3);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("END: Simulating.");
    }

    private static void calcNextNode(final Layer[] psiLayerArr, final int actPsiPos, final int nextPsiPos, final int actPsiArrPos, final int nextPsiArrPos,
                                     final int nextSpinPos, final int nextSpinCntPos, final int nextSpinDirPos,
                                     final int nextSpinSpeedDirPos, final int nextSpeedCntPos, final int nextSpeedDirPos, final int nextSpeedPos,
                                     final long nextNodeCount, final SourceEvent nextSourceEvent) {
        final Node nextNode = retrieveNode(psiLayerArr, nextPsiPos, nextPsiArrPos,
                nextSpinPos, nextSpinCntPos, nextSpinDirPos,
                nextSpinSpeedDirPos, nextSpeedCntPos, nextSpeedDirPos, nextSpeedPos);

        long usedNextNodeCount = nextNodeCount;

        // if nextNode contains eField of nextSourceEvent and another SourceEvent,
        // then do a reflection.
        // Use: Count of eField, Speed, create new SourceEvent with both SourceEvents.
        final PsiNode nextPsiNode = psiLayerArr[actPsiPos].psiNodeArr[nextPsiArrPos];
        //for (int spinDirPos = 0; spinDirPos < DIR_SIZE; spinDirPos++) {
        final int spinDirPos = 0; {
            final SpinDirNode spinDirNode = nextPsiNode.spinDirNodeArr[spinDirPos];
            //for (int spinPos = 0; spinPos < MAX_SPIN; spinPos++) {
            final int spinPos = 0;  {
                //for (int spinCntPos = 0; spinCntPos < MAX_SPIN; spinCntPos++) {
                final int spinCntPos = 0;  {
                    final SpinCntNode spinCntNode = spinDirNode.spinNodeArr[spinPos].spinCntNodeArr[spinCntPos];
                    for (int spinSpeedDirPos = 0; spinSpeedDirPos < DIR_SIZE; spinSpeedDirPos++) {
                        for (int speedCntPos = 0; speedCntPos <= MAX_SPEED_C; speedCntPos++) {
                            for (int speedDirPos = 0; speedDirPos < DIR_SIZE; speedDirPos++) {
                                for (int speedPos = 0; speedPos <= MAX_SPEED_C; speedPos++) {
                                    final Node eFieldNode = spinCntNode.
                                            spinSpeedDirNodeArr[spinSpeedDirPos].speedCntNodeArr[speedCntPos].dirSpeedNodeArr[speedDirPos][speedPos];

                                    final long eFieldCount = Math.min(usedNextNodeCount, eFieldNode.eFieldCount);
                                    final SourceEvent eFieldSourceEvent = eFieldNode.eFieldSourceEvent;

                                    if (Objects.nonNull(eFieldSourceEvent) && (eFieldSourceEvent != nextSourceEvent) &&
                                            //TODO ((nextSpeedDirPos != speedDirPos) && (nextSpeedPos != speedPos)) &&
                                            (eFieldCount > 0)) {

                                        final Node sourceNode = retrieveNode(psiLayerArr, nextPsiPos, actPsiArrPos,
                                                nextSpinPos, nextSpinCntPos, nextSpinDirPos,
                                                spinSpeedDirPos, speedCntPos, speedDirPos, speedPos);

                                        final SourceEvent newSourceEvent = nextSourceEvent.retrieveChildSourceEvent(eFieldSourceEvent, true);
                                        System.out.println("Reflection: cnt:" + eFieldCount + ", newSourceEvent:" + newSourceEvent.eventId +
                                                " <- (nextSourceEvent:(" + nextSourceEvent.eventId + ",parent:" + (Objects.nonNull(nextSourceEvent.parentSourceEvent) ? nextSourceEvent.parentSourceEvent.eventId : -1) + "), " +
                                                "eFieldSourceEvent:(" + eFieldSourceEvent.eventId +",parent:" + (Objects.nonNull(eFieldSourceEvent.parentSourceEvent) ? eFieldSourceEvent.parentSourceEvent.eventId : -1) + ")) " +
                                                "nextSpeedDirPos:" + nextSpeedDirPos + ", speedDirPos:" + speedDirPos + ", nextSpeedPos:" + nextSpeedPos + ", speedPos:" + speedPos);

                                        eFieldNode.eFieldCount -= eFieldCount;
                                        //eFieldNode.eFieldSourceEvent = null;

                                        calcNextState(psiLayerArr, nextPsiPos, actPsiArrPos,
                                                spinSpeedDirPos, speedCntPos, speedDirPos, speedPos,
                                                newSourceEvent,
                                                sourceNode, eFieldCount);

                                        usedNextNodeCount -= eFieldCount;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (usedNextNodeCount > 0) {
            calcNextState(psiLayerArr, nextPsiPos, nextPsiArrPos,
                    nextSpinSpeedDirPos, nextSpeedCntPos, nextSpeedDirPos, nextSpeedPos,
                    nextSourceEvent, nextNode, usedNextNodeCount);
        }
    }

    private static void calcNextState(Layer[] psiLayerArr, int nextPsiPos, int nextPsiArrPos,
                                      int nextSpinSpeedDirPos, int nextSpeedCntPos, int nextSpeedDirPos, int nextSpeedPos,
                                      SourceEvent nextSourceEvent, Node nextNode, long usedNextNodeCount) {
        calcNextNodeState(nextNode,
                usedNextNodeCount,//sourceNode.count
                nextSourceEvent);//sourceNode.sourceEvent

        // Actual Node:
        {
            final int moveDir = 0;
            calcNextEFieldState(psiLayerArr, nextPsiPos, nextPsiArrPos, nextSpinSpeedDirPos, nextSpeedDirPos, nextSpeedPos, moveDir,
                    usedNextNodeCount, nextSourceEvent);
        }
        // Move to Node in next step.
        {
            final int calcSpeedCntPos = nextSpeedCntPos + nextSpeedPos;
            if (calcSpeedCntPos >= MAX_SPEED_C) {
                final int moveDir = nextSpeedDirPos == 0 ? -1 : 1;
                calcNextEFieldState(psiLayerArr, nextPsiPos, nextPsiArrPos, nextSpinSpeedDirPos, nextSpeedDirPos, nextSpeedPos, moveDir,
                        usedNextNodeCount, nextSourceEvent);//sourceNode.count
            }
        }
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

    private static void calcNextEFieldState(Layer[] psiLayerArr, int nextPsiPos, int nextPsiArrPos, int nextSpinSpeedDirPos, int nextSpeedDirPos, int nextSpeedPos, int moveDir,
                                            long fieldCount, final SourceEvent nextEFieldSourceEvent) {
        final int nPsiArrPos = (nextPsiArrPos + moveDir + PsiArrSize) % PsiArrSize;

        final Node nextEFieldNode = retrieveNode(psiLayerArr, nextPsiPos, nPsiArrPos,
                0, 0, 0,
                nextSpinSpeedDirPos, 0, nextSpeedDirPos, nextSpeedPos);

        nextEFieldNode.eFieldCount += fieldCount;
        if (Objects.nonNull(nextEFieldNode.eFieldSourceEvent) && (nextEFieldNode.eFieldSourceEvent != nextEFieldSourceEvent)) {
            throw new RuntimeException("nextEFieldNode.eFieldSourceEvent(%d) != nextEFieldSourceEvent(%d)".
                    formatted(nextEFieldNode.eFieldSourceEvent.eventId, nextEFieldSourceEvent.eventId));
        }
        nextEFieldNode.eFieldSourceEvent = nextEFieldSourceEvent;
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

        final double xSampleSize = VIEW_WIDTH / (double) PsiArrSize;
        for (int psiArrPos = 0; psiArrPos < PsiArrSize - 1; psiArrPos++) {
            int xp1 = (psiArrPos + PsiArrSize) % PsiArrSize;
            int xp2 = (psiArrPos + 1 + PsiArrSize) % PsiArrSize;
            int x1 = (int) (xp1 * xSampleSize);
            int x2 = (int) ((xp2) * xSampleSize);

            double[] ye1 = new double[DIR_SIZE];
            double[] ye2 = new double[DIR_SIZE];
            long ys1 = 0;
            long ys2 = 0;
            long yc1 = 0;
            long yc2 = 0;
            double yp1 = 0.0D;
            double yp2 = 0.0D;
            int lastY2 = 0;
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
                                                int y2 = (int) Math.round((VIEW_HEIGHT * yd2 / 10.0D) + 0.5D);
                                                g.setColor(createColorById(node1.sourceEvent.eventId));
                                                lastY2 += y2;
                                                g.fillRect(x1, VIEW_EXTRA_HEIGHT * 7 - lastY2, (int) xSampleSize, y2);
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

    private static Color createColorById(final long id) {
        return new Color(
                (int) (id * 64) % 255,
                (int) ((id + 64) * 32) % 255,
                (int) ((id + 128) * 16) % 255);
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
