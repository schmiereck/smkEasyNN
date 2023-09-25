package de.schmiereck.smkEasyNN.mlp;

import static de.schmiereck.smkEasyNN.mlp.MlpLayer.calcInitWeight;
import static de.schmiereck.smkEasyNN.mlp.MlpLayer.calcInitWeight2;
import static de.schmiereck.smkEasyNN.mlp.MlpLayer.calcInitWeight3;

import java.util.Arrays;
import java.util.Random;

public final class MlpLayerService {
    private MlpLayerService() {}

    static MlpLayer[] createLayers(final MlpLayerConfig[] layerConfigArr,
                                   final MlpValueInput[] valueInputArr, final MlpInputInterface biasInput, final MlpInputInterface clockInput,
                                   final MlpConfiguration config, final boolean useError, final Random rnd) {
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
                layerArr[layerPos] = createArrayLayer(layerPos, isOutputLayer, allInputLayerSize, layerSize, layerPos, inputLayerPos,
                        layerArr, valueInputArr, biasInput, clockInput,
                        config, useError, rnd);
            } else {
                layerArr[layerPos] = createFlatLayer(layerPos, isOutputLayer, allInputLayerSize, layerSize, layerPos, inputLayerPos, inputLayerSize,
                        layerArr, valueInputArr, biasInput, clockInput,
                        config, useError, rnd);
            }
        }
        return layerArr;
    }

    static MlpLayer createFlatLayer(final int layerNr, final boolean isOutputLayer, final int allInputLayerSize, final int layerSize,
                                    final int layerPos, final int inputLayerPos, final int inputLayerSize,
                                    final MlpLayer[] layers, final MlpValueInput[] valueInputArr, final MlpInputInterface biasInput, final MlpInputInterface clockInput,
                                    final MlpConfiguration config, final boolean useError, final Random rnd) {
        final MlpLayer mlpLayer = new MlpLayer(layerNr, allInputLayerSize, layerSize);

        final MlpLayer inputLayer = layers[inputLayerPos];

        for (int neuronPos = 0; neuronPos < mlpLayer.neuronArr.length; neuronPos++) {
            final MlpNeuron neuron = mlpLayer.neuronArr[neuronPos];

            for (int inputLayerNeuronPos = 0; inputLayerNeuronPos < inputLayerSize; inputLayerNeuronPos++) {
                addInputSynapse(layerPos, neuron, inputLayer, inputLayerNeuronPos, valueInputArr, useError, config.getInitialWeightValue());
            }
            addAdditionalSynapse(neuron, biasInput, clockInput, config, rnd);
        }
        mlpLayer.initWeights2(config.initialWeightValue, rnd);
        mlpLayer.setIsOutputLayer(isOutputLayer);

        return mlpLayer;
    }

    static MlpLayer createArrayLayer(final int layerNr, final boolean isOutputLayer, final int allInputLayerSize, final int layerSize,
                                     final int layerPos, final int inputLayerPos,
                                     final MlpLayer[] layers, final MlpValueInput[] valueInputArr, final MlpInputInterface biasInput, final MlpInputInterface clockInput,
                                     final MlpConfiguration config, final boolean useError, final Random rnd) {
        final MlpLayer mlpLayer = new MlpLayer(layerNr, allInputLayerSize, layerSize);
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
                        addInputSynapse(layerPos, neuron, inputLayer, inputLayerNeuronPos, valueInputArr, useError, config.getInitialWeightValue());
                    }
                }
                addAdditionalSynapse(neuron, biasInput, clockInput, config, rnd);
            }
        }
        mlpLayer.initWeights2(config.initialWeightValue, rnd);
        mlpLayer.setIsOutputLayer(isOutputLayer);

        return mlpLayer;
    }

    private static void addInputSynapse(final int layerPos, final MlpNeuron neuron,
                                        final MlpLayer inputLayer, final int inputLayerNeuronPos, final MlpValueInput[] valueInputArr,
                                        final boolean useError, final float initialWeightValue) {
        final MlpInputInterface input;
        final MlpInputErrorInterface inputError;
        if (layerPos == 0) {
            input = valueInputArr[inputLayerNeuronPos];
            inputError = null;
        } else {
            final MlpNeuron inputNeuron = inputLayer.neuronArr[inputLayerNeuronPos];
            input = inputNeuron;
            if (useError) {
                inputError = inputNeuron;
            } else {
                inputError = null;
            }
        }
        final MlpSynapse synapse = createSynapse(initialWeightValue, false, input, inputError);
        neuron.synapseList.add(synapse);
    }

    private static void addAdditionalSynapse(final MlpNeuron neuron, final MlpInputInterface biasInput, final MlpInputInterface clockInput,
                                             final MlpConfiguration config, final Random rnd) {
        if (config.useAdditionalBiasInput) {
            final MlpSynapse synapse = new MlpSynapse(biasInput, null, false);
            //synapse.weight = calcInitWeight3(config.getInitialWeightValue(), rnd);
            //synapse.weight = calcInitWeight(config.getInitialBiasWeightValue(), rnd);
            synapse.weight = config.getInitialBiasWeightValue();
            neuron.synapseList.add(synapse);
        }
        if (config.useAdditionalClockInput) {
            final MlpSynapse synapse = new MlpSynapse(clockInput, null, false);
            synapse.weight = calcInitWeight3(config.getInitialWeightValue(), rnd);
            neuron.synapseList.add(synapse);
        }
    }

    public static void addForwwardInputs(final MlpNet mlpNet, final int inputLayerPos, final int toLayerPos, final Random rnd) {
        addForwwardInputs(mlpNet, inputLayerPos, toLayerPos, true, false, true, rnd);
    }

    /**
     * RNN
     *
     * Neuronales Netz · Seite · HOOU
     * https://www.hoou.de/projects/neuronale-netze-kurz-erklart/pages/neuronales-netz
     *
     * Rekurrentes neuronales Netz – Wikipedia
     * https://de.wikipedia.org/wiki/Rekurrentes_neuronales_Netz
     *
     * Long Short-Term Memory Units (kurz: LSTMs)
     *
     * Aufbau einer LSTM-Zelle
     * https://www.bigdata-insider.de/was-ist-ein-long-short-term-memory-a-774848/
     *
     * // 0
     * // 1 to    <---,
     * // 2 input ----'
     */
    public static void addForwwardInputs(final MlpNet mlpNet, final int inputLayerPos, final int toLayerPos,
                                         final boolean createManyToMany, final boolean useError, final boolean forward,
                                         final Random rnd) {
        final MlpLayer inputLayer = mlpNet.getLayer(inputLayerPos);
        final MlpLayer toLayer = mlpNet.getLayer(toLayerPos);

        if (createManyToMany) {
            Arrays.stream(toLayer.neuronArr).forEach(toNeuron -> {
                Arrays.stream(inputLayer.neuronArr).forEach(inputNeuron -> {
                    final MlpSynapse synapse = createSynapse(calcInitWeight2(mlpNet.getInitialWeightValue(), rnd), useError, forward, inputNeuron, rnd);
                    toNeuron.synapseList.add(synapse);
                });
                if (mlpNet.getUseAdditionalBiasInput()) {
                    final MlpSynapse synapse = new MlpSynapse(mlpNet.biasInput, null, forward);
                    synapse.weight = calcInitWeight2(mlpNet.getInitialWeightValue(), rnd);
                    toNeuron.synapseList.add(synapse);
                }
            });
        } else {
            for (int toNeuronPos = 0; toNeuronPos < toLayer.neuronArr.length; toNeuronPos++) {
                final MlpNeuron toNeuron = toLayer.neuronArr[toNeuronPos];

                final int inputNeuronPos = (toNeuronPos * inputLayer.neuronArr.length) / toLayer.neuronArr.length;
                final MlpNeuron inputNeuron = inputLayer.neuronArr[inputNeuronPos];

                final MlpSynapse synapse = createSynapse(calcInitWeight2(mlpNet.getInitialWeightValue(), rnd), useError, forward, inputNeuron, rnd);
                toNeuron.synapseList.add(synapse);
            }
        }
    }

    public static void addShortTermMemoryInputs(final MlpNet mlpNet,
                                                final int layerPos, final int firstNeuronPos, final int lastNeuronPos,
                                                final boolean useError, final boolean forward,
                                                final Random rnd) {
        final MlpLayer layer = mlpNet.getLayer(layerPos);

        for (int toNeuronPos = firstNeuronPos + 1; toNeuronPos < lastNeuronPos; toNeuronPos++) {
            final MlpNeuron toNeuron = layer.neuronArr[toNeuronPos];

            final int inputNeuronPos = toNeuronPos - 1;
            final MlpNeuron inputNeuron = layer.neuronArr[inputNeuronPos];

            final MlpSynapse synapse = createSynapse(calcInitWeight2(mlpNet.getInitialWeightValue(), rnd), useError, forward, inputNeuron, rnd);
            toNeuron.synapseList.add(synapse);
        }
    }

    private static MlpSynapse createSynapse(final float initWeight, final boolean useError, final boolean forward, final MlpNeuron inputNeuron, final Random rnd) {
        final MlpInputInterface input = inputNeuron;
        final MlpInputErrorInterface inputError;
        if (useError) {
            inputError = inputNeuron;
        } else {
            inputError = null;
        }
        return createSynapse(initWeight, forward, input, inputError);
    }

    private static MlpSynapse createSynapse(final float initWeight, final boolean forward,
                                            final MlpInputInterface input, final MlpInputErrorInterface inputError) {
        final MlpSynapse synapse = new MlpSynapse(input, inputError, forward);
        synapse.weight = initWeight;
        return synapse;
    }

    public static void addInternalInputs(final MlpNet mlpNet, final int layerPos,
                                         final boolean createManyToMany, final boolean useError, final boolean forward,
                                         final Random rnd) {
        final MlpLayer toLayer = mlpNet.getLayer(layerPos);

        for (int toNeuronPos = 0; toNeuronPos < toLayer.neuronArr.length; toNeuronPos++) {
            final MlpNeuron toNeuron = toLayer.neuronArr[toNeuronPos];

            for (int inputNeuronPos = toNeuronPos; inputNeuronPos < toLayer.neuronArr.length; inputNeuronPos++) {
                final MlpNeuron inputNeuron = toLayer.neuronArr[inputNeuronPos];

                final MlpSynapse synapse = createSynapse(calcInitWeight2(mlpNet.getInitialWeightValue(), rnd), useError, forward, inputNeuron, rnd);
                toNeuron.synapseList.add(synapse);
            }
        }
    }
}
