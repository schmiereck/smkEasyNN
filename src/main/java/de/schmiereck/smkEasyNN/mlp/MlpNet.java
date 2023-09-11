package de.schmiereck.smkEasyNN.mlp;

import java.util.Random;

public class MlpNet {
    MlpLayer[] layers;
    final MlpInputInterface[] biasInputArr;
    final MlpInputInterface clockInput;
    private final MlpValueInput[] valueInputArr;

    private final MlpConfiguration config;

    public MlpNet(final int[] layersSize, final Random rnd) {
        this(layersSize, false, false, rnd);
    }

    public MlpNet(final int[] layersSize, final boolean useAdditionalBiasInput, final Random rnd) {
        this(layersSize, useAdditionalBiasInput, false, rnd);
    }

    public MlpNet(final int[] layersSize, final boolean useAdditionalBiasInput, final boolean useAdditionalClockInput, final Random rnd) {
        this(new MlpConfiguration(useAdditionalBiasInput, useAdditionalClockInput), layersSize, rnd);
    }

    public MlpNet(final MlpConfiguration config, int[] layersSize, final Random rnd) {
        this.config = config;
        this.layers = new MlpLayer[layersSize.length];
        this.biasInputArr = new MlpValueInput[layersSize.length];
        for (int biasNeuronPos = 0; biasNeuronPos < layersSize.length; biasNeuronPos++) {
            this.biasInputArr[biasNeuronPos] = new MlpValueInput(MlpService.BIAS_VALUE);
        }
        this.clockInput = new MlpValueInput(MlpService.CLOCK_VALUE);

        this.valueInputArr = new MlpValueInput[layersSize[0]];
        for (int neuronPos = 0; neuronPos < this.valueInputArr.length; neuronPos++) {
            this.valueInputArr[neuronPos] = new MlpValueInput(0.0F);
        }

        for (int layerPos = 0; layerPos < layersSize.length; layerPos++) {
            final int inputLayerPos = (layerPos == 0 ? layerPos : layerPos - 1);
            final int inputLayerSize = layersSize[inputLayerPos];
            final int additionalBiasInputSize = (this.config.useAdditionalBiasInput ? 1 : 0);
            final int additionalClockInputSize = (this.config.useAdditionalClockInput ? 1 : 0);
            final int allInputLayerSize = (inputLayerSize + additionalBiasInputSize + additionalClockInputSize);
            final int layerOutputSize = layersSize[layerPos];
            final boolean isOutputLayer = layerPos == (layersSize.length - 1);

            this.layers[layerPos] = createFlatLayer(isOutputLayer, allInputLayerSize, layerOutputSize, layerPos, inputLayerPos, inputLayerSize,
                    this.layers, this.valueInputArr, this.biasInputArr, this.clockInput,
                    this.config, rnd);
        }
    }

    private static MlpLayer createFlatLayer(final boolean isOutputLayer, final int allInputLayerSize, final int layerOutputSize,
                                            final int layerPos, final int inputLayerPos, final int inputLayerSize,
                                            final MlpLayer[] layers, final MlpValueInput[] valueInputArr, final MlpInputInterface[] biasInputArr, final MlpInputInterface clockInput,
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
                final MlpInputInterface biasInput = biasInputArr[layerPos];
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

    public void setInputValue(final int inputPos, final float inputValue) {
        this.valueInputArr[inputPos].setValue(inputValue);
    }

    public MlpValueInput[] getValueInputArr() {
        return this.valueInputArr;
    }

    public MlpLayer getLayer(final int layerPos) {
        return this.layers[layerPos];
    }

    public MlpLayer getOutputLayer() {
        return this.layers[this.layers.length - 1];
    }

    public boolean getUseAdditionalBiasInput() {
        return this.config.useAdditionalBiasInput;
    }

    public boolean getUseAdditionalClockInput() {
        return this.config.useAdditionalClockInput;
    }

    public float getInitialWeightValue() {
        return this.config.initialWeightValue;
    }
}
