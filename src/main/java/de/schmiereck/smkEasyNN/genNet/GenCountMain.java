package de.schmiereck.smkEasyNN.genNet;

import java.util.*;

import static de.schmiereck.smkEasyNN.genNet.GenNetService.createGenNetSynapse;

public class GenCountMain {

    /**
     * DCS Directed Cyclic Graphs.
     */
    public static void main(String[] args) {
        final Random rnd = new Random();

        final float[][] trainInputArrArr = new float[][]
                {
                        { 0, 0 },
                        { 0, 1 },
                        { 1, 0 },
                        { 1, 1 },
                };
        final float[][] expectedOutputArrArr = new float[][]
                {
                        { 0, 0, 0 },
                        { 0, 0, 1 },
                        { 0, 1, 0 },
                        { 1, 0, 0 },
                };
        final int[] layerSizeArr = new int[]{ 2, 3, 3 };

        final GenNet genNet = GenNetService.createNet(layerSizeArr, rnd);

        // better Results: lower mutationRate (/10), bigger population size (*10), bigger epoche size (*10)
        final float minMutationRate = 0.014F;
        final float maxMutationRate = 0.18F;
        final float copyPercent = 0.011F;
        final int populationSize = 800;
        final int epocheSize = 1_000;

        final GenNet trainedGenNet = GenNetTrainService.runTrainNet(genNet,
                minMutationRate, maxMutationRate, populationSize, epocheSize, copyPercent,
                expectedOutputArrArr, trainInputArrArr, rnd);

        showWinner(trainedGenNet, expectedOutputArrArr, trainInputArrArr);
    }

    private static void showWinner(final GenNet net, final float[][] expectedOutputArrArr, final float[][] trainInputArrArr) {
        System.out.println("Winner:");
        for (int pos = 0; pos < trainInputArrArr.length; pos++) {
            final float[] inArr = trainInputArrArr[pos];
            final float[] outArr = expectedOutputArrArr[pos];
            GenNetService.submitInputValue(net, 0, inArr[0]);
            GenNetService.submitInputValue(net, 1, inArr[1]);
            GenNetService.calc(net);
            System.out.printf("in: %s", Arrays.toString(inArr));
            System.out.printf(" exp.out: %s", Arrays.toString(outArr));
            System.out.print(" out: [");
            for (int outPos = 0; outPos < outArr.length; outPos++) {
                if (outPos > 0) {
                    System.out.print(", ");
                }
                final float outputValue = GenNetService.retrieveOutputValue(net, outPos);
                System.out.printf("%+1.1f", outputValue);
            }
            System.out.println("]");
        }
    }

    private static void showOutput(final GenNet genNet, final float[] expectedOutputArr) {

        for (int outputPos = 0; outputPos < expectedOutputArr.length; outputPos++) {
            final float outputValue = GenNetService.retrieveOutputValue(genNet, outputPos);
            final float expectedOutputValue = expectedOutputArr[outputPos];
            final float diff = (expectedOutputValue - outputValue);
        }
    }

    private static GenNet createNet(final Random rnd) {
        final GenNet genNet = new GenNet();

        final float initBias = 0.0F;

        // Inputs:
        final GenNeuron in1GenNeuron = new GenNeuron(GenNeuron.NeuronType.Input, initBias);
        GenNetService.submitNewNeuron(genNet, in1GenNeuron);

        final GenNeuron in2GenNeuron = new GenNeuron(GenNeuron.NeuronType.Input, initBias);
        GenNetService.submitNewNeuron(genNet, in2GenNeuron);

        // Hidden:
        final GenNeuron hide1GenNeuron = new GenNeuron(GenNeuron.NeuronType.Hidden, initBias);
        GenNetService.submitNewNeuron(genNet, hide1GenNeuron);

        final GenNeuron hide2GenNeuron = new GenNeuron(GenNeuron.NeuronType.Hidden, initBias);
        GenNetService.submitNewNeuron(genNet, hide2GenNeuron);

        final GenNeuron hide3GenNeuron = new GenNeuron(GenNeuron.NeuronType.Hidden, initBias);
        GenNetService.submitNewNeuron(genNet, hide3GenNeuron);

        // Outputs:
        final GenNeuron out1GenNeuron = new GenNeuron(GenNeuron.NeuronType.Output, initBias);
        GenNetService.submitNewNeuron(genNet, out1GenNeuron);

        final GenNeuron out2GenNeuron = new GenNeuron(GenNeuron.NeuronType.Output, initBias);
        GenNetService.submitNewNeuron(genNet, out2GenNeuron);

        final GenNeuron out3GenNeuron = new GenNeuron(GenNeuron.NeuronType.Output, initBias);
        GenNetService.submitNewNeuron(genNet, out3GenNeuron);

        // Synapses:
        // Inputs to Hidden:
        createGenNetSynapse(in1GenNeuron, hide1GenNeuron, rnd);
        createGenNetSynapse(in1GenNeuron, hide2GenNeuron, rnd);
        createGenNetSynapse(in1GenNeuron, hide3GenNeuron, rnd);

        createGenNetSynapse(in2GenNeuron, hide1GenNeuron, rnd);
        createGenNetSynapse(in2GenNeuron, hide2GenNeuron, rnd);
        createGenNetSynapse(in2GenNeuron, hide3GenNeuron, rnd);

        // Hidden to Outputs:
        createGenNetSynapse(hide1GenNeuron, out1GenNeuron, rnd);
        createGenNetSynapse(hide1GenNeuron, out2GenNeuron, rnd);
        createGenNetSynapse(hide1GenNeuron, out3GenNeuron, rnd);

        createGenNetSynapse(hide2GenNeuron, out1GenNeuron, rnd);
        createGenNetSynapse(hide2GenNeuron, out2GenNeuron, rnd);
        createGenNetSynapse(hide2GenNeuron, out3GenNeuron, rnd);

        createGenNetSynapse(hide3GenNeuron, out1GenNeuron, rnd);
        createGenNetSynapse(hide3GenNeuron, out2GenNeuron, rnd);
        createGenNetSynapse(hide3GenNeuron, out3GenNeuron, rnd);

        return genNet;
    }
}
