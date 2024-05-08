package de.schmiereck.smkEasyNN.graphNet;

import java.util.*;

public class GraphNetCountMain {

    /**
     * DCS Directed Cyclic Graphs.
     */
    public static void main(String[] args) {
        final Random rnd = new Random();
        final List<GraphNetBrain> graphNetBrainList = new ArrayList<>();

        for (int brainPos = 0; brainPos < 30; brainPos++) {
            final GraphNetBrain graphNetBrain = createBrain(rnd);
            graphNetBrainList.add(graphNetBrain);
        }

        final float[][][] inArrOutArrArr = {
                {
                        { 0, 0 },
                        { 0, 1 },
                        { 1, 0 },
                        { 1, 1 },
                },
                {
                        { 0, 0, 0 },
                        { 0, 0, 1 },
                        { 0, 1, 0 },
                        { 1, 0, 0 },
                }
        };

        final float mutationRate = 0.1F;

        for (int epochePos = 0; epochePos < 10_000; epochePos++) {
            System.out.printf("Epoch %8d:", epochePos);

            graphNetBrainList.forEach(graphNetBrain -> {
                calcBrainInOutError(graphNetBrain, inArrOutArrArr);
                System.out.printf(" %5.3f", graphNetBrain.error);
            });
            System.out.println();

            calcNextGeneration(graphNetBrainList, rnd, mutationRate);
        }
        showWinner(graphNetBrainList.get(0), inArrOutArrArr);
    }

    private static void showWinner(final GraphNetBrain brain, float[][][] inArrOutArrArr) {
        System.out.println("Winner:");
        for (int pos = 0; pos < inArrOutArrArr[0].length; pos++) {
            final float[] inArr = inArrOutArrArr[0][pos];
            final float[] outArr = inArrOutArrArr[1][pos];
            GraphNetService.submitInputValue(brain, 0, inArr[0]);
            GraphNetService.submitInputValue(brain, 1, inArr[1]);
            GraphNetService.calc(brain);
            System.out.printf("in: %s", Arrays.toString(inArr));
            System.out.printf(" exp.out: %s", Arrays.toString(outArr));
            System.out.print(" out: [");
            for (int outPos = 0; outPos < outArr.length; outPos++) {
                if (outPos > 0) {
                    System.out.print(", ");
                }
                final float outputValue = GraphNetService.retrieveOutputValue(brain, outPos);
                System.out.printf("%+1.1f", outputValue);
            }
            System.out.println("]");
        }
    }

    private static void showOutput(final GraphNetBrain graphNetBrain, final float[] expectedOutputArr) {

        for (int outputPos = 0; outputPos < expectedOutputArr.length; outputPos++) {
            final float outputValue = GraphNetService.retrieveOutputValue(graphNetBrain, outputPos);
            final float expectedOutputValue = expectedOutputArr[outputPos];
            final float diff = (expectedOutputValue - outputValue);
        }
    }

    private static void calcBrainInOutError(GraphNetBrain graphNetBrain, float[][][] inArrOutArrArr) {
        graphNetBrain.error = 0.0F;
        //final int pos = rnd.nextInt(4);
        for (int pos = 0; pos < inArrOutArrArr[0].length; pos++) {
            final float[] inArr = inArrOutArrArr[0][pos];
            final float[] outArr = inArrOutArrArr[1][pos];
            GraphNetService.submitInputValue(graphNetBrain, 0, inArr[0]);
            GraphNetService.submitInputValue(graphNetBrain, 1, inArr[1]);
            GraphNetService.calc(graphNetBrain);
            final float error = calcError(graphNetBrain, outArr);
            graphNetBrain.error += error;
        }
    }

    private static void calcNextGeneration(List<GraphNetBrain> graphNetBrainList, Random rnd, float mutationRate) {
        graphNetBrainList.sort((o1, o2) -> Float.compare(o1.error, o2.error));
        final int copySize = (int) (graphNetBrainList.size() * 0.3F);
        final int selectSize = graphNetBrainList.size() - copySize;

        final List<GraphNetBrain> nextGraphNetBrainList = new ArrayList<>();

        final List<GraphNetBrain> copyGraphNetBrainList = graphNetBrainList.subList(0, copySize);
        //final List<GraphNetBrain> selectGraphNetBrainList = graphNetBrainList.subList(copySize, graphNetBrainList.size());
        final List<GraphNetBrain> selectGraphNetBrainList = graphNetBrainList.subList(0, graphNetBrainList.size());

        nextGraphNetBrainList.addAll(copyGraphNetBrainList);
        for (int selectCnt = 0; selectCnt < selectSize; selectCnt++) {
            final int brainSelectPos = rnd.nextInt(selectCnt + 1);
            final GraphNetBrain brain = selectGraphNetBrainList.get(brainSelectPos);
            final GraphNetBrain mutatedBrain = GraphNetService.createMutatedBrain(brain, mutationRate);
            nextGraphNetBrainList.add(mutatedBrain);
        }
        graphNetBrainList.clear();
        graphNetBrainList.addAll(nextGraphNetBrainList);
    }

    private static float calcError(final GraphNetBrain graphNetBrain, final float[] expectedOutputArr) {
        float error = 0.0F;

        for (int outputPos = 0; outputPos < expectedOutputArr.length; outputPos++) {
            final float outputValue = GraphNetService.retrieveOutputValue(graphNetBrain, outputPos);
            final float expectedOutputValue = expectedOutputArr[outputPos];
            final float diff = (expectedOutputValue - outputValue);
            error += diff * diff;
        }
        return error;
    }

    private static GraphNetBrain createBrain(final Random rnd) {
        final GraphNetBrain graphNetBrain = new GraphNetBrain();

        final float initBias = 0.0F;

        // Inputs:
        final GraphNetNeuron in1GraphNetNeuron = new GraphNetNeuron(GraphNetNeuron.NeuronType.Input, initBias);
        GraphNetService.submitNewNeuron(graphNetBrain, in1GraphNetNeuron);

        final GraphNetNeuron in2GraphNetNeuron = new GraphNetNeuron(GraphNetNeuron.NeuronType.Input, initBias);
        GraphNetService.submitNewNeuron(graphNetBrain, in2GraphNetNeuron);

        // Hidden:
        final GraphNetNeuron hide1GraphNetNeuron = new GraphNetNeuron(GraphNetNeuron.NeuronType.Hidden, initBias);
        GraphNetService.submitNewNeuron(graphNetBrain, hide1GraphNetNeuron);

        final GraphNetNeuron hide2GraphNetNeuron = new GraphNetNeuron(GraphNetNeuron.NeuronType.Hidden, initBias);
        GraphNetService.submitNewNeuron(graphNetBrain, hide2GraphNetNeuron);

        final GraphNetNeuron hide3GraphNetNeuron = new GraphNetNeuron(GraphNetNeuron.NeuronType.Hidden, initBias);
        GraphNetService.submitNewNeuron(graphNetBrain, hide3GraphNetNeuron);

        // Outputs:
        final GraphNetNeuron out1GraphNetNeuron = new GraphNetNeuron(GraphNetNeuron.NeuronType.Output, initBias);
        GraphNetService.submitNewNeuron(graphNetBrain, out1GraphNetNeuron);

        final GraphNetNeuron out2GraphNetNeuron = new GraphNetNeuron(GraphNetNeuron.NeuronType.Output, initBias);
        GraphNetService.submitNewNeuron(graphNetBrain, out2GraphNetNeuron);

        final GraphNetNeuron out3GraphNetNeuron = new GraphNetNeuron(GraphNetNeuron.NeuronType.Output, initBias);
        GraphNetService.submitNewNeuron(graphNetBrain, out3GraphNetNeuron);

        // Synapses:
        // Inputs to Hidden:
        createGraphNetSynapse(in1GraphNetNeuron, hide1GraphNetNeuron, rnd);
        createGraphNetSynapse(in1GraphNetNeuron, hide2GraphNetNeuron, rnd);
        createGraphNetSynapse(in1GraphNetNeuron, hide3GraphNetNeuron, rnd);

        createGraphNetSynapse(in2GraphNetNeuron, hide1GraphNetNeuron, rnd);
        createGraphNetSynapse(in2GraphNetNeuron, hide2GraphNetNeuron, rnd);
        createGraphNetSynapse(in2GraphNetNeuron, hide3GraphNetNeuron, rnd);

        // Hidden to Outputs:
        createGraphNetSynapse(hide1GraphNetNeuron, out1GraphNetNeuron, rnd);
        createGraphNetSynapse(hide1GraphNetNeuron, out2GraphNetNeuron, rnd);
        createGraphNetSynapse(hide1GraphNetNeuron, out3GraphNetNeuron, rnd);

        createGraphNetSynapse(hide2GraphNetNeuron, out1GraphNetNeuron, rnd);
        createGraphNetSynapse(hide2GraphNetNeuron, out2GraphNetNeuron, rnd);
        createGraphNetSynapse(hide2GraphNetNeuron, out3GraphNetNeuron, rnd);

        createGraphNetSynapse(hide3GraphNetNeuron, out1GraphNetNeuron, rnd);
        createGraphNetSynapse(hide3GraphNetNeuron, out2GraphNetNeuron, rnd);
        createGraphNetSynapse(hide3GraphNetNeuron, out3GraphNetNeuron, rnd);

        return graphNetBrain;
    }

    private static void createGraphNetSynapse(final GraphNetNeuron inGraphNetNeuron, final GraphNetNeuron outGraphNetNeuron, final Random rnd) {
        final GraphNetSynapse graphNetSynapse = new GraphNetSynapse(inGraphNetNeuron, rnd.nextFloat(1.0F) - 0.5F);
        if (Objects.isNull(outGraphNetNeuron.inputSynapseList)) {
            outGraphNetNeuron.inputSynapseList = new ArrayList<>();
        }
        outGraphNetNeuron.inputSynapseList.add(graphNetSynapse);
    }
}
