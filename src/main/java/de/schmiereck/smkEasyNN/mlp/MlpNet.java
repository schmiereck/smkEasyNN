package de.schmiereck.smkEasyNN.mlp;

import java.util.Random;

public class MlpNet {
    MlpLayer[] layers;

    final MlpNeuron[] biasNeuronArr;
    final MlpNeuron clockNeuron;
    private final MlpValueInput[] valueInputArr;

    private boolean useAdditionalBiasInput;
    private boolean useAdditionalClockInput;

    public MlpNet(int[] layersSize, final Random rnd) {
        this(layersSize, false, false, rnd);
    }

    public MlpNet(int[] layersSize, final boolean useAdditionalBiasInput, final Random rnd) {
        this(layersSize, useAdditionalBiasInput, false, rnd);
    }

    public MlpNet(int[] layersSize, final boolean useAdditionalBiasInput, final boolean useAdditionalClockInput, final Random rnd) {
        this.layers = new MlpLayer[layersSize.length];
        this.useAdditionalBiasInput = useAdditionalBiasInput;
        this.useAdditionalClockInput = useAdditionalClockInput;
        this.biasNeuronArr = new MlpNeuron[layersSize.length];
        for (int biasNeuronPos = 0; biasNeuronPos < layersSize.length; biasNeuronPos++) {
            this.biasNeuronArr[biasNeuronPos] = new MlpNeuron(0);
            this.biasNeuronArr[biasNeuronPos].output = MlpService.BIAS_VALUE;
            this.biasNeuronArr[biasNeuronPos].lastOutput = MlpService.BIAS_VALUE;
        }
        this.clockNeuron = new MlpNeuron(0);
        this.clockNeuron.output = MlpService.CLOCK_VALUE;
        this.clockNeuron.lastOutput = MlpService.CLOCK_VALUE;

        this.valueInputArr = new MlpValueInput[layersSize[0]];
        for (int neuronPos = 0; neuronPos < this.valueInputArr.length; neuronPos++) {
            this.valueInputArr[neuronPos] = new MlpValueInput(0.0F);
        }

        for (int layerPos = 0; layerPos < layersSize.length; layerPos++) {
            final int sizeInputLayerPos = (layerPos == 0 ? layerPos : layerPos - 1);
            final int inputLayerSize = layersSize[sizeInputLayerPos];
            final int additionalBiasInputSize = (this.useAdditionalBiasInput ? 1 : 0);
            final int additionalClockInputSize = (this.useAdditionalClockInput ? 1 : 0);
            final int allInputLayerSize = (inputLayerSize + additionalBiasInputSize + additionalClockInputSize);
            final int layerOutputSize = layersSize[layerPos];

            final MlpLayer mlpLayer = new MlpLayer(allInputLayerSize, layerOutputSize, rnd);
            this.layers[layerPos] = mlpLayer;

            final MlpLayer inputLayer = this.layers[sizeInputLayerPos];

            for (int neuronPos = 0; neuronPos < mlpLayer.neuronArr.length; neuronPos++) {
                final MlpNeuron neuron = mlpLayer.neuronArr[neuronPos];

                for (int inputLayerNeuronPos = 0; inputLayerNeuronPos < inputLayerSize; inputLayerNeuronPos++) {
                    final MlpInputInterface input;
                    if (layerPos == 0) {
                        input = this.valueInputArr[inputLayerNeuronPos];
                    } else {
                        final MlpNeuron inputNeuron = inputLayer.neuronArr[inputLayerNeuronPos];
                        input = new MlpInput(inputNeuron);
                    }
                    final MlpSynapse synapse = new MlpSynapse();
                    synapse.input = input;
                    neuron.synapseList.add(synapse);
                }
                if (this.useAdditionalBiasInput) {
                    final MlpSynapse synapse = new MlpSynapse();
                    synapse.input = new MlpInput(this.biasNeuronArr[layerPos]);
                    neuron.synapseList.add(synapse);
                }
                if (this.useAdditionalClockInput) {
                    final MlpSynapse synapse = new MlpSynapse();
                    synapse.input = new MlpInput(this.clockNeuron);
                    neuron.synapseList.add(synapse);
                }
            }
            mlpLayer.initWeights2(rnd);

            if (layerPos == (layersSize.length - 1)) {
                mlpLayer.setOutputLayer(true);
            }
        }
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
        return this.useAdditionalBiasInput;
    }

    public boolean getUseAdditionalClockInput() {
        return this.useAdditionalClockInput;
    }
}
