package de.schmiereck.smkEasyNN.mlp;

import java.util.Random;

public class MlpLayerService {

    static MlpLayer[] createLayers(final MlpLayerConfig[] layerConfigArr,
                                   final MlpValueInput[] valueInputArr, final MlpInputInterface biasInput, final MlpInputInterface clockInput,
                                   final MlpConfiguration config, final Random rnd) {
        final MlpLayer[] layerArr = new MlpLayer[layerConfigArr.length];

        for (int layerPos = 0; layerPos < layerConfigArr.length; layerPos++) {
            final int inputLayerPos = (layerPos == 0 ? layerPos : layerPos - 1);
            final int inputLayerSize = layerConfigArr[inputLayerPos].getSize();
            final int additionalBiasInputSize = (config.useAdditionalBiasInput ? 1 : 0);
            final int additionalClockInputSize = (config.useAdditionalClockInput ? 1 : 0);
            final int allInputLayerSize = (inputLayerSize + additionalBiasInputSize + additionalClockInputSize);
            final int layerSize = layerConfigArr[layerPos].getSize();
            final boolean isOutputLayer = layerPos == (layerConfigArr.length - 1);

            layerArr[layerPos] = createFlatLayer(isOutputLayer, allInputLayerSize, layerSize, layerPos, inputLayerPos, inputLayerSize,
                    layerArr, valueInputArr, biasInput, clockInput,
                    config, rnd);
        }
        return layerArr;
    }

    static MlpLayer createFlatLayer(final boolean isOutputLayer, final int allInputLayerSize, final int layerSize,
                                    final int layerPos, final int inputLayerPos, final int inputLayerSize,
                                    final MlpLayer[] layers, final MlpValueInput[] valueInputArr, final MlpInputInterface biasInput, final MlpInputInterface clockInput,
                                    final MlpConfiguration config, final Random rnd) {
        final MlpLayer mlpLayer = new MlpLayer(allInputLayerSize, layerSize);

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
