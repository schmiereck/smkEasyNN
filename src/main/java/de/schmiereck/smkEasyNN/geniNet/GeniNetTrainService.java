package de.schmiereck.smkEasyNN.geniNet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static de.schmiereck.smkEasyNN.geniNet.GeniNetService.*;

public class GeniNetTrainService {
    public record GeniNetMutateConfig(boolean changeNet) {}
    public static final GeniNetMutateConfig DefaultGeniNetMutateConfig = new GeniNetMutateConfig(false);

    private final static int ChangeNetMutationRate = 4;
    private final static int ChangeNetRemoveNeuronMutationRate = 10;
    private final static int ChangeNetNewNeuronMutationRate = 5;

    static GeniNet runTrainNet(final GeniNet geniNet, final float mutationRate, final int populationSize,
                              final int epocheSize, final float copyPercent,
                              final int[][] expectedOutputArrArr, final int[][] trainInputArrArr,
                              final Random rnd) {
        return runTrainNet(geniNet, mutationRate, populationSize, epocheSize, copyPercent,
                DefaultGeniNetMutateConfig,
                expectedOutputArrArr,  trainInputArrArr, rnd);
    }

    static GeniNet runTrainNet(final GeniNet geniNet, final float mutationRate, final int populationSize,
                              final int epocheSize, final float copyPercent,
                              final GeniNetMutateConfig config,
                              final int[][] expectedOutputArrArr, final int[][] trainInputArrArr,
                              final Random rnd) {
        return runTrainNet(geniNet, mutationRate, mutationRate, populationSize, epocheSize, copyPercent, config,
                expectedOutputArrArr, trainInputArrArr,
                rnd);
    }

    static GeniNet runTrainNet(final GeniNet geniNet, final float minMutationRate, final float maxMutationRate, final int populationSize,
                              final int epocheSize, final float copyPercent,
                              final int[][] expectedOutputArrArr, final int[][] trainInputArrArr,
                              final Random rnd) {
        return runTrainNet(geniNet, minMutationRate, maxMutationRate, populationSize,
                epocheSize, copyPercent,
                DefaultGeniNetMutateConfig,
                expectedOutputArrArr, trainInputArrArr,
                rnd);
    }

    static GeniNet runTrainNet(final GeniNet geniNet, final float minMutationRate, final float maxMutationRate, final int populationSize,
                              final int epocheSize, final float copyPercent,
                              final GeniNetMutateConfig config,
                              final int[][] expectedOutputArrArr, final int[][] trainInputArrArr,
                              final Random rnd) {
        return runTrainNet(geniNet, minMutationRate, maxMutationRate, populationSize, epocheSize, copyPercent, config,
                expectedOutputArrArr, trainInputArrArr,
                (epochePos, geniNetList) -> System.out.printf("Epoch %8d:", epochePos),
                (error) -> System.out.printf(" %3d", error),
                () -> System.out.println(),
                rnd);
    }

    static GeniNet runTrainNet(final GeniNet geniNet, final float mutationRate, final int populationSize,
                              final int epocheSize, final float copyPercent,
                              final GeniNetMutateConfig config,
                              final int[][] expectedOutputArrArr, final int[][] trainInputArrArr,
                              final BiConsumer<Integer, List<GeniNet>> printEpoch,
                              final Consumer<Long> printError,
                              final Runnable printEndline,
                              final Random rnd) {
        return runTrainNet(geniNet, mutationRate, mutationRate, populationSize, epocheSize, copyPercent, config,
                expectedOutputArrArr, trainInputArrArr,
                printEpoch, printError, printEndline,
                rnd);
    }

    static GeniNet runTrainNet(final int[] layerSizeArr, final float minMutationRate, final float maxMutationRate, final int populationSize,
                              final int epocheSize, final float copyPercent,
                              final GeniNetMutateConfig config,
                              final int[][] expectedOutputArrArr, final int[][] trainInputArrArr,
                              final BiConsumer<Integer, List<GeniNet>> printEpoch,
                              final Consumer<Long> printError,
                              final Runnable printEndline,
                              final Random rnd) {
        final List<GeniNet> geniNetList = new ArrayList<>();

        for (int netPos = 0; netPos < populationSize; netPos++) {
            final GeniNet geniNet = GeniNetService.createNet(layerSizeArr, rnd);
            geniNetList.add(geniNet);
        }

        return runTrainNet(geniNetList, minMutationRate, maxMutationRate, epocheSize, copyPercent, config,
                expectedOutputArrArr, trainInputArrArr,
                printEpoch, printError, printEndline,
                rnd);
    }

    static GeniNet runTrainNet(final GeniNet geniNet, final float minMutationRate, final float maxMutationRate, final int populationSize,
                              final int epocheSize, final float copyPercent,
                              final GeniNetMutateConfig config,
                              final int[][] expectedOutputArrArr, final int[][] trainInputArrArr,
                              final BiConsumer<Integer, List<GeniNet>> printEpoch,
                              final Consumer<Long> printError,
                              final Runnable printEndline,
                              final Random rnd) {
        final List<GeniNet> geniNetList = new ArrayList<>();

        for (int netPos = 0; netPos < populationSize; netPos++) {
            //final GeniNet mutatedGeniNet = createNet(rnd);
            final float mutationRate = calcMutationRate(minMutationRate, maxMutationRate, rnd);
            final GeniNet mutatedGeniNet = createMutatedNet(geniNet, mutationRate, config, rnd);
            geniNetList.add(mutatedGeniNet);
        }

        return runTrainNet(geniNetList, minMutationRate, maxMutationRate, epocheSize, copyPercent, config,
                expectedOutputArrArr, trainInputArrArr,
                printEpoch, printError, printEndline,
                rnd);
    }

    static GeniNet runTrainNet(final List<GeniNet> geniNetList, final float minMutationRate, final float maxMutationRate,
                              final int epocheSize, final float copyPercent,
                              final GeniNetMutateConfig config,
                              final int[][] expectedOutputArrArr, final int[][] trainInputArrArr,
                              final BiConsumer<Integer, List<GeniNet>> printEpoch,
                              final Consumer<Long> printError,
                              final Runnable printEndline,
                              final Random rnd) {
        for (int epochePos = 0; epochePos < epocheSize; epochePos++) {
            printEpoch.accept(epochePos, geniNetList);

            calcErrorAndSort(geniNetList, expectedOutputArrArr, trainInputArrArr);

            geniNetList.forEach(mutatedGeniNet -> {
                printError.accept(mutatedGeniNet.error);
            });
            printEndline.run();

            if (epochePos >= epocheSize - 1) {
                break;
            }
            calcNextGeneration(geniNetList, minMutationRate, maxMutationRate, copyPercent, config, rnd);
        }
        return geniNetList.get(0);
    }

    private static void calcErrorAndSort(final List<GeniNet> geniNetList, final int[][] expectedOutputArrArr, final int[][] trainInputArrArr) {
        geniNetList.forEach(mutatedGeniNet -> {
            calcNetInOutError(mutatedGeniNet, expectedOutputArrArr, trainInputArrArr);
        });
        geniNetList.sort((o1Net, o2Net) -> Long.compare(o1Net.error, o2Net.error));
    }

    private static void calcNetInOutError(final GeniNet geniNet, final int[][] expectedOutputArrArr, final int[][] trainInputArrArr) {
        geniNet.error = VALUE_0;
        //final int pos = rnd.nextInt(4);
        for (int pos = 0; pos < trainInputArrArr.length; pos++) {
            final int[] inArr = trainInputArrArr[pos];
            final int[] outArr = expectedOutputArrArr[pos];
            for (int inputPos = 0; inputPos < inArr.length; inputPos++) {
                GeniNetService.submitInputValue(geniNet, inputPos, inArr[inputPos]);
            }
            GeniNetService.calc(geniNet);
            final long error = calcError(geniNet, outArr);
            geniNet.error += error;
        }
    }

    private static void calcNextGeneration(final List<GeniNet> geniNetList,
                                           final float minMutationRate, final float maxMutationRate, final float copyPercent, final GeniNetMutateConfig config,
                                           final Random rnd) {
        final float mutationRate = calcMutationRate(minMutationRate, maxMutationRate, rnd);
        final float mutationRate2 = calcMutationRate2(minMutationRate, maxMutationRate, rnd);
        final int copySize = calcMutationCount(geniNetList.size(), copyPercent, 1);
        //final int scipSize = copySize * 2;
        final int selectSize = geniNetList.size() - (copySize);
        //final int selectSize = geniNetList.size() - (copySize + scipSize);

        final List<GeniNet> nextGeniNetList = new ArrayList<>();

        final List<GeniNet> copyGeniNetList = geniNetList.subList(0, copySize);
        //final List<GeniNet> copyGeniNetList = geniNetList.subList(scipSize, scipSize + copySize + 1);

        final List<GeniNet> selectGeniNetList = geniNetList.subList(0, selectSize);
        //final List<GeniNet> selectGeniNetList = geniNetList.subList(scipSize, scipSize + selectSize + 1);
        final int selectSize2 = selectGeniNetList.size();

        nextGeniNetList.addAll(copyGeniNetList);

        for (int selectCnt = 0; selectCnt < selectSize2; selectCnt++) {
            //final GeniNet net = selectNet(selectGeniNetList, selectCnt, rnd);
            final GeniNet net = selectNet2(selectGeniNetList, selectCnt, selectSize2, rnd);
            final float usedMutationRate;
            if (rnd.nextBoolean()) {
                usedMutationRate = mutationRate;
            } else {
                usedMutationRate = mutationRate2;
            }
            final GeniNet mutatedNet = createMutatedNet(net, usedMutationRate, config, rnd);
            nextGeniNetList.add(mutatedNet);
        }
        geniNetList.clear();
        geniNetList.addAll(nextGeniNetList);
    }

    private static GeniNet selectNet(final List<GeniNet> selectGeniNetList, final int selectCnt, final Random rnd) {
        final int netSelectPos = rnd.nextInt(selectCnt + 1);
        //final int netSelectPos = selectCnt;
        final GeniNet net = selectGeniNetList.get(netSelectPos);
        return net;
    }

    private static GeniNet selectNet2(final List<GeniNet> geniNetList, final int selectCnt, final int selectSize, final Random rnd) {
        final int maxSelectCnt = (geniNetList.size() * (selectCnt + 1)) / selectSize;
        final int netSelectPos = rnd.nextInt(maxSelectCnt);
        //final int netSelectPos = selectCnt;
        final GeniNet net = geniNetList.get(netSelectPos);
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

    private static long calcError(final GeniNet geniNet, final int[] expectedOutputArr) {
        long error = VALUE_0;

        for (int outputPos = 0; outputPos < expectedOutputArr.length; outputPos++) {
            final int outputValue = GeniNetService.retrieveOutputValue(geniNet, outputPos);
            final int expectedOutputValue = expectedOutputArr[outputPos];
            final long diff = (expectedOutputValue - outputValue);
            //error += diff * diff;
            //long err =  (diff * diff);
            long err =  Math.min(diff * diff, Long.MAX_VALUE / VALUE_MAX);
            //error += err / VALUE_MAX;
            error += err;
        }
        //return error / expectedOutputArr.length;
        return error;
    }

    public static GeniNet createCopyNet(final GeniNet net) {
        return GeniNetService.copyNet(net);
    }

    public static GeniNet createMutatedNet(final GeniNet net, final float mutationRate, final Random rnd) {
        return createMutatedNet(net, mutationRate, DefaultGeniNetMutateConfig, rnd);
    }

    public static GeniNet createMutatedNet(final GeniNet net, final float mutationRate, final GeniNetMutateConfig config, final Random rnd) {
        final GeniNet mutatedNet = GeniNetService.copyNet(net);
        return mutateNet(mutatedNet, mutationRate, config, rnd);
    }

    public static GeniNet mutateNet(final GeniNet net, final float mutationRate, final GeniNetMutateConfig config, final Random rnd) {
        final int mutationCount = calcMutationCount(net, mutationRate);
        for (int pos = 0; pos < mutationCount; pos++) {
            if (rnd.nextInt(mutationCount) > 0) {
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

    private static int calcMutationCount(final GeniNet net, final float percent) {
        return calcMutationCount(net.neuronList.size(), percent, 2);
    }

    public static int calcMutationCount(final int size, final float percent, final int faktor) {
        final int mutationCount = (int)(size * percent);
        return mutationCount == 0 ? 1 : mutationCount * faktor;
    }

    private static void mutateBias(final GeniNet mutatedNet, final float mutationRate, final Random rnd) {
        final GeniNeuron neuron = mutatedNet.neuronList.get(rnd.nextInt(mutatedNet.neuronList.size()));
        if (neuron.neuronType != GeniNeuron.NeuronType.Input) {
            //neuron.bias += rnd.nextFloat(0.1F) - 0.05F;
            final int biasDiff = rnd.nextInt(calcPercentValue(mutationRate) + 1) - (calcPercentValue(mutationRate) / 2);
            neuron.bias += biasDiff;
        }
    }

    private static void mutateWeight(final GeniNet mutatedNet, final float mutationRate, final Random rnd) {
        final GeniNeuron neuron = mutatedNet.neuronList.get(rnd.nextInt(mutatedNet.neuronList.size()));
        if (Objects.nonNull(neuron.inputSynapseList) && !neuron.inputSynapseList.isEmpty()) {
            final GeniSynapse synapse = neuron.inputSynapseList.get(rnd.nextInt(neuron.inputSynapseList.size()));
            //synapse.weight += rnd.nextFloat(1.0F) - 0.5F;
            //synapse.weight += rnd.nextFloat(0.5F) - 0.25F;
            //synapse.weight += rnd.nextFloat(0.1F) - 0.05F;
            //synapse.weight += rnd.nextFloat(2.0F) - 1.0F;
            //synapse.weight += rnd.nextFloat(mutationRate) - (mutationRate / 2.0F);
            final int weightDiff = rnd.nextInt(calcPercentValue(mutationRate) + 1) - (calcPercentValue(mutationRate) / 2);
            synapse.weight += weightDiff;
        }
    }

    private static void mutateNewNeuron(final GeniNet mutatedNet, final Random rnd) {
        if (rnd.nextInt(ChangeNetNewNeuronMutationRate) == 0) {
            // Insert new Neuron in existing synapse connection:
            final GeniNeuron outNeuron = mutatedNet.neuronList.get(rnd.nextInt(mutatedNet.neuronList.size()));
            if (outNeuron.getNeuronType() != GeniNeuron.NeuronType.Input) {
                if (Objects.nonNull(outNeuron.inputSynapseList) && !outNeuron.inputSynapseList.isEmpty()) {
                    final int inSynapsePos = rnd.nextInt(outNeuron.inputSynapseList.size());
                    final GeniSynapse inSynapse = outNeuron.inputSynapseList.get(inSynapsePos);
                    final GeniNeuron inNeuron = inSynapse.getInGenNeuron();
                    // inNeuron <--inSynapse-- outNeuron

                    final GeniNeuron newNeuron = new GeniNeuron(GeniNeuron.NeuronType.Hidden, VALUE_0);
                    submitNewNeuron(mutatedNet, outNeuron.neuronIndex, newNeuron);
                    inSynapse.inGeniNeuron = newNeuron;
                    createGeniNetSynapse(inNeuron, newNeuron, VALUE_MAX);
                    // inNeuron <--newSynapse-- newNeuron <--inSynapse-- outNeuron
                }
            }
        }
    }

    private static void mutateNewSynapse(final GeniNet mutatedNet, final Random rnd) {
        // Insert new Neuron between random Neurons:
        final GeniNeuron inNeuron = mutatedNet.neuronList.get(rnd.nextInt(mutatedNet.neuronList.size()));
        final GeniNeuron outNeuron = mutatedNet.neuronList.get(
                rnd.nextInt(Math.max(mutatedNet.inputNeuronList.size(), inNeuron.neuronIndex),
                        mutatedNet.neuronList.size()));
        if (outNeuron.getNeuronType() != GeniNeuron.NeuronType.Input) {
            if (Objects.isNull(outNeuron.inputSynapseList)) {
                outNeuron.inputSynapseList = new ArrayList<>();
            }
            createGeniNetSynapse(inNeuron, outNeuron, VALUE_0);
        }
    }

    private static void mutateRemoveNeuron(final GeniNet mutatedNet, final Random rnd) {
        if (rnd.nextInt(ChangeNetRemoveNeuronMutationRate) == 0) {
            final int startHiddenNeuronPos = mutatedNet.inputNeuronList.size();
            final int hiddenNeuronSize = mutatedNet.neuronList.size() - startHiddenNeuronPos - mutatedNet.outputNeuronList.size();
            if (hiddenNeuronSize > 0) {
                final int removedNeuronPos = startHiddenNeuronPos + rnd.nextInt(hiddenNeuronSize);
                final GeniNeuron removedNeuron = mutatedNet.neuronList.get(removedNeuronPos);
                removedNeuron.inputSynapseList.forEach(inSynapse -> {
                    inSynapse.inGeniNeuron = null;
                });
                removedNeuron.inputSynapseList.clear();
                for (int neuronPos = removedNeuronPos + 1; neuronPos < mutatedNet.neuronList.size(); neuronPos++) {
                    final GeniNeuron nextNeuron = mutatedNet.neuronList.get(neuronPos);
                    nextNeuron.neuronIndex--;
                    for (int synapsePos = 0; synapsePos < nextNeuron.inputSynapseList.size(); synapsePos++) {
                        final GeniSynapse synapse = nextNeuron.inputSynapseList.get(synapsePos);
                        if (synapse.inGeniNeuron == removedNeuron) {
                            nextNeuron.inputSynapseList.remove(synapsePos);
                            synapsePos--;
                        }
                    }
                }
            }
        }
    }

    private static void mutateRemoveSynapse(final GeniNet mutatedNet, final Random rnd) {
        final GeniNeuron outNeuron = mutatedNet.neuronList.get(rnd.nextInt(mutatedNet.neuronList.size()));
        if (Objects.nonNull(outNeuron.inputSynapseList) && !outNeuron.inputSynapseList.isEmpty()) {
            final int inSynapsePos = rnd.nextInt(outNeuron.inputSynapseList.size());
            final GeniSynapse inSynapse = outNeuron.inputSynapseList.get(inSynapsePos);
            inSynapse.inGeniNeuron = null;
            outNeuron.inputSynapseList.remove(inSynapsePos);
        }
    }
}
