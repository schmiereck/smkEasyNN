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
            final MlpLayerConfig layerConfig = layerConfigArr[layerPos];
            final int layerSize = layerConfig.getSize();
            final boolean isOutputLayer = layerPos == (layerConfigArr.length - 1);

            if (layerConfig.getIsArray()) {
                layerArr[layerPos] = createArrayLayer(isOutputLayer, allInputLayerSize, layerSize, layerPos, inputLayerPos, inputLayerSize,
                        layerArr, valueInputArr, biasInput, clockInput,
                        config, rnd);
            } else {
                layerArr[layerPos] = createFlatLayer(isOutputLayer, allInputLayerSize, layerSize, layerPos, inputLayerPos, inputLayerSize,
                        layerArr, valueInputArr, biasInput, clockInput,
                        config, rnd);
            }
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
                addInputSynapse(layerPos, neuron, inputLayer, inputLayerNeuronPos, valueInputArr);
            }
            addAdditionalSynapse(neuron, biasInput, clockInput, config);
        }
        mlpLayer.initWeights2(config.initialWeightValue, rnd);
        mlpLayer.setOutputLayer(isOutputLayer);

        return mlpLayer;
    }

    static MlpLayer createArrayLayer(final boolean isOutputLayer, final int allInputLayerSize, final int layerSize,
                                     final int layerPos, final int inputLayerPos, final int inputLayerSize,
                                     final MlpLayer[] layers, final MlpValueInput[] valueInputArr, final MlpInputInterface biasInput, final MlpInputInterface clockInput,
                                     final MlpConfiguration config, final Random rnd) {
        final MlpLayer mlpLayer = new MlpLayer(allInputLayerSize, layerSize);
        final int xCellSize = 1;
        final int yCellSize = 1;
        final int xArraySize = 4;
        final int yArraySize = 3;

        final MlpLayer inputLayer = layers[inputLayerPos];

        for (int yNeuronPos = 0; yNeuronPos < yArraySize; yNeuronPos++) {
            for (int xNeuronPos = 0; xNeuronPos < xArraySize; xNeuronPos++) {
                final int neuronPos = (yNeuronPos * yArraySize) + xNeuronPos;
                final MlpNeuron neuron = mlpLayer.neuronArr[neuronPos];

                final int xMinPos = Math.max(xNeuronPos - xCellSize, 0);
                final int xMaxPos = Math.min(xNeuronPos + xCellSize, xArraySize - 1);
                final int yMinPos = Math.max(yNeuronPos - yCellSize, 0);
                final int yMaxPos = Math.min(yNeuronPos + yCellSize, yArraySize - 1);

                for (int yPos = yMinPos; yPos <= yMaxPos; yPos++) {
                    for (int xPos = xMinPos; xPos <= xMaxPos; xPos++) {
                        final int inputLayerNeuronPos = ((yPos) * yArraySize) + (xPos);
                        addInputSynapse(layerPos, neuron, inputLayer, inputLayerNeuronPos, valueInputArr);
                    }
                }
                addAdditionalSynapse(neuron, biasInput, clockInput, config);
            }
        }
        mlpLayer.initWeights2(config.initialWeightValue, rnd);
        mlpLayer.setOutputLayer(isOutputLayer);

        return mlpLayer;
    }

    private static void addInputSynapse(final int layerPos, final MlpNeuron neuron, final MlpLayer inputLayer, final int inputLayerNeuronPos, final MlpValueInput[] valueInputArr) {
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

    private static void addAdditionalSynapse(final MlpNeuron neuron, final MlpInputInterface biasInput, final MlpInputInterface clockInput, final MlpConfiguration config) {
        if (config.useAdditionalBiasInput) {
            final MlpSynapse synapse = new MlpSynapse(biasInput, null, false);
            neuron.synapseList.add(synapse);
        }
        if (config.useAdditionalClockInput) {
            final MlpSynapse synapse = new MlpSynapse(clockInput, null, false);
            neuron.synapseList.add(synapse);
        }
    }
}
