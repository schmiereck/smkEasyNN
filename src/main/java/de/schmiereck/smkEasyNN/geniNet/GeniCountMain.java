package de.schmiereck.smkEasyNN.geniNet;

import java.util.Arrays;
import java.util.Random;

import static de.schmiereck.smkEasyNN.geniNet.GeniNetService.VALUE_0;
import static de.schmiereck.smkEasyNN.geniNet.GeniNetService.createGeniNetSynapse;

public class GeniCountMain {

    /**
     * DCS Directed Cyclic Graphs.
     */
    public static void main(String[] args) {
        final Random rnd = new Random();

        final int[][] trainInputArrArr = new int[][]
                {
                        { 0, 0 },
                        { 0, 1 },
                        { 1, 0 },
                        { 1, 1 },
                };
        final int[][] expectedOutputArrArr = new int[][]
                {
                        { 0, 0, 0 },
                        { 0, 0, 1 },
                        { 0, 1, 0 },
                        { 1, 0, 0 },
                };
        final int[] layerSizeArr = new int[]{ 2, 3, 3 };

        final GeniNet geniNet = GeniNetService.createNet(layerSizeArr, rnd);

        // better Results: lower mutationRate (/10), bigger population size (*10), bigger epoche size (*10)
        final float minMutationRate = 0.014F;
        final float maxMutationRate = 0.18F;
        final float copyPercent = 0.011F;
        final int populationSize = 800;
        final int epocheSize = 1_000;

        final GeniNet trainedGeniNet = GeniNetTrainService.runTrainNet(geniNet,
                minMutationRate, maxMutationRate, populationSize, epocheSize, copyPercent,
                expectedOutputArrArr, trainInputArrArr, rnd);

        showWinner(trainedGeniNet, expectedOutputArrArr, trainInputArrArr);
    }

    private static void showWinner(final GeniNet net, final int[][] expectedOutputArrArr, final int[][] trainInputArrArr) {
        System.out.println("Winner:");
        for (int pos = 0; pos < trainInputArrArr.length; pos++) {
            final int[] inArr = trainInputArrArr[pos];
            final int[] outArr = expectedOutputArrArr[pos];
            GeniNetService.submitInputValue(net, 0, inArr[0]);
            GeniNetService.submitInputValue(net, 1, inArr[1]);
            GeniNetService.calc(net);
            System.out.printf("in: %s", Arrays.toString(inArr));
            System.out.printf(" exp.out: %s", Arrays.toString(outArr));
            System.out.print(" out: [");
            for (int outPos = 0; outPos < outArr.length; outPos++) {
                if (outPos > 0) {
                    System.out.print(", ");
                }
                final int outputValue = GeniNetService.retrieveOutputValue(net, outPos);
                System.out.printf("%+1.1f", outputValue);
            }
            System.out.println("]");
        }
    }

    private static void showOutput(final GeniNet geniNet, final int[] expectedOutputArr) {

        for (int outputPos = 0; outputPos < expectedOutputArr.length; outputPos++) {
            final int outputValue = GeniNetService.retrieveOutputValue(geniNet, outputPos);
            final int expectedOutputValue = expectedOutputArr[outputPos];
            final int diff = (expectedOutputValue - outputValue);
        }
    }

    private static GeniNet createNet(final Random rnd) {
        final GeniNet geniNet = new GeniNet();

        final int initBias = VALUE_0;

        // Inputs:
        final GeniNeuron in1GeniNeuron = new GeniNeuron(GeniNeuron.NeuronType.Input, initBias);
        GeniNetService.submitNewNeuron(geniNet, in1GeniNeuron);

        final GeniNeuron in2GeniNeuron = new GeniNeuron(GeniNeuron.NeuronType.Input, initBias);
        GeniNetService.submitNewNeuron(geniNet, in2GeniNeuron);

        // Hidden:
        final GeniNeuron hide1GeniNeuron = new GeniNeuron(GeniNeuron.NeuronType.Hidden, initBias);
        GeniNetService.submitNewNeuron(geniNet, hide1GeniNeuron);

        final GeniNeuron hide2GeniNeuron = new GeniNeuron(GeniNeuron.NeuronType.Hidden, initBias);
        GeniNetService.submitNewNeuron(geniNet, hide2GeniNeuron);

        final GeniNeuron hide3GeniNeuron = new GeniNeuron(GeniNeuron.NeuronType.Hidden, initBias);
        GeniNetService.submitNewNeuron(geniNet, hide3GeniNeuron);

        // Outputs:
        final GeniNeuron out1GeniNeuron = new GeniNeuron(GeniNeuron.NeuronType.Output, initBias);
        GeniNetService.submitNewNeuron(geniNet, out1GeniNeuron);

        final GeniNeuron out2GeniNeuron = new GeniNeuron(GeniNeuron.NeuronType.Output, initBias);
        GeniNetService.submitNewNeuron(geniNet, out2GeniNeuron);

        final GeniNeuron out3GeniNeuron = new GeniNeuron(GeniNeuron.NeuronType.Output, initBias);
        GeniNetService.submitNewNeuron(geniNet, out3GeniNeuron);

        // Synapses:
        // Inputs to Hidden:
        createGeniNetSynapse(in1GeniNeuron, hide1GeniNeuron, rnd);
        createGeniNetSynapse(in1GeniNeuron, hide2GeniNeuron, rnd);
        createGeniNetSynapse(in1GeniNeuron, hide3GeniNeuron, rnd);

        createGeniNetSynapse(in2GeniNeuron, hide1GeniNeuron, rnd);
        createGeniNetSynapse(in2GeniNeuron, hide2GeniNeuron, rnd);
        createGeniNetSynapse(in2GeniNeuron, hide3GeniNeuron, rnd);

        // Hidden to Outputs:
        createGeniNetSynapse(hide1GeniNeuron, out1GeniNeuron, rnd);
        createGeniNetSynapse(hide1GeniNeuron, out2GeniNeuron, rnd);
        createGeniNetSynapse(hide1GeniNeuron, out3GeniNeuron, rnd);

        createGeniNetSynapse(hide2GeniNeuron, out1GeniNeuron, rnd);
        createGeniNetSynapse(hide2GeniNeuron, out2GeniNeuron, rnd);
        createGeniNetSynapse(hide2GeniNeuron, out3GeniNeuron, rnd);

        createGeniNetSynapse(hide3GeniNeuron, out1GeniNeuron, rnd);
        createGeniNetSynapse(hide3GeniNeuron, out2GeniNeuron, rnd);
        createGeniNetSynapse(hide3GeniNeuron, out3GeniNeuron, rnd);

        return geniNet;
    }
}
