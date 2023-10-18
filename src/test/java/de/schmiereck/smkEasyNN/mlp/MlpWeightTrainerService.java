package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpService.train;

import java.util.Objects;
import java.util.Random;

public abstract class MlpWeightTrainerService {

    public static float runTrainRandom(final MlpNet net, final float[][] expectedOutputArrArr, final float[][] trainInputArrArr,
                                       final float learningRate, final float momentum, final Random rnd,
                                       final MlpWeightTrainer trainer) {
        float mainOutputMseErrorValue = 0.0F;
        int mainOutputCount = 0;
        for (int expectedResultPos = 0; expectedResultPos < expectedOutputArrArr.length; expectedResultPos++) {
            int idx = rnd.nextInt(expectedOutputArrArr.length);

            final float[][][] inArrArrArr = new float[net.getLayerArr().length][trainer.trainSize][3 + (trainer.trainSize + 1) * 3];

            for (int layerPos = 0; layerPos < net.getLayerArr().length; layerPos++) {
                final MlpLayer layer = net.getLayer(layerPos);

                final float[][] inArrArr = inArrArrArr[layerPos];

                for (int neuronPos = 0; neuronPos < layer.neuronArr.length; neuronPos++) {
                    final MlpNeuron neuron = layer.neuronArr[neuronPos];

                    final float[] inArr = inArrArr[neuronPos];

                    for (int synapsePos = 0; synapsePos < neuron.synapseList.size(); synapsePos++) {
                        final MlpSynapse synapse = neuron.synapseList.get(synapsePos);

                        inArr[3 + synapsePos * 3 + 0] = synapse.weight;
                        inArr[3 + synapsePos * 3 + 1] = Objects.nonNull(synapse.getInputError()) ? synapse.getInputError().getErrorValue() : 0.0F;
                    }
                }
            }
            mainOutputMseErrorValue += train(net, trainInputArrArr[idx], expectedOutputArrArr[idx], learningRate, momentum);
            mainOutputCount++;

            for (int layerPos = 0; layerPos < net.getLayerArr().length; layerPos++) {
                final MlpLayer layer = net.getLayer(layerPos);

                final float[][] inArrArr = inArrArrArr[layerPos];

                for (int neuronPos = 0; neuronPos < layer.neuronArr.length; neuronPos++) {
                    final MlpNeuron neuron = layer.neuronArr[neuronPos];

                    final float[] inArr = inArrArr[neuronPos];

                    inArr[0] = layerPos / (float)net.getLayerArr().length;
                    inArr[1] = neuron.errorValue;
                    inArr[2] = neuron.outputValue;

                    final float[] outArr = new float[(trainer.trainSize + 1) * 2];

                    for (int synapsePos = 0; synapsePos < neuron.synapseList.size(); synapsePos++) {
                        final MlpSynapse synapse = neuron.synapseList.get(synapsePos);

                        inArr[3 + synapsePos * 3 + 2] = synapse.getInput().getInputValue();

                        outArr[0] = synapse.weight;
                        outArr[1] = Objects.nonNull(synapse.getInputError()) ? synapse.getInputError().getErrorValue() : 0.0F;
                    }

                    train(trainer.trainNet, inArr, outArr, learningRate, momentum);
                }
            }
        }
        return mainOutputMseErrorValue / mainOutputCount;
    }
}
