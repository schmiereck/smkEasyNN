package de.schmiereck.smkEasyNN.genNet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class GenNetTrainService {

    static GenNet runTrainNet(final GenNet genNet, final float mutationRate, final int populationSize,
                              final int epocheSize, final float copyPercent, final float[][] expectedOutputArrArr, final float[][] trainInputArrArr, final Random rnd) {
        return runTrainNet(genNet, 0.0F, mutationRate, populationSize, epocheSize, copyPercent, expectedOutputArrArr, trainInputArrArr, rnd);
    }

    static GenNet runTrainNet(final int[] layerSizeArr, final float minMutationRate, final float maxMutationRate, final int populationSize,
                              final int epocheSize, final float copyPercent, final float[][] expectedOutputArrArr, final float[][] trainInputArrArr, final Random rnd) {
        final List<GenNet> genNetList = new ArrayList<>();

        for (int netPos = 0; netPos < populationSize; netPos++) {
            final GenNet genNet = GenNetService.createNet(layerSizeArr, rnd);
            genNetList.add(genNet);
        }

        return runTrainNet(genNetList, minMutationRate, maxMutationRate, populationSize, epocheSize, copyPercent, expectedOutputArrArr, trainInputArrArr, rnd);
    }

    static GenNet runTrainNet(final GenNet genNet, final float minMutationRate, final float maxMutationRate, final int populationSize,
                              final int epocheSize, final float copyPercent, final float[][] expectedOutputArrArr, final float[][] trainInputArrArr, final Random rnd) {
        final List<GenNet> genNetList = new ArrayList<>();

        for (int netPos = 0; netPos < populationSize; netPos++) {
            //final GenNet mutatedGenNet = createNet(rnd);
            final float mutationRate = calcMutationRate(minMutationRate, maxMutationRate, rnd);
            final GenNet mutatedGenNet = createMutatedNet(genNet, mutationRate, rnd);
            genNetList.add(mutatedGenNet);
        }

        return runTrainNet(genNetList, minMutationRate, maxMutationRate, populationSize, epocheSize, copyPercent, expectedOutputArrArr, trainInputArrArr, rnd);
    }

    static GenNet runTrainNet(final List<GenNet> genNetList, final float minMutationRate, final float maxMutationRate, final int populationSize,
                              final int epocheSize, final float copyPercent, final float[][] expectedOutputArrArr, final float[][] trainInputArrArr, final Random rnd) {
        for (int epochePos = 0; epochePos < epocheSize; epochePos++) {
            System.out.printf("Epoch %8d:", epochePos);

            calcErrorAndSort(genNetList, expectedOutputArrArr, trainInputArrArr);

            genNetList.forEach(mutatedGenNet -> {
                System.out.printf(" %5.3f", mutatedGenNet.error);
            });
            System.out.println();

            if (epochePos >= epocheSize - 1) {
                break;
            }
            calcNextGeneration(genNetList, minMutationRate, maxMutationRate, copyPercent, rnd);
        }
        return genNetList.get(0);
    }

    private static void calcErrorAndSort(final List<GenNet> genNetList, final float[][] expectedOutputArrArr, final float[][] trainInputArrArr) {
        genNetList.forEach(mutatedGenNet -> {
            calcNetInOutError(mutatedGenNet, expectedOutputArrArr, trainInputArrArr);
        });
        genNetList.sort((o1, o2) -> Float.compare(o1.error, o2.error));
    }

    private static void calcNetInOutError(final GenNet genNet, final float[][] expectedOutputArrArr, final float[][] trainInputArrArr) {
        genNet.error = 0.0F;
        //final int pos = rnd.nextInt(4);
        for (int pos = 0; pos < trainInputArrArr.length; pos++) {
            final float[] inArr = trainInputArrArr[pos];
            final float[] outArr = expectedOutputArrArr[pos];
            for (int inputPos = 0; inputPos < inArr.length; inputPos++) {
                GenNetService.submitInputValue(genNet, inputPos, inArr[inputPos]);
            }
            GenNetService.calc(genNet);
            final float error = calcError(genNet, outArr);
            genNet.error += error;
        }
    }

    private static void calcNextGeneration(final List<GenNet> genNetList, final float minMutationRate, final float maxMutationRate, final float copyPercent, final Random rnd) {
        final float mutationRate = calcMutationRate(minMutationRate, maxMutationRate, rnd);
        final int copySize = calcMutationCount(genNetList.size(), copyPercent, 1);
        final int selectSize = genNetList.size() - copySize;

        final List<GenNet> nextGenNetList = new ArrayList<>();

        final List<GenNet> copyGenNetList = genNetList.subList(0, copySize);
        //final List<GraphNetNet> selectGraphNetNetList = graphNetNetList.subList(copySize, graphNetNetList.size());
        //final List<GenNet> selectGenNetList = genNetList.subList(0, genNetList.size());
        final List<GenNet> selectGenNetList = genNetList.subList(0, selectSize);

        nextGenNetList.addAll(copyGenNetList);
        for (int selectCnt = 0; selectCnt < selectSize; selectCnt++) {
            final int netSelectPos = rnd.nextInt(selectCnt + 1);
            //final int netSelectPos = selectCnt;
            final GenNet net = selectGenNetList.get(netSelectPos);
            final GenNet mutatedNet = createMutatedNet(net, mutationRate, rnd);
            nextGenNetList.add(mutatedNet);
        }
        genNetList.clear();
        genNetList.addAll(nextGenNetList);
    }

    private static float calcMutationRate(float minMutationRate, float maxMutationRate, Random rnd) {
        return rnd.nextFloat(minMutationRate, maxMutationRate);
    }

    private static float calcMutationRate2(float minMutationRate, float maxMutationRate, Random rnd) {
        //final float mutationRate = rnd.nextFloat(minMutationRate, maxMutationRate);
        final float mutationRate;
        if (rnd.nextBoolean()) {
            mutationRate = minMutationRate;
        } else {
            mutationRate = maxMutationRate;
        }
        return mutationRate;
    }

    private static float calcError(final GenNet genNet, final float[] expectedOutputArr) {
        float error = 0.0F;

        for (int outputPos = 0; outputPos < expectedOutputArr.length; outputPos++) {
            final float outputValue = GenNetService.retrieveOutputValue(genNet, outputPos);
            final float expectedOutputValue = expectedOutputArr[outputPos];
            final float diff = (expectedOutputValue - outputValue);
            error += diff * diff;
        }
        return error;
    }

    public static GenNet createMutatedNet(final GenNet net, final float mutationRate, final Random rnd) {
        final GenNet mutatedNet = GenNetService.copyNet(net);
        return mutateNet(mutatedNet, mutationRate, rnd);
    }

    public static GenNet mutateNet(final GenNet net, final float mutationRate, final Random rnd) {
        final int mutationCount = calcMutationCount(net, mutationRate);
        for (int pos = 0; pos < mutationCount; pos++) {
            //switch (rnd.nextInt(5)) {
            switch (rnd.nextInt(3)) {
                // Nothing
                case 0 -> {
                }
                // Weight modification
                case 1 -> mutateWeight(net, mutationRate, rnd);
                // Bias modification
                case 2 -> mutateBias(net, mutationRate, rnd);
                // New Synapse
                case 3 -> mutateNewSynapse(net);
                // New Neuron
                case 4 -> mutateNewNeuron(net);
            }
        }
        return net;
    }

    private static int calcMutationCount(final GenNet net, final float percent) {
        return calcMutationCount(net.neuronList.size(), percent, 2);
    }

    public static int calcMutationCount(final int size, final float percent, final int faktor) {
        final int mutationCount = (int)(size * percent);
        return mutationCount == 0 ? 1 : mutationCount * faktor;
    }

    private static void mutateBias(final GenNet mutatedNet, final float mutationRate, final Random rnd) {
        final GenNeuron neuron = mutatedNet.neuronList.get(rnd.nextInt(mutatedNet.neuronList.size()));
        if (neuron.neuronType != GenNeuron.NeuronType.Input) {
            //neuron.bias += rnd.nextFloat(0.1F) - 0.05F;
            neuron.bias += rnd.nextFloat(mutationRate) - (mutationRate / 2.0F);
        }
    }

    private static void mutateWeight(final GenNet mutatedNet, final float mutationRate, final Random rnd) {
        final GenNeuron neuron = mutatedNet.neuronList.get(rnd.nextInt(mutatedNet.neuronList.size()));
        if (Objects.nonNull(neuron.inputSynapseList)) {
            final GenSynapse synapse = neuron.inputSynapseList.get(rnd.nextInt(neuron.inputSynapseList.size()));
            //synapse.weight += rnd.nextFloat(1.0F) - 0.5F;
            //synapse.weight += rnd.nextFloat(0.5F) - 0.25F;
            //synapse.weight += rnd.nextFloat(0.1F) - 0.05F;
            //synapse.weight += rnd.nextFloat(2.0F) - 1.0F;
            synapse.weight += rnd.nextFloat(mutationRate) - (mutationRate / 2.0F);
        }
    }

    private static void mutateNewNeuron(final GenNet mutatedNet) {

    }

    private static void mutateNewSynapse(final GenNet mutatedNet) {

    }
}
