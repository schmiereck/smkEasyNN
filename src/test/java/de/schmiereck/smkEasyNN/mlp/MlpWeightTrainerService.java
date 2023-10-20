package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpService.train;

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

    public static float runTrainRandomWithTrainer(final MlpNet net, final float[][] expectedOutputArrArr, final float[][] trainInputArrArr,
                                                    final float learningRate, final float momentum, final Random rnd,
                                                    final MlpWeightTrainer weightTrainer) {
        float mainOutputMseErrorValue = 0.0F;
        int mainOutputCount = 0;
        for (int expectedResultPos = 0; expectedResultPos < expectedOutputArrArr.length; expectedResultPos++) {
            final int idx = rnd.nextInt(expectedOutputArrArr.length);
            final float[] trainInputArr = trainInputArrArr[idx];
            final float[] expectedOutputArr = expectedOutputArrArr[idx];

            //----------------------------------------------------------------------------------------------------------
            final float[] calcOutputArr = MlpService.run(net, trainInputArr);

            mainOutputMseErrorValue += MlpService.trainNetErrorValues(net, learningRate, momentum, expectedOutputArr);

            //----------------------------------------------------------------------------------------------------------
            //final float[][][] inArrArrArr = initWeightTrainerInput(weightTrainer, net);

            //----------------------------------------------------------------------------------------------------------
            //mainOutputMseErrorValue += train(net, trainInputArr, expectedOutputArr, learningRate, momentum);

            trainNetWeightsWithTrainer(net, learningRate, momentum, weightTrainer);

            //----------------------------------------------------------------------------------------------------------
            mainOutputCount++;
        }
        return mainOutputMseErrorValue / mainOutputCount;
    }

    static void trainNetWeightsWithTrainer(final MlpNet net, final float learningRate, final float momentum,
                                           final MlpWeightTrainer weightTrainer) {
        for (int layerPos = net.layerArr.length - 1; layerPos >= 0; layerPos--) {
            final MlpLayer layer = net.layerArr[layerPos];
            trainLayerWeightsWithTrainer(net, layerPos, layer, learningRate, momentum, weightTrainer);
        }
    }

    public static void trainLayerWeightsWithTrainer(final MlpNet net,
                                                    final int layerPos, final MlpLayer layer,
                                                    final float learningRate, final float momentum,
                                                    final MlpWeightTrainer trainer) {
        final int additionalNeuronSize = calcAdditionalNeuronSize(net.getConfig());

        for (int outputPos = 0; outputPos < layer.neuronArr.length; outputPos++) {
            final MlpNeuron neuron = layer.neuronArr[outputPos];

            final float[] inArr = new float[InArrNeuronSize + (trainer.trainSize + additionalNeuronSize) * InArrSynapseSize];
            initWeightTrainerInputForNeuron(net, layerPos, neuron, inArr);

            final float[] trainerOutputArr = MlpService.run(trainer.trainNet, inArr);

            for (int inputPos = 0; inputPos < neuron.synapseList.size(); inputPos++) {
                final MlpSynapse synapse = neuron.synapseList.get(inputPos);

                //synapse.weight += trainerOutputArr[(inputPos * OutArrSynapseSize) + 0];
                synapse.weight = trainerOutputArr[(inputPos * OutArrSynapseSize) + 0];
                synapse.dweight = trainerOutputArr[(inputPos * OutArrSynapseSize) + 1];
            }
        }
    }

    public static float runTrainRandomNetAndTrainer(final MlpNet net, final float[][] expectedOutputArrArr, final float[][] trainInputArrArr,
                                                    final float learningRate, final float momentum, final Random rnd,
                                                    final MlpWeightTrainer weightTrainer) {
        weightTrainer.trainerMse = 0.0F;
        float mainOutputMseErrorValue = 0.0F;
        int mainOutputCount = 0;
        for (int expectedResultPos = 0; expectedResultPos < expectedOutputArrArr.length; expectedResultPos++) {
            final int idx = rnd.nextInt(expectedOutputArrArr.length);
            final float[] trainInputArr = trainInputArrArr[idx];
            final float[] expectedOutputArr = expectedOutputArrArr[idx];

            //----------------------------------------------------------------------------------------------------------
            final float[] calcOutputArr = MlpService.run(net, trainInputArr);

            mainOutputMseErrorValue += MlpService.trainNetErrorValues(net, learningRate, momentum, expectedOutputArr);

            //----------------------------------------------------------------------------------------------------------
            final float[][][] inArrArrArr = initWeightTrainerInput(weightTrainer, net);

            //----------------------------------------------------------------------------------------------------------
            //mainOutputMseErrorValue += train(net, trainInputArr, expectedOutputArr, learningRate, momentum);

            MlpService.trainNetWeights(net, learningRate, momentum);

            //----------------------------------------------------------------------------------------------------------
            mainOutputCount++;

            trainWeightTrainer(weightTrainer, net, inArrArrArr, learningRate, momentum);
        }
        return mainOutputMseErrorValue / mainOutputCount;
    }

    private static float[][][] initWeightTrainerInput(final MlpWeightTrainer trainer, final MlpNet net) {
        final int additionalNeuronSize = calcAdditionalNeuronSize(net.getConfig());
        final float[][][] inArrArrArr = new float[net.getLayerArr().length][trainer.trainSize][InArrNeuronSize + (trainer.trainSize + additionalNeuronSize) * InArrSynapseSize];

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

    private static void trainWeightTrainer(final MlpWeightTrainer weightTrainer, final MlpNet net,
                                           final float[][][] inArrArrArr, final float learningRate, final float momentum) {
        int trainingCnt = 0;
        for (int layerPos = 0; layerPos < net.getLayerArr().length; layerPos++) {
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

                    expectedOutArr[(synapsePos * OutArrSynapseSize) + 0] = synapse.weight;
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
