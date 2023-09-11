package de.schmiereck.smkEasyNN.mlp;

import java.util.Random;

public class MlpLayerService {

    static MlpLayer createFlatLayer(final boolean isOutputLayer, final int allInputLayerSize, final int layerOutputSize,
                                    final int layerPos, final int inputLayerPos, final int inputLayerSize,
                                    final MlpLayer[] layers, final MlpValueInput[] valueInputArr, final MlpInputInterface biasInput, final MlpInputInterface clockInput,
                                    final MlpConfiguration config, final Random rnd) {
        final MlpLayer mlpLayer = new MlpLayer(allInputLayerSize, layerOutputSize, rnd);

        final MlpLayer inputLayer = layers[inputLayerPos];

        for (int neuronPos = 0; neuronPos < mlpLayer.neuronArr.length; neuronPos++) {
            final MlpNeuron neuron = mlpLayer.neuronArr[neuronPos];

            for (int inputLayerNeuronPos = 0; inputLayerNeuronPos < inputLayerSize; inputLayerNeuronPos++) {
                final MlpInputInterface input;
                final MlpInputErrorInterface inputError;
                if (layerPos == 0) {
                    input = valueInputArr[inputLayerNeuronPos];
                    inputError = null;
                } else {
                    final MlpNeuron inputNeuron = inputLayer.neuronArr[inputLayerNeuronPos];
                    input = inputNeuron;
                    inputError = inputNeuron;
                }
                final MlpSynapse synapse = new MlpSynapse(input, inputError, false);
                neuron.synapseList.add(synapse);
            }
            if (config.useAdditionalBiasInput) {
                final MlpSynapse synapse = new MlpSynapse(biasInput, null, false);
                neuron.synapseList.add(synapse);
            }
            if (config.useAdditionalClockInput) {
                final MlpSynapse synapse = new MlpSynapse(clockInput, null, false);
                neuron.synapseList.add(synapse);
            }
        }
        mlpLayer.initWeights2(config.initialWeightValue, rnd);
        mlpLayer.setOutputLayer(isOutputLayer);

        return mlpLayer;
    }
}
