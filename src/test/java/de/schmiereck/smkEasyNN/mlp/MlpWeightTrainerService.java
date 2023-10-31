package de.schmiereck.smkEasyNN.mlp;

import java.util.Objects;
import java.util.Random;

public abstract class MlpWeightTrainerService {
    /**
     * inArr[0] = layerPos / (float)net.getLayerArr().length;
     * inArr[1] = neuron.errorValue;
     * inArr[2] = neuron.outputValue;
     */
    public static final int InArrNeuronSize = 3;

    /**
     * inArr[InArrNeuronSize + synapsePos * InArrSynapseSize + 0] = synapse.weight;
     * inArr[InArrNeuronSize + synapsePos * InArrSynapseSize + 1] = synapse.dweight;
     * inArr[InArrNeuronSize + synapsePos * InArrSynapseSize + 2] = Objects.nonNull(synapse.getInputError()) ? synapse.getInputError().getErrorValue() : 0.0F;
     * inArr[InArrNeuronSize + synapsePos * InArrSynapseSize + 3] = synapse.getInput().getInputValue();
     */
    public static final int InArrSynapseSize = 4;

    /**
     * outArr[0] = synapse.weight;
     * //--outArr[1] = Objects.nonNull(synapse.getInputError()) ? synapse.getInputError().getErrorValue() : 0.0F;--//
     * outArr[1] = synapse.dweight;
     */
    public static final int OutArrSynapseSize = 2;

    public static float runTrainRandomWithTrainerArr(final MlpNet net, final float[][] expectedOutputArrArr, final float[][] trainInputArrArr,
                                                  final float learningRate, final float momentum, final Random rnd,
                                                  final MlpWeightTrainer[] weightTrainerArr) {
        float mainOutputMseErrorValue = 0.0F;
        int mainOutputCount = 0;
        for (int expectedResultPos = 0; expectedResultPos < expectedOutputArrArr.length; expectedResultPos++) {
            final int idx = rnd.nextInt(expectedOutputArrArr.length);
            final float[] trainInputArr = trainInputArrArr[idx];
            final float[] expectedOutputArr = expectedOutputArrArr[idx];

            //----------------------------------------------------------------------------------------------------------
            final float[] calcOutputArr = MlpService.run(net, trainInputArr);

            mainOutputMseErrorValue += MlpService.trainNetErrorValues(net, expectedOutputArr);

            //----------------------------------------------------------------------------------------------------------
            //final float[][][] inArrArrArr = initWeightTrainerInput(weightTrainer, net);

            //----------------------------------------------------------------------------------------------------------
            //mainOutputMseErrorValue += train(net, trainInputArr, expectedOutputArr, learningRate, momentum);

            trainNetWeightsWithTrainerArr(net, learningRate, momentum, weightTrainerArr);

            //----------------------------------------------------------------------------------------------------------
            mainOutputCount++;
        }
        return mainOutputMseErrorValue / mainOutputCount;
    }

    static void trainNetWeightsWithTrainerArr(final MlpNet net, final float learningRate, final float momentum,
                                           final MlpWeightTrainer[] weightTrainerArr) {
        for (int layerPos = net.layerArr.length - 1; layerPos >= 0; layerPos--) {
            final MlpLayer layer = net.layerArr[layerPos];
            final MlpWeightTrainer weightTrainer = weightTrainerArr[layerPos];

            final boolean useTrainerNet;
            if (Objects.nonNull(weightTrainer)) {
                useTrainerNet = true;
            } else {
                useTrainerNet = false;
            }
            if (useTrainerNet) {
                trainLayerWeightsWithTrainer(net, layerPos, layer, weightTrainer);
            } else {
                MlpService.trainLayerWeights(layer, learningRate, momentum);
            }
        }
    }

    public static float runTrainRandomWithTrainer(final MlpNet net, final float[][] expectedOutputArrArr, final float[][] trainInputArrArr,
                                                    final float learningRate, final float momentum, final Random rnd,
                                                    final MlpWeightTrainer weightTrainer, final int trainLayerPos) {
        float mainOutputMseErrorValue = 0.0F;
        int mainOutputCount = 0;
        for (int expectedResultPos = 0; expectedResultPos < expectedOutputArrArr.length; expectedResultPos++) {
            final int idx = rnd.nextInt(expectedOutputArrArr.length);
            final float[] trainInputArr = trainInputArrArr[idx];
            final float[] expectedOutputArr = expectedOutputArrArr[idx];

            //----------------------------------------------------------------------------------------------------------
            final float[] calcOutputArr = MlpService.run(net, trainInputArr);

            mainOutputMseErrorValue += MlpService.trainNetErrorValues(net, expectedOutputArr);

            //----------------------------------------------------------------------------------------------------------
            //final float[][][] inArrArrArr = initWeightTrainerInput(weightTrainer, net);

            //----------------------------------------------------------------------------------------------------------
            //mainOutputMseErrorValue += train(net, trainInputArr, expectedOutputArr, learningRate, momentum);

            trainNetWeightsWithTrainer(net, learningRate, momentum, weightTrainer, trainLayerPos);

            //----------------------------------------------------------------------------------------------------------
            mainOutputCount++;
        }
        return mainOutputMseErrorValue / mainOutputCount;
    }

    static void trainNetWeightsWithTrainer(final MlpNet net, final float learningRate, final float momentum,
                                           final MlpWeightTrainer weightTrainer, final int trainLayerPos) {
        for (int layerPos = net.layerArr.length - 1; layerPos >= 0; layerPos--) {
            final MlpLayer layer = net.layerArr[layerPos];

            final boolean useTrainerNet;
            if (trainLayerPos != -1) {
                if (layerPos == trainLayerPos) {
                    useTrainerNet = true;
                } else {
                    useTrainerNet = false;
                }
            } else {
                useTrainerNet = true;
            }
            if (useTrainerNet) {
                trainLayerWeightsWithTrainer(net, layerPos, layer, weightTrainer);
            } else {
                MlpService.trainLayerWeights(layer, learningRate, momentum);
            }
        }
    }

    public static void trainLayerWeightsWithTrainer(final MlpNet net,
                                                    final int layerPos, final MlpLayer layer,
                                                    final MlpWeightTrainer weightTrainer) {
        final int additionalNeuronSize = calcAdditionalNeuronSize(net.getConfig());

        for (int outputPos = 0; outputPos < layer.neuronArr.length; outputPos++) {
            final MlpNeuron neuron = layer.neuronArr[outputPos];

            final float[] inArr = new float[InArrNeuronSize + (weightTrainer.trainSize + additionalNeuronSize) * InArrSynapseSize];
            initWeightTrainerInputForNeuron(net, layerPos, neuron, inArr);

            final float[] trainerOutputArr = MlpService.run(weightTrainer.trainNet, inArr);

            for (int inputPos = 0; inputPos < neuron.synapseList.size(); inputPos++) {
                final MlpSynapse synapse = neuron.synapseList.get(inputPos);

                if (weightTrainer.useWeightDiff) {
                    synapse.weight += trainerOutputArr[(inputPos * OutArrSynapseSize) + 0];
                } else {
                    synapse.weight = trainerOutputArr[(inputPos * OutArrSynapseSize) + 0];
                }
                synapse.dweight = trainerOutputArr[(inputPos * OutArrSynapseSize) + 1];
            }
        }
    }

    public static float runTrainRandomNetAndTrainerArr(final MlpNet net, final float[][] expectedOutputArrArr, final float[][] trainInputArrArr,
                                                       final float learningRate, final float momentum, final Random rnd,
                                                       final MlpWeightTrainer[] weightTrainerArr,
                                                       final float trainerLearningRate, final float trainerMomentum) {
        for (int trainerPos = 0; trainerPos < weightTrainerArr.length; trainerPos++) {
            weightTrainerArr[trainerPos].trainerMse = 0.0F;
        }
        final int trainerTrainSize = weightTrainerArr[0].trainSize;
        float mainOutputMseErrorValue = 0.0F;
        int mainOutputCount = 0;
        for (int expectedResultPos = 0; expectedResultPos < expectedOutputArrArr.length; expectedResultPos++) {
            final int idx = rnd.nextInt(expectedOutputArrArr.length);
            final float[] trainInputArr = trainInputArrArr[idx];
            final float[] expectedOutputArr = expectedOutputArrArr[idx];

            //----------------------------------------------------------------------------------------------------------
            final float[] calcOutputArr = MlpService.run(net, trainInputArr);

            mainOutputMseErrorValue += MlpService.trainNetErrorValues(net, expectedOutputArr);

            //----------------------------------------------------------------------------------------------------------
            final float[][][] inArrArrArr = initWeightTrainerInput(trainerTrainSize, net);

            //----------------------------------------------------------------------------------------------------------
            //mainOutputMseErrorValue += train(net, trainInputArr, expectedOutputArr, learningRate, momentum);

            MlpService.trainNetWeights(net, learningRate, momentum);

            //----------------------------------------------------------------------------------------------------------
            mainOutputCount++;

            trainWeightTrainerArr(weightTrainerArr, net, inArrArrArr, trainerLearningRate, trainerMomentum);
        }
        return mainOutputMseErrorValue / mainOutputCount;
    }

    public static float runTrainRandomNetAndTrainer(final MlpNet net, final float[][] expectedOutputArrArr, final float[][] trainInputArrArr,
                                                    final float learningRate, final float momentum, final Random rnd,
                                                    final MlpWeightTrainer weightTrainer, final int trainLayerPos) {
        weightTrainer.trainerMse = 0.0F;
        final int trainerTrainSize = weightTrainer.trainSize;
        float mainOutputMseErrorValue = 0.0F;
        int mainOutputCount = 0;
        for (int expectedResultPos = 0; expectedResultPos < expectedOutputArrArr.length; expectedResultPos++) {
            final int idx = rnd.nextInt(expectedOutputArrArr.length);
            final float[] trainInputArr = trainInputArrArr[idx];
            final float[] expectedOutputArr = expectedOutputArrArr[idx];

            //----------------------------------------------------------------------------------------------------------
            final float[] calcOutputArr = MlpService.run(net, trainInputArr);

            mainOutputMseErrorValue += MlpService.trainNetErrorValues(net, expectedOutputArr);

            //----------------------------------------------------------------------------------------------------------
            final float[][][] inArrArrArr = initWeightTrainerInput(trainerTrainSize, net);

            //----------------------------------------------------------------------------------------------------------
            //mainOutputMseErrorValue += train(net, trainInputArr, expectedOutputArr, learningRate, momentum);

            MlpService.trainNetWeights(net, learningRate, momentum);

            //----------------------------------------------------------------------------------------------------------
            mainOutputCount++;

            trainWeightTrainer(weightTrainer, net, inArrArrArr, trainLayerPos);
        }
        return mainOutputMseErrorValue / mainOutputCount;
    }

    private static float[][][] initWeightTrainerInput(final int trainerTrainSize, final MlpNet net) {
        final int maxNeuronSize = calcMaxNeuronSize(net);
        final int additionalNeuronSize = calcAdditionalNeuronSize(net.getConfig());
        final float[][][] inArrArrArr = new float[net.getLayerArr().length]
                [maxNeuronSize]
                [InArrNeuronSize + (trainerTrainSize + additionalNeuronSize) * InArrSynapseSize];

        for (int layerPos = 0; layerPos < net.getLayerArr().length; layerPos++) {
            final MlpLayer layer = net.getLayer(layerPos);

            final float[][] inArrArr = inArrArrArr[layerPos];

            for (int neuronPos = 0; neuronPos < layer.neuronArr.length; neuronPos++) {
                final MlpNeuron neuron = layer.neuronArr[neuronPos];

                final float[] inArr = inArrArr[neuronPos];
                initWeightTrainerInputForNeuron(net, layerPos, neuron, inArr);
            }
        }
        return inArrArrArr;
    }

    private static int calcMaxNeuronSize(final MlpNet net) {
        int maxNeuronSize = 0;

        for (int layerPos = 0; layerPos < net.getLayerArr().length; layerPos++) {
            final MlpLayer layer = net.getLayer(layerPos);

            if (layer.neuronArr.length > maxNeuronSize) {
                maxNeuronSize = layer.neuronArr.length;
            }
        }

        return maxNeuronSize;
    }

    private static void initWeightTrainerInputForNeuron(final MlpNet net, final int layerPos, final MlpNeuron neuron, final float[] inArr) {
        inArr[0] = layerPos / (float) net.getLayerArr().length;
        inArr[1] = neuron.errorValue;
        inArr[2] = neuron.outputValue;

        for (int synapsePos = 0; synapsePos < neuron.synapseList.size(); synapsePos++) {
            final MlpSynapse synapse = neuron.synapseList.get(synapsePos);

            inArr[InArrNeuronSize + synapsePos * InArrSynapseSize + 0] = synapse.weight;
            inArr[InArrNeuronSize + synapsePos * InArrSynapseSize + 1] = synapse.dweight;
            inArr[InArrNeuronSize + synapsePos * InArrSynapseSize + 2] = Objects.nonNull(synapse.getInputError()) ? synapse.getInputError().getErrorValue() : 0.0F;
            inArr[InArrNeuronSize + synapsePos * InArrSynapseSize + 3] = synapse.getInput().getInputValue();
        }
    }

    private static void trainWeightTrainerArr(final MlpWeightTrainer[] weightTrainerArr, final MlpNet net,
                                           final float[][][] inArrArrArr, final float trainerLearningRate, final float trainerMomentum) {
        for (int layerPos = 0; layerPos < net.getLayerArr().length; layerPos++) {
            final MlpLayer layer = net.getLayer(layerPos);
            final MlpWeightTrainer weightTrainer = weightTrainerArr[layerPos];

            final float[][] inArrArr = inArrArrArr[layerPos];

            int trainingCnt = 0;

            for (int neuronPos = 0; neuronPos < layer.neuronArr.length; neuronPos++) {
                final MlpNeuron neuron = layer.neuronArr[neuronPos];

                final float[] inArr = inArrArr[neuronPos];

                //inArr[0] = layerPos / (float)net.getLayerArr().length;
                //inArr[1] = neuron.errorValue;
                //inArr[2] = neuron.outputValue;

                final int additionalNeuronSize = calcAdditionalNeuronSize(net.getConfig());
                final float[] expectedOutArr = new float[(weightTrainer.trainSize + additionalNeuronSize) * OutArrSynapseSize];

                for (int synapsePos = 0; synapsePos < neuron.synapseList.size(); synapsePos++) {
                    final MlpSynapse synapse = neuron.synapseList.get(synapsePos);

                    //inArr[InArrNeuronSize + synapsePos * InArrSynapseSize + 3] = synapse.getInput().getInputValue();

                    if (weightTrainer.useWeightDiff) {
                        expectedOutArr[(synapsePos * OutArrSynapseSize) + 0] = synapse.weight - inArr[InArrNeuronSize + synapsePos * InArrSynapseSize + 0];
                    } else {
                        expectedOutArr[(synapsePos * OutArrSynapseSize) + 0] = synapse.weight;
                    }
                    //expectedOutArr[1] = Objects.nonNull(synapse.getInputError()) ? synapse.getInputError().getErrorValue() : 0.0F;
                    expectedOutArr[(synapsePos * OutArrSynapseSize) + 1] = synapse.dweight;
                }

                //weightTrainer.trainerMse += train(weightTrainer.trainNet, inArr, expectedOutArr, learningRate, momentum);
                final float[] calcOutputArr = MlpService.run(weightTrainer.trainNet, inArr);

                // Accept the unknown output as expected and do not train it:
                for (int pos = (neuron.synapseList.size() * OutArrSynapseSize); pos < expectedOutArr.length; pos++) {
                    expectedOutArr[pos] = calcOutputArr[pos];
                    //expectedOutArr[pos] = 0.0F;
                }

                weightTrainer.trainerMse += MlpService.trainWithOutput(weightTrainer.trainNet,
                        expectedOutArr, calcOutputArr,
                        trainerLearningRate, trainerMomentum);//1learningRate, momentum);
                trainingCnt++;
            }
            weightTrainer.trainerMse /= trainingCnt;
        }
    }

    private static void trainWeightTrainer(final MlpWeightTrainer weightTrainer, final MlpNet net,
                                           final float[][][] inArrArrArr, final int trainLayerPos) {
        int trainingCnt = 0;

        final int startTrainLayerPos;
        final int endTrainLayerPos;
        if (trainLayerPos == -1) {
            startTrainLayerPos = 0;
            endTrainLayerPos = net.getLayerArr().length;
        } else {
            startTrainLayerPos = trainLayerPos;
            endTrainLayerPos = trainLayerPos + 1;
        }

        for (int layerPos = startTrainLayerPos; layerPos < endTrainLayerPos; layerPos++) {
            final MlpLayer layer = net.getLayer(layerPos);

            final float[][] inArrArr = inArrArrArr[layerPos];

            for (int neuronPos = 0; neuronPos < layer.neuronArr.length; neuronPos++) {
                final MlpNeuron neuron = layer.neuronArr[neuronPos];

                final float[] inArr = inArrArr[neuronPos];

                //inArr[0] = layerPos / (float)net.getLayerArr().length;
                //inArr[1] = neuron.errorValue;
                //inArr[2] = neuron.outputValue;

                final int additionalNeuronSize = calcAdditionalNeuronSize(net.getConfig());
                final float[] expectedOutArr = new float[(weightTrainer.trainSize + additionalNeuronSize) * OutArrSynapseSize];

                for (int synapsePos = 0; synapsePos < neuron.synapseList.size(); synapsePos++) {
                    final MlpSynapse synapse = neuron.synapseList.get(synapsePos);

                    //inArr[InArrNeuronSize + synapsePos * InArrSynapseSize + 3] = synapse.getInput().getInputValue();

                    if (weightTrainer.useWeightDiff) {
                        expectedOutArr[(synapsePos * OutArrSynapseSize) + 0] = synapse.weight - inArr[InArrNeuronSize + synapsePos * InArrSynapseSize + 0];
                    } else {
                        expectedOutArr[(synapsePos * OutArrSynapseSize) + 0] = synapse.weight;
                    }
                    //expectedOutArr[1] = Objects.nonNull(synapse.getInputError()) ? synapse.getInputError().getErrorValue() : 0.0F;
                    expectedOutArr[(synapsePos * OutArrSynapseSize) + 1] = synapse.dweight;
                }

                //weightTrainer.trainerMse += train(weightTrainer.trainNet, inArr, expectedOutArr, learningRate, momentum);
                final float[] calcOutputArr = MlpService.run(weightTrainer.trainNet, inArr);

                // Accept the unknown output as expected and do not train it:
                for (int pos = (neuron.synapseList.size() * OutArrSynapseSize); pos < expectedOutArr.length; pos++) {
                    //expectedOutArr[pos] = calcOutputArr[pos];
                    expectedOutArr[pos] = 0.0F;
                }

                weightTrainer.trainerMse += MlpService.trainWithOutput(weightTrainer.trainNet, expectedOutArr, calcOutputArr, 0.01F, 0.6F);//1learningRate, momentum);
                trainingCnt++;
            }
        }
        weightTrainer.trainerMse /= trainingCnt;
    }

    public static int calcAdditionalNeuronSize(final MlpConfiguration config) {
        final int additionalNeuronSize;
        if (config.getUseAdditionalBiasInput()) {
            additionalNeuronSize = 1;
        } else {
            additionalNeuronSize = 0;
        }
        return additionalNeuronSize;
    }
}
