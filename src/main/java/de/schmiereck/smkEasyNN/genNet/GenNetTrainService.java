package de.schmiereck.smkEasyNN.genNet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Consumer;

import static de.schmiereck.smkEasyNN.genNet.GenNetService.createGenNetSynapse;
import static de.schmiereck.smkEasyNN.genNet.GenNetService.submitNewNeuron;

public class GenNetTrainService {
    public record GenNetMutateConfig(boolean changeNet) {}
    public static final GenNetMutateConfig DefaultGenNetMutateConfig = new GenNetMutateConfig(false);

    private final static int ChangeNetMutationRate = 4;
    private final static int ChangeNetRemoveNeuronMutationRate = 10;
    private final static int ChangeNetNewNeuronMutationRate = 5;

    static GenNet runTrainNet(final GenNet genNet, final float mutationRate, final int populationSize,
                              final int epocheSize, final float copyPercent,
                              final float[][] expectedOutputArrArr, final float[][] trainInputArrArr,
                              final Random rnd) {
        return runTrainNet(genNet, mutationRate, populationSize, epocheSize, copyPercent,
                DefaultGenNetMutateConfig,
                expectedOutputArrArr,  trainInputArrArr, rnd);
    }

    static GenNet runTrainNet(final GenNet genNet, final float mutationRate, final int populationSize,
                              final int epocheSize, final float copyPercent,
                              final GenNetMutateConfig config,
                              final float[][] expectedOutputArrArr, final float[][] trainInputArrArr,
                              final Random rnd) {
        return runTrainNet(genNet, mutationRate, mutationRate, populationSize, epocheSize, copyPercent, config,
                expectedOutputArrArr, trainInputArrArr,
                rnd);
    }

    static GenNet runTrainNet(final GenNet genNet, final float minMutationRate, final float maxMutationRate, final int populationSize,
                              final int epocheSize, final float copyPercent,
                              final float[][] expectedOutputArrArr, final float[][] trainInputArrArr,
                              final Random rnd) {
        return runTrainNet(genNet, minMutationRate, maxMutationRate, populationSize,
                epocheSize, copyPercent,
                DefaultGenNetMutateConfig,
                expectedOutputArrArr, trainInputArrArr,
                rnd);
    }

    static GenNet runTrainNet(final GenNet genNet, final float minMutationRate, final float maxMutationRate, final int populationSize,
                              final int epocheSize, final float copyPercent,
                              final GenNetMutateConfig config,
                              final float[][] expectedOutputArrArr, final float[][] trainInputArrArr,
                              final Random rnd) {
        return runTrainNet(genNet, minMutationRate, maxMutationRate, populationSize, epocheSize, copyPercent, config,
                expectedOutputArrArr, trainInputArrArr,
                (epochePos) -> System.out.printf("Epoch %8d:", epochePos),
                (error) -> System.out.printf(" %5.3f", error),
                () -> System.out.println(),
                rnd);
    }

    static GenNet runTrainNet(final GenNet genNet, final float mutationRate, final int populationSize,
                              final int epocheSize, final float copyPercent,
                              final GenNetMutateConfig config,
                              final float[][] expectedOutputArrArr, final float[][] trainInputArrArr,
                              final Consumer<Integer> printEpoch,
                              final Consumer<Float> printError,
                              final Runnable printEndline,
                              final Random rnd) {
        return runTrainNet(genNet, mutationRate, mutationRate, populationSize, epocheSize, copyPercent, config,
                expectedOutputArrArr, trainInputArrArr,
                printEpoch, printError, printEndline,
                rnd);
    }

    static GenNet runTrainNet(final int[] layerSizeArr, final float minMutationRate, final float maxMutationRate, final int populationSize,
                              final int epocheSize, final float copyPercent,
                              final GenNetMutateConfig config,
                              final float[][] expectedOutputArrArr, final float[][] trainInputArrArr,
                              final Consumer<Integer> printEpoch,
                              final Consumer<Float> printError,
                              final Runnable printEndline,
                              final Random rnd) {
        final List<GenNet> genNetList = new ArrayList<>();

        for (int netPos = 0; netPos < populationSize; netPos++) {
            final GenNet genNet = GenNetService.createNet(layerSizeArr, rnd);
            genNetList.add(genNet);
        }

        return runTrainNet(genNetList, minMutationRate, maxMutationRate, epocheSize, copyPercent, config,
                expectedOutputArrArr, trainInputArrArr,
                printEpoch, printError, printEndline,
                rnd);
    }

    static GenNet runTrainNet(final GenNet genNet, final float minMutationRate, final float maxMutationRate, final int populationSize,
                              final int epocheSize, final float copyPercent,
                              final GenNetMutateConfig config,
                              final float[][] expectedOutputArrArr, final float[][] trainInputArrArr,
                              final Consumer<Integer> printEpoch,
                              final Consumer<Float> printError,
                              final Runnable printEndline,
                              final Random rnd) {
        final List<GenNet> genNetList = new ArrayList<>();

        for (int netPos = 0; netPos < populationSize; netPos++) {
            //final GenNet mutatedGenNet = createNet(rnd);
            final float mutationRate = calcMutationRate(minMutationRate, maxMutationRate, rnd);
            final GenNet mutatedGenNet = createMutatedNet(genNet, mutationRate, rnd);
            genNetList.add(mutatedGenNet);
        }

        return runTrainNet(genNetList, minMutationRate, maxMutationRate, epocheSize, copyPercent, config,
                expectedOutputArrArr, trainInputArrArr,
                printEpoch, printError, printEndline,
                rnd);
    }

    static GenNet runTrainNet(final List<GenNet> genNetList, final float minMutationRate, final float maxMutationRate,
                              final int epocheSize, final float copyPercent,
                              final GenNetMutateConfig config,
                              final float[][] expectedOutputArrArr, final float[][] trainInputArrArr,
                              final Consumer<Integer> printEpoch,
                              final Consumer<Float> printError,
                              final Runnable printEndline,
                              final Random rnd) {
        for (int epochePos = 0; epochePos < epocheSize; epochePos++) {
            printEpoch.accept(epochePos);

            calcErrorAndSort(genNetList, expectedOutputArrArr, trainInputArrArr);

            genNetList.forEach(mutatedGenNet -> {
                printError.accept(mutatedGenNet.error);
            });
            printEndline.run();

            if (epochePos >= epocheSize - 1) {
                break;
            }
            calcNextGeneration(genNetList, minMutationRate, maxMutationRate, copyPercent, config, rnd);
        }
        return genNetList.get(0);
    }

    private static void calcErrorAndSort(final List<GenNet> genNetList, final float[][] expectedOutputArrArr, final float[][] trainInputArrArr) {
        genNetList.forEach(mutatedGenNet -> {
            calcNetInOutError(mutatedGenNet, expectedOutputArrArr, trainInputArrArr);
        });
        genNetList.sort((o1Net, o2Net) -> Float.compare(o1Net.error, o2Net.error));
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

    private static void calcNextGeneration(final List<GenNet> genNetList,
                                           final float minMutationRate, final float maxMutationRate, final float copyPercent, final GenNetMutateConfig config,
                                           final Random rnd) {
        final float mutationRate = calcMutationRate(minMutationRate, maxMutationRate, rnd);
        final float mutationRate2 = calcMutationRate2(minMutationRate, maxMutationRate, rnd);
        final int copySize = calcMutationCount(genNetList.size(), copyPercent, 1);
        final int selectSize = genNetList.size() - copySize;

        final List<GenNet> nextGenNetList = new ArrayList<>();

        final List<GenNet> copyGenNetList = genNetList.subList(0, copySize);
        //final List<GraphNetNet> selectGraphNetNetList = graphNetNetList.subList(copySize, graphNetNetList.size());
        //final List<GenNet> selectGenNetList = genNetList.subList(0, genNetList.size());
        final List<GenNet> selectGenNetList = genNetList.subList(0, selectSize);

        nextGenNetList.addAll(copyGenNetList);
        for (int selectCnt = 0; selectCnt < selectSize; selectCnt++) {
            //final GenNet net = selectNet(selectGenNetList, selectCnt, rnd);
            final GenNet net = selectNet2(selectGenNetList, selectCnt, selectSize, rnd);
            final float usedMutationRate;
            if (rnd.nextBoolean()) {
                usedMutationRate = mutationRate;
            } else {
                usedMutationRate = mutationRate2;
            }
            final GenNet mutatedNet = createMutatedNet(net, usedMutationRate, config, rnd);
            nextGenNetList.add(mutatedNet);
        }
        genNetList.clear();
        genNetList.addAll(nextGenNetList);
    }

    private static GenNet selectNet(final List<GenNet> selectGenNetList, final int selectCnt, final Random rnd) {
        final int netSelectPos = rnd.nextInt(selectCnt + 1);
        //final int netSelectPos = selectCnt;
        final GenNet net = selectGenNetList.get(netSelectPos);
        return net;
    }

    private static GenNet selectNet2(final List<GenNet> genNetList, final int selectCnt, final int selectSize, final Random rnd) {
        final int maxSelectCnt = (genNetList.size() * (selectCnt + 1)) / selectSize;
        final int netSelectPos = rnd.nextInt(maxSelectCnt);
        //final int netSelectPos = selectCnt;
        final GenNet net = genNetList.get(netSelectPos);
        return net;
    }

    public static float calcMutationRate(final float minMutationRate, final float maxMutationRate, final Random rnd) {
        if (minMutationRate == maxMutationRate) {
            return rnd.nextFloat(maxMutationRate);
        } else {
            return rnd.nextFloat(minMutationRate, maxMutationRate);
        }
    }

    private static float calcMutationRate2(final float minMutationRate, final float maxMutationRate, final Random rnd) {
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

    public static GenNet createCopyNet(final GenNet net) {
        return GenNetService.copyNet(net);
    }

    public static GenNet createMutatedNet(final GenNet net, final float mutationRate, final Random rnd) {
        return createMutatedNet(net, mutationRate, DefaultGenNetMutateConfig, rnd);
    }

    public static GenNet createMutatedNet(final GenNet net, final float mutationRate, final GenNetMutateConfig config, final Random rnd) {
        final GenNet mutatedNet = GenNetService.copyNet(net);
        return mutateNet(mutatedNet, mutationRate, config, rnd);
    }

    public static GenNet mutateNet(final GenNet net, final float mutationRate, final GenNetMutateConfig config, final Random rnd) {
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
            }
        }
        if (config.changeNet()) {
            if (rnd.nextInt(ChangeNetMutationRate) == 0) {
                switch (rnd.nextInt(5)) {
                    // Nothing
                    case 0 -> {
                    }
                    // New Synapse
                    case 1 -> mutateNewSynapse(net, rnd);
                    // New Neuron
                    case 2 -> mutateNewNeuron(net, rnd);
                    // Remove Synapse
                    case 3 -> mutateRemoveSynapse(net, rnd);
                    // Remove Neuron
                    case 4 -> mutateRemoveNeuron(net, rnd);
                }
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
        if (Objects.nonNull(neuron.inputSynapseList) && !neuron.inputSynapseList.isEmpty()) {
            final GenSynapse synapse = neuron.inputSynapseList.get(rnd.nextInt(neuron.inputSynapseList.size()));
            //synapse.weight += rnd.nextFloat(1.0F) - 0.5F;
            //synapse.weight += rnd.nextFloat(0.5F) - 0.25F;
            //synapse.weight += rnd.nextFloat(0.1F) - 0.05F;
            //synapse.weight += rnd.nextFloat(2.0F) - 1.0F;
            synapse.weight += rnd.nextFloat(mutationRate) - (mutationRate / 2.0F);
        }
    }

    private static void mutateNewNeuron(final GenNet mutatedNet, final Random rnd) {
        if (rnd.nextInt(ChangeNetNewNeuronMutationRate) == 0) {
            // Insert new Neuron in existing synapse connection:
            final GenNeuron outNeuron = mutatedNet.neuronList.get(rnd.nextInt(mutatedNet.neuronList.size()));
            if (outNeuron.getNeuronType() != GenNeuron.NeuronType.Input) {
                if (Objects.nonNull(outNeuron.inputSynapseList) && !outNeuron.inputSynapseList.isEmpty()) {
                    final int inSynapsePos = rnd.nextInt(outNeuron.inputSynapseList.size());
                    final GenSynapse inSynapse = outNeuron.inputSynapseList.get(inSynapsePos);
                    final GenNeuron inNeuron = inSynapse.getInGenNeuron();
                    // inNeuron <--inSynapse-- outNeuron

                    final GenNeuron newNeuron = new GenNeuron(GenNeuron.NeuronType.Hidden, 0.0F);
                    submitNewNeuron(mutatedNet, outNeuron.neuronIndex, newNeuron);
                    inSynapse.inGenNeuron = newNeuron;
                    createGenNetSynapse(inNeuron, newNeuron, 1.0F);
                    // inNeuron <--newSynapse-- newNeuron <--inSynapse-- outNeuron
                }
            }
        }
    }

    private static void mutateNewSynapse(final GenNet mutatedNet, final Random rnd) {
        // Insert new Neuron between random Neurons:
        final GenNeuron inNeuron = mutatedNet.neuronList.get(rnd.nextInt(mutatedNet.neuronList.size()));
        final GenNeuron outNeuron = mutatedNet.neuronList.get(
                rnd.nextInt(Math.max(mutatedNet.inputNeuronList.size(), inNeuron.neuronIndex),
                        mutatedNet.neuronList.size()));
        if (outNeuron.getNeuronType() != GenNeuron.NeuronType.Input) {
            if (Objects.isNull(outNeuron.inputSynapseList)) {
                outNeuron.inputSynapseList = new ArrayList<>();
            }
            createGenNetSynapse(inNeuron, outNeuron, 0.0F);
        }
    }

    private static void mutateRemoveNeuron(final GenNet mutatedNet, final Random rnd) {
        if (rnd.nextInt(ChangeNetRemoveNeuronMutationRate) == 0) {
            final int startHiddenNeuronPos = mutatedNet.inputNeuronList.size();
            final int hiddenNeuronSize = mutatedNet.neuronList.size() - startHiddenNeuronPos - mutatedNet.outputNeuronList.size();
            if (hiddenNeuronSize > 0) {
                final int removedNeuronPos = startHiddenNeuronPos + rnd.nextInt(hiddenNeuronSize);
                final GenNeuron removedNeuron = mutatedNet.neuronList.get(removedNeuronPos);
                removedNeuron.inputSynapseList.forEach(inSynapse -> {
                    inSynapse.inGenNeuron = null;
                });
                removedNeuron.inputSynapseList.clear();
                for (int neuronPos = removedNeuronPos + 1; neuronPos < mutatedNet.neuronList.size(); neuronPos++) {
                    final GenNeuron nextNeuron = mutatedNet.neuronList.get(neuronPos);
                    nextNeuron.neuronIndex--;
                    for (int synapsePos = 0; synapsePos < nextNeuron.inputSynapseList.size(); synapsePos++) {
                        final GenSynapse synapse = nextNeuron.inputSynapseList.get(synapsePos);
                        if (synapse.inGenNeuron == removedNeuron) {
                            nextNeuron.inputSynapseList.remove(synapsePos);
                            synapsePos--;
                        }
                    }
                }
            }
        }
    }

    private static void mutateRemoveSynapse(final GenNet mutatedNet, final Random rnd) {
        final GenNeuron outNeuron = mutatedNet.neuronList.get(rnd.nextInt(mutatedNet.neuronList.size()));
        if (Objects.nonNull(outNeuron.inputSynapseList) && !outNeuron.inputSynapseList.isEmpty()) {
            final int inSynapsePos = rnd.nextInt(outNeuron.inputSynapseList.size());
            final GenSynapse inSynapse = outNeuron.inputSynapseList.get(inSynapsePos);
            inSynapse.inGenNeuron = null;
            outNeuron.inputSynapseList.remove(inSynapsePos);
        }
    }
}
