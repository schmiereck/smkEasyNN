package de.schmiereck.smkEasyNN.mlp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MlpNet {
    MlpLayer[] layers;

    final MlpNeuron[] biasNeuronArr;
    private final MlpValueInput[] valueInputArr;

    public MlpNet(int[] layersSize, final boolean useAdditionalBiasInput, final Random rnd) {
        this.layers = new MlpLayer[layersSize.length];
        this.biasNeuronArr = new MlpNeuron[layersSize.length];
        for (int biasNeuronPos = 0; biasNeuronPos < layersSize.length; biasNeuronPos++) {
            this.biasNeuronArr[biasNeuronPos] = new MlpNeuron(0);
            this.biasNeuronArr[biasNeuronPos].output = MlpService.BIAS_VALUE;
        }

        this.valueInputArr = new MlpValueInput[layersSize[0]];
        for (int neuronPos = 0; neuronPos < this.valueInputArr.length; neuronPos++) {
            this.valueInputArr[neuronPos] = new MlpValueInput(0.0F);
        }

        for (int layerPos = 0; layerPos < layersSize.length; layerPos++) {
            final int sizeInputLayerPos = (layerPos == 0 ? layerPos : layerPos - 1);
            final int inputLayerSize = layersSize[sizeInputLayerPos];
            int allInputLayerSize = (useAdditionalBiasInput ? inputLayerSize + 1 : inputLayerSize);
            int layerOutputSize = layersSize[layerPos];

            final List<MlpInputInterface> inputArr = new ArrayList<>(Collections.nCopies(allInputLayerSize, null));

            final MlpLayer mlpLayer = new MlpLayer(inputArr, layerOutputSize, rnd);
            this.layers[layerPos] = mlpLayer;

            final MlpLayer inputLayer = this.layers[sizeInputLayerPos];

            for (int neuronPos = 0; neuronPos < inputLayerSize; neuronPos++) {
                if (layerPos == 0) {
                    inputArr.set(neuronPos, this.valueInputArr[neuronPos]);
                } else {
                    inputArr.set(neuronPos, new MlpInput(this.layers[sizeInputLayerPos].neuronArr[neuronPos]));
                }
            }
            if (useAdditionalBiasInput) {
                inputArr.set(allInputLayerSize - 1, new MlpInput(this.biasNeuronArr[sizeInputLayerPos]));
            }

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
                if (useAdditionalBiasInput) {
                    final MlpSynapse synapse = new MlpSynapse();
                    synapse.input = new MlpInput(this.biasNeuronArr[layerPos]);
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

    public MlpLayer getLayer(final int layerPos) {
        return this.layers[layerPos];
    }

    public MlpLayer getOutputLayer() {
        return this.layers[this.layers.length - 1];
    }
}
