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

    public static float runTrainRandom(final MlpNet net, final float[][] expectedOutputArrArr, final float[][] trainInputArrArr,
                                       final float learningRate, final float momentum, final Random rnd,
                                       final MlpWeightTrainer trainer) {
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
            final float[][][] inArrArrArr = new float[net.getLayerArr().length][trainer.trainSize][InArrNeuronSize + (trainer.trainSize + 1) * InArrSynapseSize];

            for (int layerPos = 0; layerPos < net.getLayerArr().length; layerPos++) {
                final MlpLayer layer = net.getLayer(layerPos);

                final float[][] inArrArr = inArrArrArr[layerPos];

                for (int neuronPos = 0; neuronPos < layer.neuronArr.length; neuronPos++) {
                    final MlpNeuron neuron = layer.neuronArr[neuronPos];

                    final float[] inArr = inArrArr[neuronPos];

                    inArr[0] = layerPos / (float)net.getLayerArr().length;
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
            }

            //----------------------------------------------------------------------------------------------------------
            //mainOutputMseErrorValue += train(net, trainInputArr, expectedOutputArr, learningRate, momentum);

            MlpService.trainNetWeights(net, learningRate, momentum);

            //----------------------------------------------------------------------------------------------------------
            mainOutputCount++;

            for (int layerPos = 0; layerPos < net.getLayerArr().length; layerPos++) {
                final MlpLayer layer = net.getLayer(layerPos);

                final float[][] inArrArr = inArrArrArr[layerPos];

                for (int neuronPos = 0; neuronPos < layer.neuronArr.length; neuronPos++) {
                    final MlpNeuron neuron = layer.neuronArr[neuronPos];

                    final float[] inArr = inArrArr[neuronPos];

                    //inArr[0] = layerPos / (float)net.getLayerArr().length;
                    //inArr[1] = neuron.errorValue;
                    //inArr[2] = neuron.outputValue;

                    final int additionalNeuronSize;
                    if (net.getUseAdditionalBiasInput()) {
                        additionalNeuronSize = 1;
                    } else {
                        additionalNeuronSize = 0;
                    }
                    final float[] expectedOutArr = new float[(trainer.trainSize + additionalNeuronSize) * OutArrSynapseSize];

                    for (int synapsePos = 0; synapsePos < neuron.synapseList.size(); synapsePos++) {
                        final MlpSynapse synapse = neuron.synapseList.get(synapsePos);

                        //inArr[InArrNeuronSize + synapsePos * InArrSynapseSize + 3] = synapse.getInput().getInputValue();

                        expectedOutArr[0] = synapse.weight;
                        //expectedOutArr[1] = Objects.nonNull(synapse.getInputError()) ? synapse.getInputError().getErrorValue() : 0.0F;
                        expectedOutArr[1] = synapse.dweight;
                    }

                    final float trainerMse = train(trainer.trainNet, inArr, expectedOutArr, learningRate, momentum);
                    System.out.printf("trainerMse:%.8f\n", trainerMse);
                }
            }
        }
        return mainOutputMseErrorValue / mainOutputCount;
    }
}
